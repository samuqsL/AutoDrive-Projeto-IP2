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
    
    @Override
    public List<Cliente> listarTodosClientes() {
        return this.repoC.listarClientes();
    }

    @Override
    public List<Veiculo> listarTodosVeiculos() {
        return this.repoVeic.listarTodos();
    }

    @Override
    public List<Vendedor> listarTodosVendedores() {
        return this.repoVend.listarTodos();
    }
    
    @Override
    public List<Venda> listarTodasVendas() {
        return this.repoV.listarTodasVendas();
    }
    
    @Override
    public boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada) {
        Cliente c = repoC.procurarCliente(cpfCliente);
        Vendedor v = repoVend.procurarVendedor(nomeVendedor);
        Veiculo veic = repoVeic.procurarVeiculo(chassi);

        // 🛑 TRAVA DE DUPLICAÇÃO ANTES DA VENDA: Se o veículo já estiver VENDIDO na memória, barra imediatamente
        if (veic != null && veic.getStatus() == StatusVeiculo.VENDIDO) {
            return false;
        }

        if (c != null && v != null && veic != null) {
            Venda novaVenda = new Venda(c, v, veic, entrada);
            novaVenda.setDataVenda(LocalDateTime.now()); 
            
            if (novaVenda.realizarVenda()) {
                this.repoV.adicionarVenda(novaVenda);
                
                // 🟢 CORREÇÃO DOS MÉTODOS: Usa os métodos reais mapeados do seu repositório de veículos
                this.repoVeic.adicionarVeiculo(veic); 
                
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada, LocalDateTime dataDigitada) {
        Cliente c = repoC.procurarCliente(cpfCliente);
        Vendedor v = repoVend.procurarVendedor(nomeVendedor);
        Veiculo veic = repoVeic.procurarVeiculo(chassi);

        // 🛑 TRAVA DE DUPLICAÇÃO ANTES DA VENDA
        if (veic != null && veic.getStatus() == StatusVeiculo.VENDIDO) {
            return false;
        }

        if (c != null && v != null && veic != null) {
            Venda novaVenda = new Venda(c, v, veic, entrada);
            novaVenda.setDataVenda(dataDigitada); 
            
            if (novaVenda.realizarVenda()) {
                this.repoV.adicionarVenda(novaVenda);
                
                // 🟢 CORREÇÃO DOS MÉTODOS: Sincroniza o estado do veículo no repositório
                this.repoVeic.adicionarVeiculo(veic);
                
                return true;
            }
        }
        return false;
    }

    @Override
    public Venda procurarVenda(int numero) {
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
        this.repoV.removerVenda(numero);
    }
}
