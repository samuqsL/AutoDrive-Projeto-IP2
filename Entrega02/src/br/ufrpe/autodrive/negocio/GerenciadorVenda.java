package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorVenda implements IGerenciadorVenda {
    
    private IRepositorioVendas repoV;
    private IRepositorioClientes repoC;
    private IRepositorioVendedores repoVend;
    private IRepositorioVeiculos repoVeic;

    public GerenciadorVenda(IRepositorioVendas repoV, IRepositorioClientes repoC, 
                            IRepositorioVendedores repoVend, IRepositorioVeiculos repoVeic) {
        this.repoV = repoV;
        this.repoC = repoC;
        this.repoVend = repoVend;
        this.repoVeic = repoVeic;
    }

    @Override
    public boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada) {
        Cliente c = repoC.procurarCliente(cpfCliente);
        Vendedor v = repoVend.procurarVendedor(nomeVendedor);
        Veiculo veic = repoVeic.procurarVeiculo(chassi);

        // AJUSTE: Mudei para procurarVenda(numero)
        if (c != null && v != null && veic != null && repoV.procurarVenda(numero) == null) {
            
            Venda novaVenda = new Venda(numero, c, v, veic, entrada);
            
            if (novaVenda.realizarVenda()) {
                this.repoV.adicionarVenda(novaVenda); // Bate com IRepositorio
                return true;
            }
        }
        return false;
    }

    @Override
    public Venda procurarVenda(int numero) {
        // AJUSTE: Mudei para procurarVenda(numero)
        return this.repoV.procurarVenda(numero);
    }
    
    @Override
    public List<Notificacao> listarAlertasRevisao() {
        List<Notificacao> filtrados = new ArrayList<>();
        List<Venda> todasAsVendas = repoV.listarTodasVendas(); 

        for (Venda v : todasAsVendas) {
            Notificacao n = new Notificacao(
                v.getVeiculo().getQuilometragem(),
                0,
                v.getDataVenda().toString(),
                v.calcularMesesUso(),
                v.getCliente(),
                v.getVeiculo()
            );

            if (n.gerarAlerta()) {
                filtrados.add(n);
            }
        }
        return filtrados;
    }
    
    @Override
    public void removerVenda(int numero) {
        // Ajuste: Sincronizado com a interface do repositório
        this.repoV.removerVenda(numero);
    }
}
