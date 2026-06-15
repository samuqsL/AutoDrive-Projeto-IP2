package br.ufrpe.autodrive.negocio.beans;

import java.io.Serializable;

public class MaoDeObra implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String descricao;
    private double valor;

    public MaoDeObra() {}

    public MaoDeObra(String descricao, double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public double calcularCusto() {
        return this.valor;
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
}
