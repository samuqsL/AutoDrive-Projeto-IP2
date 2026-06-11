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

    // =========================================================================
    // CORREÇÃO: Fluxo de abertura usando a assinatura real '.salvar(novaOS)'
    // =========================================================================
    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            OrdemServico novaOS = new OrdemServico();
            novaOS.setCliente(cliente);
            novaOS.setVeiculo(veiculo);
            
            // Procura se existe algum mecânico totalmente disponível na oficina
            Mecanico mecanicoLivre = null;
            for (Mecanico m : repoMecanicos.listarTodos()) {
                if (m.isDisponivel()) {
                    mecanicoLivre = m;
                    break;
                }
            }

            // Se achou mecânico livre, vincula imediatamente e muda o status da OS
            if (mecanicoLivre != null) {
                mecanicoLivre.setDisponivel(false); // Ocupa o mecânico
                novaOS.setMecanico(mecanicoLivre);
                novaOS.setStatus(StatusOS.PROCESSO_MANUTENCAO);
                
                // Atualiza o estado do mecânico no repositório persistente sem duplicar
                repoMecanicos.removerMecanico(mecanicoLivre.getNome());
                repoMecanicos.adicionarMecanico(mecanicoLivre);
            } else {
                // Se não há mecânicos, ela nasce puramente na fila de espera (ABERTA)
                novaOS.setStatus(StatusOS.ABERTA);
                novaOS.setMecanico(null);
            }

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
            
            // 2. REQUISITO OBRIGATÓRIO: Injeta os R$ 120.00 fixos do óleo padrão da oficina
            os.setValorTotal(somaItens + 120.0);
            
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
