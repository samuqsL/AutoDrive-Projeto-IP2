package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;

public class GerenciadorOficina implements IGerenciadorOficina {

    private IRepositorioOS repoOS;
    private IRepositorioClientes repoClientes; // Precisamos desses dois para buscar
    private IRepositorioVeiculos repoVeiculos; // os objetos reais pelos IDs

    public GerenciadorOficina(IRepositorioOS repoOS, IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos) {
        this.repoOS = repoOS;
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
    }

    @Override
    public boolean abrirOS(int numero, String dataAbertura, String cpfCliente, String chassiVeiculo) {
        // Busca os objetos reais nos repositórios
        Cliente cliente = repoClientes.procurarCliente(cpfCliente);
        Veiculo veiculo = repoVeiculos.procurarVeiculo(chassiVeiculo);

        // Valida se os dois existem antes de criar a OS
        if (cliente != null && veiculo != null && repoOS.buscarPorNumero(numero) == null) {
            OrdemServico novaOS = new OrdemServico(numero, dataAbertura, cliente, veiculo);
            repoOS.salvar(novaOS);
            return true;
        }
        return false;
    }

    @Override
    public boolean finalizarServico(int numeroOS) {
        OrdemServico os = repoOS.buscarPorNumero(numeroOS);
        if (os != null) {
            // Chama a lógica que já está na Bean OrdemServico
            return os.finalizarOS();
        }
        return false;
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
            os.adicionarServico(servico);
            return true;
        }
        return false;
    }
}
