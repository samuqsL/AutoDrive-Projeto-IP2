package model;

public class Cliente {

  private String nome;
  private String cpf;
  private String cnh;
  private String email;
  private String telefone;

  //Construtor default (sem valores - precisa settar atributos posteriormente!)
  public Cliente() {}

  //Construtor 1 (Principal)
  public Cliente (String nome, String cpf, String cnh, String email, String telefone){
    this.nome = nome;
    this.cpf = cpf;
    this.cnh = cnh;
    this.email = email;
    this.telefone = telefone;
  }
  //Construtor 2 (overloading[simplificando] --> direciona pro construtor principal)
   public Cliente (String nome, String cpf, String cnh){
     this(nome, cpf, cnh, null, null);
   }

  //getters (pegar valor)
  public String getNome() {return this.nome;}
  public String getCpf() {return this.cpf;}
  public String getCnh() {return this.cnh;}
  public String getEmail() {return this.email;}
  public String getTelefone() {return this.telefone;}
  //setters (alterar valor)
  public void setNome(String nome) {this.nome = nome;}
  public void setCpf(String cpf) {this.cpf = cpf;}
  public void setCnh(String cnh) {this.cnh = cnh;}
  public void setEmail(String email) {this.email = email;}
  public void setTelefone(String telefone) {this.telefone = telefone;}

  // ------ //
  
  //Metodo 1 (atualizar informações do cliente, apenas atributos alteravéis)
  public void atualizarCadastro(String nome, String cnh, String email, String telefone){
    setNome(nome);
    setCnh(cnh);
    setEmail(email);
    setTelefone(telefone);
  }
  public boolean validarCnhCliente() {
    return this.cnh != null && !this.cnh.isEmpty();
}
}
