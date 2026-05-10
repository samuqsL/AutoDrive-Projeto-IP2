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

    // ADICIONE ESTES PARA BATER COM O REPOSITÓRIO:

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
