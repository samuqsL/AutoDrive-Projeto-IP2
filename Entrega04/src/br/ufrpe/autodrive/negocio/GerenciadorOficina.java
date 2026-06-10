package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.List;
import java.util.ArrayList;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 
    private IRepositorioMecanicos repoMecanicos; // 🟢 CORREÇÃO: Substitui as instâncias fixas pela arquitetura correta

    // Construtor atualizado recebendo os repositórios, incluindo o de mecânicos
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
            
            // 🛑 TRAVA CRÍTICA DE REGRA DE NEGÓCIO:
            // O veículo SÓ pode entrar na oficina se ele estiver totalmente DISPONÍVEL.
            // Se estiver em TEST_DRIVE, VENDIDO ou já estiver EM_MANUTENCAO, a OS deve ser negada.
            if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
                return false; 
            }

            // Instancia a Ordem de Serviço (Gera número e data automaticamente internamente)
            OrdemServico novaOS = new OrdemServico(cliente, veiculo);
            
            // Carimba o veículo como EM_MANUTENCAO e grava no repositório persistente
            veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
            repoVeiculos.adicionarVeiculo(veiculo); 
            
            // Salva no repositório persistente como status ABERTA (na fila)
            repoOS.salvar(novaOS);
            
            // Invoca o algoritmo da fila para tentar alocar um mecânico disponível imediatamente
            verificarEProcessarFila();
            return true;
        }
        return false;
    }

    // FUNÇÃO LOCALIZADA: Algoritmo de gerenciamento da Fila por Ordem de Chegada (FIFO)
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        // Varre a lista na ordem em que foram salvas (ordem cronológica de abertura)
        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                // Puxa do repositório a lista atualizada de mecânicos
                List<Mecanico> todosMecanicos = repoMecanicos.listarTodos();
                List<Mecanico> disponiveis = new ArrayList<>();
                
                for (Mecanico m : todosMecanicos) {
                    if (m.isDisponivel()) {
                        disponiveis.add(m);
                    }
                }

                // Se houver pelo menos um mecânico disponível, prossegue com a alocação
                if (!disponiveis.isEmpty()) {
                    Mecanico mecanicoEscolhido;
                    // Se houver mais de um (ex: Mario e Luigi), sorteia de forma aleatória
                    if (disponiveis.size() > 1) {
                        mecanicoEscolhido = disponiveis.get(Math.random() < 0.5 ? 0 : 1);
                    } else {
                        mecanicoEscolhido = disponiveis.get(0);
                    }
                    alocarMecanicoNaOS(os, mecanicoEscolhido);
                } else {
                    // Todos ocupados, a OS permanece em ABERTA esperando na fila
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
        
        // 🟢 CORREÇÃO: Força a atualização do mecânico no repositório persistente
        repoMecanicos.removerMecanico(mecanico.getNome());
        repoMecanicos.adicionarMecanico(mecanico);
        
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
            
            // 🟢 AJUSTE DA VENDA: Devolve o veículo para DISPONIVEL e salva o estado dele no arquivo persistente
            if (os.getVeiculo() != null) {
                os.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL); 
                repoVeiculos.adicionarVeiculo(os.getVeiculo());
            }
            
            // FUNÇÃO LOCALIZADA: Captura o mecânico da OS, soma +1 à produtividade dele e o libera
            Mecanico mecanicoAtribuido = os.getMecanico();
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); // Adiciona +1 à produtividade individual
                mecanicoAtribuido.setDisponivel(true);        // Fica disponível novamente
                
                // Força a atualização do mecânico no repositório persistente
                repoMecanicos.removerMecanico(mecanicoAtribuido.getNome());
                repoMecanicos.adicionarMecanico(mecanicoAtribuido);
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
            // 🟢 CORREÇÃO: Estava "this.listaServicos.add" apontando para variável nula/inexistente
            os.getListaServicos().add(servico);
            repoOS.salvar(os); // Salva as mudanças da OS
            return true;
        }
        return false;
    }
}
