package model;

public class Vendedor {

  private String nome;
  private double comissao;
  private double percentualComissao;

  //construtor default (sem valores - precisa settar atributos posteriormente!)
  public Vendedor() {}
  
  //construtor principal da classe!
  public Vendedor(String nome, double percentualComissao){
    this.nome = nome;
    this.percentualComissao = percentualComissao;
    this.comissao = 0;
  }

  //getters (pegar valor)
  public String getNome() {return this.nome;}
  public double getComissao() {return this.comissao;}
  public double getPercentualComissao() {return this.percentualComissao;}
  //setters
  public void setNome(String nome) {this.nome = nome;}
  
  public void setComissao(double comissao) {
    if (comissao < 0) {
      throw new IllegalArgumentException("Comissão inválida"); //Impede que a comissão seja alterada para negativo!
    }
    this.comissao = comissao;  
  }
  public void setPercentualComissao(double percentualComissao){
    if (percentualComissao < 0) {
      throw new IllegalArgumentException("Percentual de comissao Inválido");
    }
    this.percentualComissao = percentualComissao;
  }

  //Metodos (...)
}
