package br.ufrpe.autodrive.negocio.beans;

import java.util.ArrayList;
import java.util.List;

public class OrdemServico {
    private int numero;
    private StatusOS status;
    private String dataAbertura;
    private String dataFechamento;
    private Double valorTotal;

    private Cliente cliente;
    private Veiculo veiculo;

    private List<Pecas> listaPecas;
    private List<MaoDeObra> listaServicos;

    public OrdemServico() {
        this.listaPecas = new ArrayList<>();
        this.listaServicos = new ArrayList<>();
        this.status = StatusOS.ABERTA;
    }

    public OrdemServico(int numero, String dataAbertura, Cliente cliente, Veiculo veiculo) {
        this();
        this.numero = numero;
        this.dataAbertura = dataAbertura;
        this.cliente = cliente;
        this.veiculo = veiculo;
        
        // AJUSTE: Ao abrir a OS, o veículo entra em manutenção
        if (this.veiculo != null) {
            this.veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        }
    }

    // AJUSTE: Passou a retornar boolean em vez de void (e sem System.out.println)
    public boolean adicionarPeca(Pecas peca, int quantidade) {
        if (peca.retirarDoEstoque(quantidade)) {
            Pecas item = new Pecas(
                peca.getNome(),
                peca.getCodigo(),
                peca.getPreco(),
                quantidade
            );
            listaPecas.add(item);
            return true; // Sucesso
        }
        return false; // Falha: Estoque insuficiente
    }

    public void adicionarServico(MaoDeObra servico) {
        listaServicos.add(servico);
    }

    public Double calcularTotal() {
        double total = 0;

        for (Pecas p : listaPecas) {
            total += p.custoPecas();
        }

        for (MaoDeObra m : listaServicos) {
            total += m.calcularCusto();
        }

        this.valorTotal = total;
        return total;
    }

    public void marcarComoPago() {
        this.status = StatusOS.PAGA;
    }

    // ✅ REQ16
    private boolean validarItensObrigatorios() {
        for (Pecas p : listaPecas) {
            if (p.getNome().equalsIgnoreCase("oleo")) {
                return true;
            }
        }
        return false;
    }

    // AJUSTE: Passou a retornar boolean em vez de void (e sem System.out.println)
    public boolean finalizarOS() {
        if (status != StatusOS.PAGA) {
            return false; // OS precisa estar PAGA
        }
    
        if (!validarItensObrigatorios()) {
            return false; // Obrigatório incluir óleo na revisão
        }
    
        this.status = StatusOS.FINALIZADA;
    
        // AJUSTE: Após finalizar e pagar, o veículo volta a ficar disponível
        if (this.veiculo != null) {
            this.veiculo.setStatus(StatusVeiculo.ESTOQUE);
        }
    
        // NOVO AJUSTE: "Liberar" todos os mecânicos que trabalharam nesta OS
        for (MaoDeObra servico : listaServicos) {
            if (servico.getMecanico() != null) {
                servico.getMecanico().setDisponivel(true);
            }
        }
    
        return true; // Sucesso: OS finalizada, veículo e mecânicos liberados
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
}
