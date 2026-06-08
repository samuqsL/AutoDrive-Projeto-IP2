package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes;
    private IRepositorioVeiculos repoVeiculos;
    
    private List<Mecanico> listaMecanicos = new ArrayList<>();
    private Queue<OrdemServico> filaDeOS = new LinkedList<>();
    private Random gerador = new Random();

    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
    }

    @Override
    public void adicionarMecanico(Mecanico m) {
        if (m != null) {
            this.listaMecanicos.add(m);
        }
    }

    @Override
    public int abrirOS(String cpfCliente, String chassiVeiculo) {
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        if (cliente != null && veiculo != null) {
            
            if (veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO) {
                return -1; 
            }
            
            int numeroUnico;
            do {
                numeroUnico = gerador.nextInt(99999) + 1;
            } while (repoOS.buscarPorNumero(numeroUnico) != null);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataAtual = LocalDate.now().format(formatter);
            
            OrdemServico novaOS = new OrdemServico(numeroUnico, dataAtual, cliente, veiculo);
            
            Mecanico mecanicoLivre = null;
            for (Mecanico m : listaMecanicos) {
                if (m.isDisponivel()) {
                    mecanicoLivre = m;
                    break;
                }
            }

            if (mecanicoLivre != null) {
                novaOS.setMecanicoResponsavel(mecanicoLivre);
                novaOS.setStatus(StatusOS.PROCESSO_MANUTENCAO);
                mecanicoLivre.setDisponivel(false);
            } else {
                filaDeOS.add(novaOS);
            }
            
            repoOS.salvar(novaOS);
            return numeroUnico;
        }
        return -1;
    }

    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        
        if (os != null && os.getStatus() == StatusOS.PROCESSO_MANUTENCAO) {
            
            Mecanico m = os.getMecanicoResponsavel();
            if (m != null) {
                m.incrementarProdutividade();
                m.setDisponivel(true);
            }
            
            os.setStatus(StatusOS.FINALIZADA);
            if (os.getVeiculo() != null) {
                os.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
            }
            
            if (!filaDeOS.isEmpty() && m != null) {
                OrdemServico proximaOS = filaDeOS.poll();
                proximaOS.setMecanicoResponsavel(m);
                proximaOS.setStatus(StatusOS.PROCESSO_MANUTENCAO);
                m.setDisponivel(false);
                repoOS.salvar(proximaOS);
            }
            
            repoOS.salvar(os);
            return true;
        }
        return false;
    }

    @Override
    public List<OrdemServico> listarHistoricoOSFinalizadas() {
        List<OrdemServico> finalizadas = new ArrayList<>();
        for (OrdemServico os : repoOS.listarTodas()) { 
            if (os.getStatus() == StatusOS.FINALIZADA) {
                finalizadas.add(os);
            }
        }
        return finalizadas;
    }

    @Override
    public boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null) {
            return os.adicionarPeca(peca, quantidade);
        }
        return false;
    }

    @Override
    public boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null) {
            return os.getListaServicos().add(servico);
        }
        return false;
    }
}
