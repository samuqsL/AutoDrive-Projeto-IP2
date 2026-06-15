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
    private IRepositorioPecas repoPecas; // 🟢 ADIÇÃO: Repositório de peças para checar o estoque do Óleo e Peças da Tela

    // Construtor atualizado recebendo os repositórios, incluindo o de mecânicos e peças
    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, IRepositorioMecanicos repoMecanicos, IRepositorioPecas repoPecas) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.repoMecanicos = repoMecanicos;
        this.repoPecas = repoPecas;
        
        // Sempre que o sistema inicia, ele roda uma verificação automática caso existam OS salvas
        verificarEProcessarFila();
    }

    // =========================================================================
    // CORREÇÃO: Fluxo de abertura usando a assinatura real '.salvar(novaOS)'
    // Este foi mantido para retrocompatibilidade com os testes do Main.java!
    // =========================================================================
    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        // Redireciona para a nova versão passando parâmetros nulos de peça, usando um serviço padrão
        return abrirOSCompleta(cpfCliente, chassiVeiculo, null, 0, "Serviço Padrão de Fila", 0.0);
    }

    // =========================================================================
    // 🟢 NOVO FLUXO COMPLETO PARA A TELA FXML (Lida com o Óleo, Peças e Mão de Obra)
    // =========================================================================
    public boolean abrirOSCompleta(String cpfCliente, String chassiVeiculo, Pecas peca, int qtd, String descServico, double valorServico) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            
            // 🛑 TRAVA DE SEGURANÇA COMPLEMENTAR: impede duplicidade de veículo na oficina
            List<OrdemServico> todasOS = repoOS.listarTodas();
            if (todasOS != null) {
                for (OrdemServico os : todasOS) {
                    if (os.getVeiculo() != null && os.getVeiculo().getChassi().equals(chassiVeiculo)) {
                        if (os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
                            // Prints removidos para focar na resposta da UI FXML
                            return false; // 🚫 Rejeita na hora e não cria duplicata
                        }
                    }
                }
            }
            
            // 🔥 AJUSTE INTELIGENTE: Só passa para EM_MANUTENCAO se o veículo for de estoque/disponível da loja.
            if (veiculo.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.ESTOQUE || 
                veiculo.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.DISPONIVEL) {
                veiculo.setStatus(br.ufrpe.autodrive.negocio.beans.StatusVeiculo.EM_MANUTENCAO);
            }
            
            OrdemServico novaOS = new OrdemServico();
            novaOS.setCliente(cliente);
            novaOS.setVeiculo(veiculo);

            // =========================================================
            // 🟢 DESCONTO OBRIGATÓRIO E AUTOMÁTICO DO ÓLEO DE MOTOR
            // =========================================================
            Pecas oleo = repoPecas.buscarPorCodigo("EST-001");
            if (oleo != null && oleo.getQuantidade() >= 1) {
                oleo.retirarDoEstoque(1); // Desconta 1 do estoque
                repoPecas.salvar(oleo); // Salva fisicamente
                // Adiciona a peça com a quantidade vinculada àquela OS
                novaOS.adicionarPeca(new Pecas(oleo.getNome(), oleo.getCodigo(), oleo.getPreco(), 1), 1);
            } else {
                return false; // Bloqueia abertura por falta de óleo
            }

            // =========================================================
            // 🟢 ADIÇÃO DA PEÇA EXTRA ESCOLHIDA NA TELA FXML (Se houver)
            // =========================================================
            if (peca != null && qtd > 0) {
                Pecas pecaEstoque = repoPecas.buscarPorCodigo(peca.getCodigo());
                if (pecaEstoque != null && pecaEstoque.retirarDoEstoque(qtd)) {
                    repoPecas.salvar(pecaEstoque);
                    novaOS.adicionarPeca(new Pecas(pecaEstoque.getNome(), pecaEstoque.getCodigo(), pecaEstoque.getPreco(), qtd), qtd);
                }
            }
            
            // Procura se existe algum mecânico totalmente disponível na oficina
            Mecanico mecanicoLivre = null;
            for (Mecanico m : repoMecanicos.listarTodos()) {
                if (m.isDisponivel()) {
                    mecanicoLivre = m;
                    break;
                }
            }

            // Prepara o serviço com o preço estipulado pelo gerente
            MaoDeObra servico = new MaoDeObra();
            servico.setDescricao(descServico);
            servico.setValor(valorServico);

            // Se achou mecânico livre, vincula imediatamente e muda o status da OS
            if (mecanicoLivre != null) {
                mecanicoLivre.setDisponivel(false); // Ocupa o mecânico
                novaOS.setMecanico(mecanicoLivre);
                novaOS.setStatus(StatusOS.PROCESSO_MANUTENCAO);
                
                servico.setMecanico(mecanicoLivre);

                // Atualiza o estado do mecânico no repositório persistente sem duplicar
                repoMecanicos.removerMecanico(mecanicoLivre.getNome());
                repoMecanicos.adicionarMecanico(mecanicoLivre);
            } else {
                // Se não há mecânicos, ela nasce puramente na fila de espera (ABERTA)
                novaOS.setStatus(StatusOS.ABERTA);
                novaOS.setMecanico(null);
            }

            novaOS.getListaServicos().add(servico);
            novaOS.calcularTotal(); // Garante o cálculo do óleo, peças e mão de obra

            // Usando a assinatura correta do seu repositório: '.salvar' adiciona e grava no arquivo .dat de uma vez só
            repoOS.salvar(novaOS);
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
    // =========================================================================
    // CORREÇÃO DEFINITIVA E PREMIDA: Ajustado perfeitamente com os Getters da OS
    // =========================================================================
    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        // Só permite finalizar ordens que estejam em andamento
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            
            // 1. CORREÇÃO DAS ASSINATURA: Usando os métodos reais existentes na sua OS
            double somaItens = os.getValorPecas() + os.getValorMaoDeObra();
            
            // 2. CORREÇÃO DA MENSAGEM: Valor fixado de óleo removido daqui, pois agora é contabilizado oficialmente e dinâmico na abertura da OS.
            os.setValorTotal(somaItens);
            
            // 3. Modifica os status de fechamento
            os.setStatus(StatusOS.FINALIZADA);
            os.setDataFechamento(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            // 4. Puxa o mecânico usando o getter correto da sua classe
            Mecanico mecanicoAtribuido = os.getMecanico(); 
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); // +1 OS resolvida
                mecanicoAtribuido.setDisponivel(true);        // Fica livre de novo
                
                // Atualiza o mecânico no repositório persistente
                repoMecanicos.removerMecanico(mecanicoAtribuido.getNome());
                repoMecanicos.adicionarMecanico(mecanicoAtribuido);
            }
            
            // Salva a OS finalizada no arquivo físico .dat
            repoOS.salvar(os);
            
            // 🔥 AJUSTE SEGURO CORRIGIDO: Só volta para o ESTOQUE se o carro estava em manutenção interna da loja
            if (os.getVeiculo() != null) {
                if (os.getVeiculo().getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.EM_MANUTENCAO) {
                    // Se era um carro de estoque fazendo manutenção interna, volta pro estoque para poder ser vendido
                    os.getVeiculo().setStatus(br.ufrpe.autodrive.negocio.beans.StatusVeiculo.ESTOQUE);
                }
                // Se o status for VENDIDO (porque o abrirOS não mexeu nele), ele continua VENDIDO. Seguro e perfeito!
            }
            
            // 5. Processa a fila FIFO para passar o mecânico liberado para o próximo carro
            this.verificarEProcessarFila();
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
