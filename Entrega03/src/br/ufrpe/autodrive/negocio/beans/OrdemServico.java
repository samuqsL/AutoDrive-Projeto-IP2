package br.ufrpe.autodrive.negocio.beans;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class OrdemServico implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int numero;
    private StatusOS status;
    private String dataAbertura;
    private String dataFechamento;
    private Double valorTotal;

    private Cliente cliente;
    private Veiculo veiculo;
    private Mecanico mecanicoResponsavel;

    private List<Pecas> listaPecas;
    private List<MaoDeObra> listaServicos;

    public OrdemServico() {
        this.listaPecas = new ArrayList<>();
        this.listaServicos = new ArrayList<>();
        this.status = StatusOS.ABERTA; 
        this.valorTotal = 0.0;
    }

    public OrdemServico(int numero, String dataAbertura, Cliente cliente, Veiculo veiculo) {
        this();
        this.numero = numero;
        this.dataAbertura = dataAbertura;
        this.cliente = cliente;
        this.veiculo = veiculo;
        
        if (this.veiculo != null) {
            this.veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
    }

    public boolean adicionarPeca(Pecas peca, int quantidade) {
        if (peca != null && quantidade > 0) {
            peca.setQuantidade(quantidade);
            return this.listaPecas.add(peca);
        }
        return false;
    }

    public void calcularTotal() {
        double total = 0;
        for (Pecas p : listaPecas) {
            total += (p.getPreco() * p.getQuantidade());
        }
        for (MaoDeObra m : listaServicos) {
            total += m.getValor();
        }
        this.valorTotal = total;
    }

    public void marcarComoPago() {
        this.status = StatusOS.PAGA;
    }

    public boolean finalizarOS() {
        if (this.status == StatusOS.PAGA || this.status == StatusOS.PROCESSO_MANUTENCAO) {
            this.status = StatusOS.FINALIZADA;
            if (this.veiculo != null) {
                this.veiculo.setStatus(StatusVeiculo.DISPONIVEL);
            }
            return true;
        }
        return false;
    }

    public Mecanico getMecanicoResponsavel() { return mecanicoResponsavel; }
    public void setMecanicoResponsavel(Mecanico mecanicoResponsavel) { this.mecanicoResponsavel = mecanicoResponsavel; }

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
}
