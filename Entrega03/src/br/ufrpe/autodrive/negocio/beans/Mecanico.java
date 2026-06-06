package br.ufrpe.autodrive.negocio.beans;

/**
 * Classe Mecanico - Agora herdando de Pessoa
 */
public class Mecanico extends Pessoa {
	
	// Apenas adiciona o ID específico de versão para o Mecanico (Serialization/Persistence)
	private static final long serialVersionUID = 1L;
	
    // 1. Removido o atributo 'nome' (agora vem de Pessoa)
    private Double bonus;
    private boolean disponivel;

    // Construtor Default
    public Mecanico() {
        super();
    }

    // Construtor Principal
    public Mecanico(String nome, Double bonus, boolean disponivel) {
        // 2. Passa o nome para a Pessoa e define CPF/Telefone como null por enquanto
        super(nome, null, null); 
        this.bonus = bonus;
        this.disponivel = disponivel;
    }

    // --- Métodos Específicos do Mecânico ---

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        if (bonus < 0) {
            throw new IllegalArgumentException("O bônus não pode ser negativo.");
        }
        this.bonus = bonus;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // Os métodos getNome() e setNome() foram APAGADOS daqui, 
    // pois o Java já os herda automaticamente de Pessoa.
}
