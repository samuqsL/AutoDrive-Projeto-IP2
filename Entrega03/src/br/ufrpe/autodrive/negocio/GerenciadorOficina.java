package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.List;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 
    private IRepositorioMecanicos repoMecanicos; // Correção: uso do repositório em vez de variáveis soltas

    // Construtor atualizado recebendo as interfaces da camada de dados
    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, IRepositorioMecanicos repoMecanicos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.repoMecanicos = repoMecanicos;
        
        // Sempre que o sistema inicia, ele roda uma verificação automática caso existam OS salvas
        verificarEProcessarFila();
    }

    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            OrdemServico novaOS = new OrdemServico(cliente, veiculo);
            repoOS.salvar(novaOS);
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    // Algoritmo de gerenciamento da Fila por Ordem de Chegada utilizando o Repositório de Mecânicos
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                // Busca no repositório algum mecânico que esteja livre
                Mecanico mecanicoDisponivel = null;
                List<Mecanico> todosMecanicos = repoMecanicos.listarTodos();
                
                for (Mecanico m : todosMecanicos) {
                    if (m.isDisponivel()) {
                        mecanicoDisponivel = m;
                        break; 
                    }
                }
                
                if (mecanicoDisponivel != null) {
                    alocarMecanicoNaOS(os, mecanicoDisponivel);
                } else {
                    // Todos ocupados, interrompe e espera alguém finalizar
                    break; 
                }
            }
        }
    }

    private void alocarMecanicoNaOS(OrdemServico os, Mecanico mecanico) {
        os.setMecanico(mecanico);
        os.setStatus(StatusOS.PROCESSO_MANUTENCAO); 
        mecanico.setDisponivel(false); 
        
        if (os.getVeiculo() != null) {
            os.getVeiculo().setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
        repoOS.salvar(os);
    }

    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            os.marcarComoPago();
            
            if (os.getListaPecas().isEmpty()) {
                Pecas pecaObrigatoria = new Pecas();
                pecaObrigatoria.setNome("oleo"); 
                pecaObrigatoria.setPreco(120.00); 
                pecaObrigatoria.setQuantidade(1);
                os.getListaPecas().add(pecaObrigatoria);
            }
            
            os.calcularTotal(); 
            os.finalizarOS(); 
            
            if (os.getVeiculo() != null) {
                os.getVeiculo().setStatus(StatusVeiculo.ESTOQUE); 
            }
            
            Mecanico mecanicoAtribuido = os.getMecanico();
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); 
                mecanicoAtribuido.setDisponivel(true);        
            }
            
            repoOS.salvar(os);
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    @Override
    public boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            boolean adicionou = os.adicionarPeca(peca, quantidade);
            if (adicionou) repoOS.salvar(os);
            return adicionou;
        }
        return false;
    }

    @Override
    public boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            // CORREÇÃO: Antes estava dando erro com `this.listaServicos`
            os.getListaServicos().add(servico);
            repoOS.salvar(os);
            return true;
        }
        return false;
    }

    public List<OrdemServico> listarTodosServicos() {
        return this.repoOS.listarTodas(); 
    }
}
