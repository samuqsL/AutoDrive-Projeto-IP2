package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.List;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 

    // FUNÇÃO LOCALIZADA: Referências fixas para gerenciar a disponibilidade dos dois mecânicos
    private Mecanico mario;
    private Mecanico luigi;

    // Construtor atualizado recebendo os mecânicos individuais instanciados na Main
    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, Mecanico mario, Mecanico luigi) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.mario = mario;
        this.luigi = luigi;
        
        // Sempre que o sistema inicia, ele roda uma verificação automática caso existam OS salvas
        verificarEProcessarFila();
    }

    // FUNÇÃO LOCALIZADA: Abertura simplificada de OS enviando direto para a fila de espera (ABERTA)
    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            // Instancia a Ordem de Serviço (Gera número e data automaticamente internamente)
            OrdemServico novaOS = new OrdemServico(cliente, veiculo);
            
            // Salva no repositório persistente como status ABERTA (na fila)
            repoOS.salvar(novaOS);
            
            // Invoca o algoritmo da fila para tentar alocar um mecânico disponível imediatamente
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    // FUNÇÃO LOCALIZADA: Algoritmo de gerenciamento da Fila por Ordem de Chegada (FIFO)
    // Se ambos estiverem disponíveis, sorteia um de forma aleatória.
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        // Varre a lista na ordem em que foram salvas (ordem cronológica de abertura)
        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                // CASO 1: Dois mecânicos entram simultaneamente em disponível (Escolha Aleatória)
                if (mario.isDisponivel() && luigi.isDisponivel()) {
                    if (Math.random() < 0.5) {
                        alocarMecanicoNaOS(os, mario);
                    } else {
                        alocarMecanicoNaOS(os, luigi);
                    }
                } 
                // CASO 2: Apenas o Mario está disponível
                else if (mario.isDisponivel()) {
                    alocarMecanicoNaOS(os, mario);
                } 
                // CASO 3: Apenas o Luigi está disponível
                else if (luigi.isDisponivel()) {
                    alocarMecanicoNaOS(os, luigi);
                } 
                // CASO 4: Todos ocupados, a OS permanece em ABERTA esperando na fila
                else {
                    break; 
                }
            }
        }
    }

    // Método auxiliar privado para amarrar o mecânico à OS e travar sua disponibilidade
    private void alocarMecanicoNaOS(OrdemServico os, Mecanico mecanico) {
        os.setMecanico(mecanico);
        os.setStatus(StatusOS.PROCESSO_MANUTENCAO); // Muda o status para Manutenção ativa
        mecanico.setDisponivel(false); // O mecânico agora está ocupado
        
        if (os.getVeiculo() != null) {
            os.getVeiculo().setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
        repoOS.salvar(os);
    }

    // FUNÇÃO LOCALIZADA: Finalização da OS incrementando +1 na produtividade do mecânico alocado
    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        // Restrito apenas a quem está sob PROCESSO_MANUTENCAO
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            os.marcarComoPago();
            
            // Inserção padrão do óleo caso esteja vazia conforme seu requisito obrigatório anterior
            if (os.getListaPecas().isEmpty()) {
                Pecas pecaObrigatoria = new Pecas();
                pecaObrigatoria.setNome("oleo"); 
                pecaObrigatoria.setPreco(120.00); 
                pecaObrigatoria.setQuantidade(1);
                os.getListaPecas().add(pecaObrigatoria);
            }
            
            os.calcularTotal(); 
            os.finalizarOS(); // Muda para FINALIZADA e bota data de fechamento
            
            if (os.getVeiculo() != null) {
                os.getVeiculo().setStatus(StatusVeiculo.ESTOQUE); // Libera o veículo
            }
            
            // FUNÇÃO LOCALIZADA: Captura o mecânico da OS, soma +1 à produtividade dele e o libera
            Mecanico mecanicoAtribuido = os.getMecanico();
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); // Adiciona +1 à produtividade individual
                mecanicoAtribuido.setDisponivel(true);        // Fica disponível novamente
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
            this.listaServicos.add(servico);
            return true;
        }
        return false;
    }
}
