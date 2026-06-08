package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 
    private IRepositorioMecanicos repoMecanicos; // 🟢 ALTERAÇÃO: Uso correto da camada de dados

    // Construtor desacoplado recebendo apenas os repositórios necessários
    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, IRepositorioMecanicos repoMecanicos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.repoMecanicos = repoMecanicos;
        
        // Sempre que o sistema inicia, ele roda uma verificação automática caso existam OS salvas
        verificarEProcessarFila();
    }

    // 🟢 ASSINATURA CORRIGIDA: Sincronizada com os parâmetros enviados pela TelaOficina
    @Override
    public boolean abrirOS(int numero, String data, String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            OrdemServico novaOS = new OrdemServico(cliente, veiculo);
            novaOS.setNumero(numero);
            novaOS.setDataAbertura(data);
            
            // Salva no repositório persistente como status ABERTA
            repoOS.salvar(novaOS);
            
            // Invoca o algoritmo da fila para tentar alocar um mecânico disponível imediatamente
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    // 🟢 LOGICA REFEITA: Gerenciamento dinâmico baseado em qualquer mecânico cadastrado no repositório
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        List<Mecanico> todosOsMecanicos = repoMecanicos.listarTodos();

        // Varre a lista na ordem cronológica de abertura (FIFO)
        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                // Filtra mecânicos disponíveis dinamicamente
                List<Mecanico> disponiveis = new ArrayList<>();
                for (Mecanico m : todosOsMecanicos) {
                    if (m.isDisponivel()) {
                        disponiveis.add(m);
                    }
                }

                // Se todos estiverem ocupados, a OS permanece na fila aguardando liberação
                if (disponiveis.isEmpty()) {
                    break; 
                }

                Mecanico escolhido;
                // Mantida a regra de sorteio caso mais de um mecânico esteja livre ao mesmo tempo
                if (disponiveis.size() > 1) {
                    int index = (int) (Math.random() * disponiveis.size());
                    escolhido = disponiveis.get(index);
                } else {
                    escolhido = disponiveis.get(0);
                }

                alocarMecanicoNaOS(os, escolhido);
            }
        }
    }

    // Método auxiliar privado para amarrar o mecânico à OS e persistir nos repositórios
    private void alocarMecanicoNaOS(OrdemServico os, Mecanico mecanico) {
        os.setMecanico(mecanico);
        os.setStatus(StatusOS.PROCESSO_MANUTENCAO); // Muda o status para Manutenção ativa
        mecanico.setDisponivel(false); // O mecânico agora está ocupado
        
        if (os.getVeiculo() != null) {
            os.getVeiculo().setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
        repoOS.salvar(os);
        repoMecanicos.atualizarMecanico(mecanico); // Salva a alteração de disponibilidade do mecânico no arquivo
    }

    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        // Restrito apenas a quem está sob PROCESSO_MANUTENCAO
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            os.marcarComoPago();
            
            // Inserção padrão do óleo caso esteja vazia
            if (os.getListaPecas().isEmpty()) {
                Pecas pecaObrigatoria = new Pecas();
                pecaObrigatoria.setNome("oleo"); 
                pecaObrigatoria.setPreco(120.00); 
                pecaObrigatoria.setQuantidade(1);
                os.getListaPecas().add(pecaObrigatoria);
            }
            
            os.calcularTotal(); 
            os.finalizarOS(); // Muda para FINALIZADA e coloca data de fechamento
            
            if (os.getVeiculo() != null) {
                os.getVeiculo().setStatus(StatusVeiculo.ESTOQUE); // Libera o veículo
            }
            
            Mecanico mecanicoAtribuido = os.getMecanico();
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); // Adiciona +1 à produtividade individual
                mecanicoAtribuido.setDisponivel(true);        // Fica disponível novamente
                repoMecanicos.atualizarMecanico(mecanicoAtribuido); // Persiste a liberação e contagem no arquivo
            }
            
            repoOS.salvar(os);
            
            // Como um mecânico acabou de ficar livre, processamos a fila para puxar o próximo cliente
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    @Override
    public boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            return os.adicionarPeca(peca, quantidade);
        }
        return false;
    }

    @Override
    public boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            // 🟢 CORREÇÃO: Vincula o serviço na lista própria daquela OS e atualiza o repositório
            os.getListaServicos().add(servico);
            repoOS.salvar(os);
            return true;
        }
        return false;
    }
}
