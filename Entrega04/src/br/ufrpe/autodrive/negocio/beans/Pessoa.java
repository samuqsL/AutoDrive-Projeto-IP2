package br.ufrpe.autodrive.negocio.beans;
import java.io.Serializable;

//A mãe assina a responsabilidade da serialização (Serialization/Persistence)*
public abstract class Pessoa implements Serializable {
	
	// É uma excelente prática de POO colocar essa constante de controle (Serialization/Persistence)*
	private static final long serialVersionUID = 1L;
	
    protected String nome;
    protected String cpf;
    protected String telefone;

    public Pessoa() {}

    public Pessoa(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    // Getters e Setters comuns
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
