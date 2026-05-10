package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.beans.Venda; // Importe sua classe de bean

public class GerenciadorVenda implements IGerenciadorVenda {
    private IRepositorioVendas repoV;

    public GerenciadorVenda(IRepositorioVendas repo) {
        this.repoV = repo;
    }

    @Override
    public boolean efetuarVenda(Venda venda) {
        // 1. Aplica as regras de negócio (as validações que você fez nos prints)
        if (venda != null && venda.getValorTotal() > 0) {
            
            // 2. CHAMA O REPOSITÓRIO para salvar (Método do seu UML de Dados)
            this.repoV.adicionarVenda(venda); 
            return true; 
        }
        return false;
    }

    @Override
    public List<Notificacao> listarAlertasRevisao() {
        List<Notificacao> filtrados = new ArrayList<>();
        
        // Pegamos a lista real do repositório
        List<Venda> todasAsVendas = repoV.listarTodasVendas(); 
    
        for (Venda v : todasAsVendas) { 
            // USANDO INFORMAÇÕES REAIS DA VENDA:
            Notificacao n = new Notificacao(
                v.getVeiculo().getQuilometragem(), // Pega a KM atual do carro vendido
                0,                                 // Começa na revisão 0
                v.getDataVenda().toString(),       // Pega a data real da venda
                v.calcularMesesUso(),              // Método na Venda que calcula meses até hoje
                v.getCliente(), 
                v.getVeiculo()
            );
    
            // O Gerenciador usa a lógica da Bean
            if (n.gerarAlerta()) {
                filtrados.add(n);
            }
        }
        return filtrados;
    }

    // ADICIONANDO ESTES PARA BATER COM O REPOSITÓRIO:

    @Override
    public void adicionarVenda(Venda venda) {
        // O Gerenciador recebe a venda da Tela e manda o Repositorio salvar
        this.repoV.adicionarVenda(venda);
    }
    
    @Override
    public void procurarVenda(String cpf) {
        // O Gerenciador repassa a busca por CPF para o Repositorio
        this.repoV.procurarVenda(cpf);
    }
    
    @Override
    public void removerVenda() {
        // O Gerenciador solicita a exclusão ao Repositorio
        this.repoV.removerVenda();
    }
}
