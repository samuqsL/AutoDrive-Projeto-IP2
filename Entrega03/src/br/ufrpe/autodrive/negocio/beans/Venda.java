package br.ufrpe.autodrive.negocio.beans;

import java.io.Serializable; // - Adicionado (para permitir serialização e persistencia dos dados)
import java.time.LocalDate;  // ⬅️ Adicionado 
import java.time.LocalDateTime;
import java.util.UUID;       // - Adicionado (gerando numero atuomatico)

//Serialização da classe (Serialization/Persistence)*
public class Venda implements Serializable{
	
  // É uma excelente prática de POO colocar essa constante de controle (Serialization/Persistence)*
  private static final long serialVersionUID = 1L;
  
  private int numero;
  private Cliente cliente;
  private Vendedor vendedor;
  private Veiculo veiculo;
  private double valorTotal;
  private double entrada;
  private LocalDateTime dataVenda;

  private static final double TAXA_IMPOSTO = 0.10;
  private static final double ENTRADA_MINIMA = 5000.0;

  public Venda() {}
  
  public Venda(Cliente cliente, Vendedor vendedor, Veiculo veiculo, double entrada) {
    this.numero = Math.abs(UUID.randomUUID().hashCode());
    this.cliente = cliente;
    this.vendedor = vendedor;
    this.veiculo = veiculo;
    setEntrada(entrada);
  }
  
  public Venda (int numero, Cliente cliente, Vendedor vendedor, Veiculo veiculo, double entrada){
    this.numero = numero;
    this.cliente = cliente;
    this.vendedor = vendedor;
    this.veiculo = veiculo;
    setEntrada(entrada);
  }

  // Getters e Setters
  public int getNumero() { return this.numero; }
  public Cliente getCliente() { return this.cliente; }
  public Vendedor getVendedor() { return this.vendedor; }
  public Veiculo getVeiculo() { return this.veiculo; }
  public double getValorTotal() { return this.valorTotal; }
  public double getEntrada() { return this.entrada; }
  public LocalDateTime getDataVenda() { return this.dataVenda; }
  public void setDataVenda(LocalDateTime data) { this.dataVenda = data; }
  
  public void setEntrada(double entrada) {
      if (entrada < 0) {
          this.entrada = 0;
      } else {
          this.entrada = entrada;
      }
  }

  /**
   * Executa a efetivação da venda aplicando as regras de negócio do AutoDrive
   */
  public boolean realizarVenda() {
      // Validação básica de integridade
      if (this.veiculo == null || this.cliente == null || this.vendedor == null) {
          return false; 
      }
      
      // REQ03 & REQ04: O carro não pode já ter sido vendido
      if (this.veiculo.getStatus() == StatusVeiculo.VENDIDO) {
          return false; 
      }

      // [NOVO] REQ12: Bloquear faturamento se possuir pendências de documentação (RENAVAM vazio ou nulo)
      if (this.veiculo.getRenavam() == null || this.veiculo.getRenavam().trim().isEmpty()) {
          return false;
      }

      // [NOVO] REQ19: Impedir a venda de veículo que possua reserva ativa
      if (this.veiculo.getStatus() == StatusVeiculo.RESERVADO) {
          return false;
      }
      
      // REQ15: Bloquear a finalização da venda caso o valor de entrada seja inferior ao mínimo
      if (this.entrada < ENTRADA_MINIMA) {
          return false; 
      }
  
      // REQ04: Cálculo de impostos e comissões
      double precoBase = veiculo.getPreco(); 
      double imposto = calcularImposto(precoBase); 
      double comissao = calcularComissao(precoBase); 
  
      this.valorTotal = precoBase + imposto; 
      
      if (this.dataVenda == null) {
          this.dataVenda = LocalDateTime.now(); 
      }
      
      // REQ03: Controlar status do veículo mudando para VENDIDO
      veiculo.setStatus(StatusVeiculo.VENDIDO); 
      
      // Acumula a comissão do vendedor com precisão
      vendedor.setComissao(vendedor.getComissao() + comissao); 
      return true;
  }
  
  public double calcularComissao(double precoBase) {
      if (vendedor == null) return 0.0;
      return precoBase * vendedor.getPercentualComissao();
  }
  
  public double calcularImposto(double precoBase) {
      return precoBase * TAXA_IMPOSTO;
  }

  public int calcularMesesUso() {
	  if (this.dataVenda == null) return 0;
	  // Garante o cálculo absoluto de meses, mesmo mudando de um ano para o outro
	  return (int) java.time.temporal.ChronoUnit.MONTHS.between(this.dataVenda.toLocalDate(), LocalDate.now());
  }
}
