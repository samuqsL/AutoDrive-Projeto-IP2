package br.ufrpe.autodrive.negocio.beans;

public class Mecanico extends Pessoa {
    
    private static final long serialVersionUID = 1L;
    
    private int produtividade;
    private boolean disponivel;

    public Mecanico() {
        super();
        this.produtividade = 0;
    }

    public Mecanico(String nome, boolean disponivel) {
        super(nome, null, null); 
        this.disponivel = disponivel;
        this.produtividade = 0;
    }

    public int getProdutividade() {
        return produtividade;
    }

    public void setProdutividade(int produtividade) {
        this.produtividade = produtividade;
    }

    public void incrementarProdutividade() {
        this.produtividade++;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
