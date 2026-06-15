package br.ufrpe.autodrive.negocio.beans;

/**
 * Classe Mecanico - Herdando de Pessoa com controle de produtividade individual
 */
public class Mecanico extends Pessoa {
	
	private static final long serialVersionUID = 1L;
	
    // FUNÇÃO LOCALIZADA: Atributos de controle individual e disponibilidade
    private int produtividade; // Substituiu o antigo 'bonus' por contador de OS finalizadas
    private boolean disponivel;

    // Construtor Default
    public Mecanico() {
        super();
        this.produtividade = 0;
    }

    // FUNÇÃO LOCALIZADA: Construtor adaptado para inicializar com nome e disponibilidade
    public Mecanico(String nome, boolean disponivel) {
        super(nome, null, null); 
        this.produtividade = 0; // Inicia sempre com zero ordens concluídas
        this.disponivel = disponivel;
    }

    // --- Métodos Específicos do Mecânico ---

    // FUNÇÃO LOCALIZADA: Getters, Setters e Incrementador de Produtividade
    public int getProdutividade() {
        return produtividade;
    }

    public void setProdutividade(int produtividade) {
        if (produtividade < 0) {
            throw new IllegalArgumentException("A produtividade não pode ser negativa.");
        }
        this.produtividade = produtividade;
    }

    public void incrementarProdutividade() {
        this.produtividade++; // Acrescenta +1 sempre que concluir uma OS
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
