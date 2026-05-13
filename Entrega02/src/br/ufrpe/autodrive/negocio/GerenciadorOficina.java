package br.ufrpe.autodrive.negocio;
import br.ufrpe.autodrive.dados.*;
// imports do beans!
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

//colocar os imports que estão precisando [!!!]

public class GerenciadorOficina implements IGerenciadorOficina {
	
private IRepositorioOS repoOS;
	
    // Construtor para receber o repositório
    public GerenciadorOficina(IRepositorioOS repo) {
        this.repoOS = repo;
    }

    @Override
    public void abrirOS(int numero, String dataAbertura, Cliente cliente, Veiculo veiculo) {
        // Cria a "folha de papel" da OS
        OrdemServico novaOS = new OrdemServico(numero, dataAbertura, cliente, veiculo);
        
        // Guarda no repositório
        repoOS.adicionarOS(novaOS);
        System.out.println("OS número " + numero + " aberta com sucesso!");
    }

    @Override
    public void registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade) {
        // 1. Busca a OS no repositório pelo número
        OrdemServico os = repoOS.procurarOS(numeroOS); 
        
        if (os != null) {
            // 2. Adiciona a peça. O método dentro de OrdemServico já vai 
            // checar a composição e retirar do estoque!
            os.adicionarPeca(peca, quantidade);
            System.out.println("Peça registrada na OS.");
        } else {
            System.out.println("OS não encontrada!");
        }
    }

    @Override
    public void registrarServicoNaOS(int numeroOS, MaoDeObra servico) {
        OrdemServico os = repoOS.procurarOS(numeroOS);
        if (os != null) {
            os.adicionarServico(servico);
            System.out.println("Serviço registrado na OS.");
        } else {
            System.out.println("OS não encontrada!");
        }
    }
    
    @Override
    public void finalizarServico(int numeroOS) {
        // Implementação futura...
    }
}
