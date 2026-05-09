package model;

import java.time.LocalDateTime;
  
public class Venda {
  //Atributos
  private Cliente cliente;
  private Vendedor vendedor;
  private Veiculo veiculo;
  private double valorTotal;
  private double entrada;
  private LocalDateTime dataVenda;

  private static final double TAXA_IMPOSTO = 0.10; //Variavel compartilhada por todas as classes (static) | variavel constate fixa (final)
  private static final double ENTRADA_MINIMA = 5000.0; // Variavel constate fixa e compatilhada de ENTRADA MINIMA da VENDA!

  //Construtor default (sem valores - precisa settar atributos posteriormente!)
  public Venda() {}
  
  //Construtor 1 [Principal]
  public Venda (Cliente cliente, Vendedor vendedor, Veiculo veiculo, double entrada){
    //valor total não é inicializado no construtor!
    this.cliente = cliente;
    this.vendedor = vendedor;
    this.veiculo = veiculo;
    setEntrada(entrada);
  }
  
  //getters (pegar valor)
  public Cliente getCliente() {return this.cliente;}
  public Vendedor getVendedor() {return this.vendedor;}
  public Veiculo getVeiculo() {return this.veiculo;}
  public double getValorTotal() {return this.valorTotal;}
  public LocalDateTime getDataVenda() {return this.dataVenda;}
  public double getEntrada() {return this.entrada;}
  //setters (alterar valor / validações)
  public void setCliente(Cliente cliente) {this.cliente = cliente;}
  public void setVendedor(Vendedor vendedor) {this.vendedor = vendedor;}
  public void setVeiculo(Veiculo veiculo) {this.veiculo = veiculo;}
  public void setValorTotal(double valorTotal) {this.valorTotal = valorTotal;}
  public void setDataVenda(LocalDateTime dataVenda) {this.dataVenda = dataVenda;}
  public void setEntrada(double entrada) {
    if (entrada < 0) {
      throw new IllegalArgumentException("Entrada inválida");
    }
    this.entrada = entrada;
  }

 //Metodo 1 (realizar venda de veiculo) - [PRINCIPAL]
  public void realizarVenda() {
  
      if (!cliente.validarCnhCliente()) {
          System.out.println("Cliente sem CNH válida");
          return; //encerrar metodo antes da hora, para impedir da venda (se CNH DO CLIENTE for INVÁLIDA)
      }
  
      if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {
          System.out.println("Veículo já vendido");
          return; //encerrar metodo antes da hora, para impedir da venda (se tiver VENDIDO)!
      }
      
      if (veiculo.getStatus() == StatusVeiculo.RESERVADO){
        System.out.println("Veiculo já reservado");
        return; //encerrar metodo antes da hora, para impedir da venda (SE tiver RESERVADO)!
      }

      if (veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO){
        System.out.println("Veiculo em manutenção");
        return; //encerrar metodo antes da hora, para impedir da venda (SE tiver EM_MANUTENCAO)!
      }
    
      if (veiculo.getRenavam() == null || veiculo.getRenavam().isEmpty()){
        System.out.println("Veículo com pendência de Documentação!");
        return; //encerrar metodo antes da hora, para impedir da venda (SE RENAVAM tiver VAZIO)!
      }
      if (entrada < ENTRADA_MINIMA) {
        System.out.println("Entrada inferior ao mínimo");
        return; //encerrar metodo antes da hora, para impedir da venda (SE ENTRADA < ENTRADA MINIMA)
      }
  
      double precoBase = veiculo.getPreco(); // Variavel local do metodo realizarVenda(), o "precoBase" é o preco do veiculo da venda!
      double imposto = calcularImposto(precoBase); // variavel local do metodo realizaVenda(), o "imposto" da venda = *calculoImposto()* (chamada do metodo)
      double comissao = calcularComissao(precoBase); // variavel local do metodo realizaVenda(), a "comissao" = *preçoBase * PercentualComissao(do vendedor)!
  
      this.valorTotal = precoBase + imposto; //Inicializa o atributo "ValorTotal" da venda = *precoBase + imposto*
     
      this.dataVenda = LocalDateTime.now(); //inicializa o atributo: "dataVenda" da venda = *data atual*
      veiculo.setStatus(StatusVeiculo.VENDIDO); // SE todas as condicões forem atendidas ENTAO veiculo é dado como *VENDIDO*
      
      vendedor.setComissao(vendedor.getComissao() + comissao); //alterar atributo comissão pro vendedor (PÓS VENDA)!
    
      System.out.println("Venda realizada");
      System.out.println("Imposto: " + imposto);
      System.out.println("Comissão: " + comissao);
  }
  
  //Metodo 2 (Calcular Comissao)
  public double calcularComissao(double precoBase) {
    return precoBase * vendedor.getPercentualComissao();
  }
  //Metodo 3 (CalcularImposto)
  public double calcularImposto(double precoBase) {
    return precoBase * TAXA_IMPOSTO;
  }
}
