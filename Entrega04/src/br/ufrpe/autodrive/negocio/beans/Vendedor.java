package br.ufrpe.autodrive.negocio.beans;


/**
 * Classe Vendedor - Agora herdando de Pessoa
 */
public class Vendedor extends Pessoa {
	
	// Apenas adiciona o ID específico de versão para o Vendedor (Serialization/Persistence)
	private static final long serialVersionUID = 1L;
	
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
    
    @Override
    public String toString() {
        return this.getNome(); // Exibe: Artur M.
    }
}
