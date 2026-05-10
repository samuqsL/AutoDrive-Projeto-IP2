package br.ufrpe.autodrive.negocio.beans;

public class Cliente extends Pessoa { // 1. Herança
    // 2. APAGUEI nome, cpf e telefone daqui! Deixei só o que é do Cliente.
    private String cnh;
    private String email;

    public Cliente() { super(); }

    public Cliente (String nome, String cpf, String cnh, String email, String telefone){
        // 3. Envia os dados comuns para a classe mãe
        super(nome, cpf, telefone); 
        this.cnh = cnh;
        this.email = email;
    }

    // O overloading continua igual, ele chama o construtor de cima!
    public Cliente (String nome, String cpf, String cnh){
        this(nome, cpf, cnh, null, null);
    }

    // os getters e setters de Nome, CPF e Telefone (já estão na Pessoa)
    
    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }
  
    // ... restante dos métodos específicos ...
}
