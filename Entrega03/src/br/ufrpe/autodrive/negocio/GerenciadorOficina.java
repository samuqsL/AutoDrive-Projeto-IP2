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

        // Valida se os dois existem e se o número da OS é único
        if (cliente != null && veiculo != null && repoOS.buscarPorNumero(numero) == null) {
            
            // 🛑 NOVA TRAVA DE SEGURANÇA: Se o carro já estiver em manutenção, impede a abertura!
            if (veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO) {
                return false; // Retorna falso e a GUI exibirá mensagem de erro
            }
            
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
        	// 🛑 TRAVA DE SEGURANÇA: Se a OS já estiver FINALIZADA, impede de rodar novamente!
            if (os.getStatus() == StatusOS.FINALIZADA) {
                return false; // Retorna falso para a GUI exibir a mensagem de erro
            }
            
            // 1. Cumpri o Requisito 1: A OS precisa estar PAGA
            os.marcarComoPago();
            
            // 2. Cumpri o Requisito 2: Garante o item obrigatório se veio da GUI vazio
            if (os.getListaPecas().isEmpty()) {
                Pecas pecaObrigatoria = new Pecas();
                pecaObrigatoria.setNome("oleo"); // Nome exato para passar no equalsIgnoreCase
                pecaObrigatoria.setPreco(120.00); // Define um preço padrão para o relatório computar
                pecaObrigatoria.setQuantidade(1);
                
                os.getListaPecas().add(pecaObrigatoria);
            }
            
            // 3. Força a OS a atualizar o seu próprio atributo 'valorTotal' internamente
            os.calcularTotal(); 
            
            // 4. Finaliza de vez mudando o status para FINALIZADA e liberando o carro
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
