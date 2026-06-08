package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 
    
    // CORREÇÃO: Utilizando Repositório em vez de atributos fixos mario/luigi
    private IRepositorioMecanicos repoMecanicos; 

    // Construtor atualizado recebendo os repositórios
    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, IRepositorioMecanicos repoMecanicos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.repoMecanicos = repoMecanicos;
        
        // Sempre que o sistema inicia, ele roda uma verificação automática caso existam OS salvas
        verificarEProcessarFila();
    }

    // FUNÇÃO LOCALIZADA: Abertura simplificada de OS enviando direto para a fila de espera (ABERTA)
    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            OrdemServico novaOS = new OrdemServico();
            novaOS.setCliente(cliente);
            novaOS.setVeiculo(veiculo);
            
            repoOS.salvar(novaOS);
            
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    // FUNÇÃO LOCALIZADA: Algoritmo de gerenciamento da Fila por Ordem de Chegada (FIFO)
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                // Busca os mecânicos pelo repositório
                List<Mecanico> mecanicos = repoMecanicos.listarTodos();
                List<Mecanico> disponiveis = new ArrayList<>();
                for (Mecanico m : mecanicos) {
                    if (m.isDisponivel()) {
                        disponiveis.add(m);
                    }
                }
                
                if (!disponiveis.isEmpty()) {
                    int indexAleatorio = (int) (Math.random() * disponiveis.size());
                    Mecanico escolhido = disponiveis.get(indexAleatorio);
                    alocarMecanicoNaOS(os, escolhido);
                } else {
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

    // FUNÇÃO LOCALIZADA: Finalização da OS incrementando +1 na produtividade do mecânico alocado
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
            boolean adicionado = os.adicionarPeca(peca, quantidade);
            if(adicionado) repoOS.salvar(os);
            return adicionado;
        }
        return false;
    }

    @Override
    public boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            // CORREÇÃO: Usando a lista da própria OS
            os.getListaServicos().add(servico);
            repoOS.salvar(os); 
            return true;
        }
        return false;
    }
}
