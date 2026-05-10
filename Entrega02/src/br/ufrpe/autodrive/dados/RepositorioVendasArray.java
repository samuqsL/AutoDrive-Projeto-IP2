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
    public boolean efetuarVenda(Cliente c, Vendedor v, Veiculo veic, double entrada) {
        // 1. O Gerenciador usa o construtor da Venda (Negócio)
        Venda novaVenda = new Venda(c, v, veic, entrada);

        // 2. Chama o método de negócio da classe Venda
        if (novaVenda.realizarVenda()) {
            // 3. Se a lógica de negócio passar, salva no repositório
            this.repoV.adicionarVenda(novaVenda);
            return true;
        }
        return false;
    }

    @Override
    public List<Notificacao> listarAlertasRevisao() {
        List<Notificacao> filtrados = new ArrayList<>();
        List<Venda> todasAsVendas = repoV.listarTodasVendas();

        for (Venda v : todasAsVendas) {
            // Cria a notificação com os dados da venda salva
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

    // Métodos de repasse para o Repositório
    @Override
    public void adicionarVenda(Venda venda) {
        this.repoV.adicionarVenda(venda);
    }

    @Override
    public void procurarVenda(String cpf) {
        this.repoV.procurarVenda(cpf);
    }

    @Override
    public void removerVenda() {
        this.repoV.removerVenda();
    }
}
