package model;

public class Mecanico {
    private String nome;
    private Double bonus;
    private boolean disponivel;

    public Mecanico() {}

    public Mecanico(String nome, Double bonus, boolean disponivel) {
        this.nome = nome;
        this.bonus = bonus;
        this.disponivel = disponivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
