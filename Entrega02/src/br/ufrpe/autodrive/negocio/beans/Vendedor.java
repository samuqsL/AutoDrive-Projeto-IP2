package br.ufrpe.autodrive.negocio.beans;

public class Vendedor extends Pessoa {
    private double comissao;
    private double percentualComissao;

    // Construtor Default (opcional, mas bom ter)
    public Vendedor() {
        super();
    }
  
    public Vendedor(String nome, double percentualComissao){
        // Repassa o nome para a classe Pessoa
        super(nome, null, null); 
        this.percentualComissao = percentualComissao;
        this.comissao = 0;
    }

    // Getters e Setters específicos do Vendedor
    public double getComissao() { return comissao; }
    
    public void setComissao(double comissao) {
        if (comissao < 0) throw new IllegalArgumentException("Comissão inválida");
        this.comissao = comissao;  
    }

    public double getPercentualComissao() { return percentualComissao; }

    public void setPercentualComissao(double percentualComissao){
        if (percentualComissao < 0) throw new IllegalArgumentException("Percentual inválido");
        this.percentualComissao = percentualComissao;
    }
}
