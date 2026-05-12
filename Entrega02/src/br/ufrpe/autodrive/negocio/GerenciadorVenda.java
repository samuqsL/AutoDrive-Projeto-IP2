package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.dados.IRepositorioClientes;
import br.ufrpe.autodrive.negocio.beans.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorVenda implements IGerenciadorVenda {
    private IRepositorioVendas repoV;
    private IRepositorioClientes repoC;

    public GerenciadorVenda(IRepositorioVendas repoV, IRepositorioClientes repoC) {
        this.repoV = repoV;
        this.repoC = repoC;
    }

    @Override
    public boolean efetuarVenda(String cpfCliente, double entrada) {
        Cliente c = repoC.procurarCliente(cpfCliente);
        
        // 1. Instanciação correta (VeiculoNovo)
        Veiculo veic = new VeiculoNovo("CHASSI123", "PLACA-001", "Modelo Teste", 2024, 100000.0);
        veic.setStatus(StatusVeiculo.DISPONIVEL);
        
        // 2. Vendedor com apenas 2 parâmetros (Nome e % Comissão)
        Vendedor v = new Vendedor("Samuel", 0.1); 

        if (c != null) {
            Venda novaVenda = new Venda(c, v, veic, entrada);
            if (novaVenda.realizarVenda()) {
                this.repoV.adicionarVenda(novaVenda);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Notificacao> listarAlertasRevisao() {
        List<Notificacao> filtrados = new ArrayList<>();
        List<Venda> todasAsVendas = repoV.listarTodasVendas(); // Busca tudo o que foi salvo

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

    // Métodos de repasse (O Gerenciador expõe o que o repositório faz)
    @Override
    public Venda procurarVenda(String cpf) {
        return this.repoV.procurarVenda(cpf);
    }

    @Override
    public void removerVenda() {
        this.repoV.removerVenda();
    }
}
