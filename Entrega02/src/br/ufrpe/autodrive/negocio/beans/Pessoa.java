package br.ufrpe.autodrive.negocio.beans;

/**
 * Classe Abstrata Pessoa
 * Serve como base para Cliente, Vendedor e Mecanico.
 * Por ser abstract, ela não pode ser instanciada diretamente (new Pessoa()).
 */
public abstract class Pessoa {
    private String nome;
    private String cpf;
    private String telefone;

    // Construtor para ser usado pelas classes filhas (super)
    public Pessoa(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // Um método toString básico pode ajudar nos relatórios depois
    @Override
    public String toString() {
        return "Nome: " + nome + " | CPF: " + cpf;
    }
}
