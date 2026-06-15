package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.List;
import java.util.ArrayList;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; 
    private IRepositorioVeiculos repoVeiculos; 
    private IRepositorioMecanicos repoMecanicos; 

    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos, IRepositorioMecanicos repoMecanicos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
        this.repoMecanicos = repoMecanicos;
        
        verificarEProcessarFila();
    }

    @Override
    public boolean abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            
            List<OrdemServico> todasOS = repoOS.listarTodas();
            if (todasOS != null) {
                for (OrdemServico os : todasOS) {
                    if (os.getVeiculo() != null && os.getVeiculo().getChassi().equals(chassiVeiculo)) {
                        if (os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
                            return false; 
                        }
                    }
                }
            }
            
            if (veiculo.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.ESTOQUE || 
                veiculo.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.DISPONIVEL) {
                veiculo.setStatus(br.ufrpe.autodrive.negocio.beans.StatusVeiculo.EM_MANUTENCAO);
            }
            
            OrdemServico novaOS = new OrdemServico();
            novaOS.setCliente(cliente);
            novaOS.setVeiculo(veiculo);
            
            Mecanico mecanicoLivre = null;
            for (Mecanico m : repoMecanicos.listarTodos()) {
                if (m.isDisponivel()) {
                    mecanicoLivre = m;
                    break;
                }
            }

            if (mecanicoLivre != null) {
                mecanicoLivre.setDisponivel(false); 
                novaOS.setMecanico(mecanicoLivre);
                novaOS.setStatus(StatusOS.PROCESSO_MANUTENCAO);
                
                repoMecanicos.removerMecanico(mecanicoLivre.getNome());
                repoMecanicos.adicionarMecanico(mecanicoLivre);
            } else {
                novaOS.setStatus(StatusOS.ABERTA);
                novaOS.setMecanico(null);
            }

            repoOS.salvar(novaOS);
            return true;
        }
        return false;
    }
    
    public synchronized void verificarEProcessarFila() {
        List<OrdemServico> listaGeral = repoOS.listarTodas();
        if (listaGeral == null) return;

        for (OrdemServico os : listaGeral) {
            if (os.getStatus() == StatusOS.ABERTA) {
                
                List<Mecanico> todosMecanicos = repoMecanicos.listarTodos();
                List<Mecanico> disponiveis = new ArrayList<>();
                
                for (Mecanico m : todosMecanicos) {
                    if (m.isDisponivel()) {
                        disponiveis.add(m);
                    }
                }

                if (!disponiveis.isEmpty()) {
                    Mecanico mecanicoEscolhido;
                    if (disponiveis.size() > 1) {
                        mecanicoEscolhido = disponiveis.get(Math.random() < 0.5 ? 0 : 1);
                    } else {
                        mecanicoEscolhido = disponiveis.get(0);
                    }
                    alocarMecanicoNaOS(os, mecanicoEscolhido);
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
        
        repoMecanicos.removerMecanico(mecanico.getNome());
        repoMecanicos.adicionarMecanico(mecanico);
        
        if (os.getVeiculo() != null) {
            os.getVeiculo().setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
        repoOS.salvar(os);
    }

    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            
            // O valor total já engloba o valor do óleo padrão (EST-001) adicionado na UI
            os.calcularTotal();
            
            os.setStatus(StatusOS.FINALIZADA);
            os.setDataFechamento(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            Mecanico mecanicoAtribuido = os.getMecanico(); 
            if (mecanicoAtribuido != null) {
                mecanicoAtribuido.incrementarProdutividade(); 
                mecanicoAtribuido.setDisponivel(true);        
                
                repoMecanicos.removerMecanico(mecanicoAtribuido.getNome());
                repoMecanicos.adicionarMecanico(mecanicoAtribuido);
            }
            
            repoOS.salvar(os);
            
            if (os.getVeiculo() != null) {
                if (os.getVeiculo().getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.EM_MANUTENCAO) {
                    os.getVeiculo().setStatus(br.ufrpe.autodrive.negocio.beans.StatusVeiculo.ESTOQUE);
                }
            }
            
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
            os.getListaServicos().add(servico);
            repoOS.salvar(os); 
            return true;
        }
        return false;
    }
}
