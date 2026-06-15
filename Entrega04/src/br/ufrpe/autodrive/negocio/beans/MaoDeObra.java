package br.ufrpe.autodrive.negocio.beans;

import java.io.Serializable;

//Serialização da classe (Serialization/Persistence)*
public class MaoDeObra implements Serializable {
	
	// É uma excelente prática de POO colocar essa constante de controle (Serialization/Persistence)*
	private static final long serialVersionUID = 1L;
	
    private String descricao;
    private double valor;
    private Mecanico mecanico;

    public MaoDeObra() {}

    public MaoDeObra(String descricao, double valor, Mecanico mecanico) {
        //AJUSTE(REQ20): checa se mecanico tá ocupado, ou disponivel! (Ajustado para evitar null point caso não haja mecânico)
        if (mecanico != null && !mecanico.isDisponivel()) {
            throw new IllegalArgumentException("Mecânico ocupado!");
        }
        this.descricao = descricao;
        this.valor = valor;
        this.mecanico = mecanico;
    }

    public double calcularCusto() {
        return valor; // Cálculo modificado para usar apenas o valor fixo digitado pelo gerente
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }
}