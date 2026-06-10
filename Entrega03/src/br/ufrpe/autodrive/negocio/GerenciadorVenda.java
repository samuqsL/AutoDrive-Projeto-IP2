package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

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
    
    // 🟢 Efetuar Venda sem data settada (automatico: estabelece data atual)
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
    
    // 🟢 Efetuar Venda com data settada (data definida no construtor):
    @Override
    public boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada, LocalDateTime dataDigitada) {
        Cliente c = repoC.procurarCliente(cpfCliente);
        Vendedor v = repoVend.procurarVendedor(nomeVendedor);
        Veiculo veic = repoVeic.procurarVeiculo(chassi);

        if (c != null && v != null && veic != null && repoV.procurarVenda(numero) == null) {
            Venda novaVenda = new Venda(numero, c, v, veic, entrada);
            
            // Injeta a data escolhida na tela antes de processar as regras
            novaVenda.setDataVenda(dataDigitada); 
            
            if (novaVenda.realizarVenda()) {
                this.repoV.adicionarVenda(novaVenda);
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
