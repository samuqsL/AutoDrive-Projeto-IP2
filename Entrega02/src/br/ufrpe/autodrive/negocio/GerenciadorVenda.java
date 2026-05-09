package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.IRepositorioVendas;

public class GerenciadorVenda implements IGerenciadorVenda {
    private IRepositorioVendas repoV; // Conforme seu UML: image_68b498.png

    public GerenciadorVenda(IRepositorioVendas repo) {
        this.repoV = repo;
    }

    @Override
    public boolean efetuarVenda(double valor) {
        // Exemplo de regra de negócio: Venda não pode ser valor zero ou negativo
        if (valor > 0) {
            // Se a lógica passar, ele mandaria salvar no repositório
            // Venda v = new Venda(valor);
            // repoV.salvar(v);
            return true; // Retorna SUCESSO para a Tela
        }
        
        return false; // Retorna FALHA para a Tela
    }
}
