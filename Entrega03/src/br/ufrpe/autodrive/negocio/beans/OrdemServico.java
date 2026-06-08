package br.ufrpe.autodrive.negocio.beans;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrdemServico implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private int numero;
    private StatusOS status;
    private String dataAbertura;
    private String dataFechamento;
    private Double valorTotal;

    private Cliente cliente;
    private Veiculo veiculo;
    
    // FUNÇÃO LOCALIZADA: Atributo para vincular o mecânico individual responsável
    private Mecanico mecanico; 

    private List<Pecas> listaPecas;
    private List<MaoDeObra> listaServicos;

    public OrdemServico() {
        this.listaPecas = new ArrayList<>();
        this.listaServicos = new ArrayList<>();
        this.status = StatusOS.ABERTA; // Toda OS nasce por padrão na fila (ABERTA)
        this.valorTotal = 0.0;

        // FUNÇÃO LOCALIZADA: Gerador automático de código aleatório para a OS (5 dígitos)
        this.numero = 10000 + new Random().nextInt(90000);

        // FUNÇÃO LOCALIZADA: Captura automática da data do sistema
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.dataAbertura = agora.format(formatador);
    }

    // FUNÇÃO LOCALIZADA: Construtor simplificado (sem número e data manuais)
    public OrdemServico(Cliente cliente, Veiculo veiculo) {
        this();
        this.cliente = cliente;
        this.veiculo = veiculo;
    }

    // FUNÇÃO LOCALIZADA: Validador boolean para identificar se a OS possui mecânico alocado
    public boolean possuiMecanico() {
        return this.mecanico != null;
    }

    // Métodos utilitários e regras mantidos do projeto original
    public boolean adicionarPeca(Pecas peca, int quantidade) {
        if (peca != null && quantidade > 0) {
            peca.setQuantidade(quantidade);
            this.listaPecas.add(peca);
            return true;
        }
        return false;
    }

    public void marcarComoPago() {
        this.status = StatusOS.PAGA;
    }

    public void calcularTotal() {
        double total = 0;
        for (Pecas p : listaPecas) {
            total += p.getPreco() * p.getQuantidade();
        }
        this.valorTotal = total;
    }

    public boolean finalizarOS() {
        this.status = StatusOS.FINALIZADA;
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.dataFechamento = agora.format(formatador);
        return true;
    }

    // Getters e Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public StatusOS getStatus() { return status; }
    public void setStatus(StatusOS status) { this.status = status; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(String dataFechamento) { this.dataFechamento = dataFechamento; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public List<Pecas> getListaPecas() { return listaPecas; }
    public void setListaPecas(List<Pecas> listaPecas) { this.listaPecas = listaPecas; }

    public List<MaoDeObra> getListaServicos() { return listaServicos; }
    public void setListaServicos(List<MaoDeObra> listaServicos) { this.listaServicos = listaServicos; }

    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }
}
