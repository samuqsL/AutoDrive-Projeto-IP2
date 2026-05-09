package model;

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

    public void adicionarPeca(Pecas peca, int quantidade) {
        if (peca.retirarDoEstoque(quantidade)) {
            Pecas item = new Pecas(
                peca.getNome(),
                peca.getCodigo(),
                peca.getPreco(),
                quantidade
            );
            listaPecas.add(item);
        } else {
            System.out.println("Estoque insuficiente para: " + peca.getNome());
        }
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

    public void finalizarOS() {
        if (status != StatusOS.PAGA) {
            System.out.println("OS precisa estar PAGA.");
            return;
        }
    
        if (!validarItensObrigatorios()) {
            System.out.println("Obrigatório incluir óleo na revisão.");
            return;
        }
    
        this.status = StatusOS.FINALIZADA;
    
        // AJUSTE: Após finalizar e pagar, o veículo volta a ficar disponível
        this.veiculo.setStatus(StatusVeiculo.ESTOQUE);
    
        // NOVO AJUSTE: "Liberar" todos os mecânicos que trabalharam nesta OS
        for (MaoDeObra servico : listaServicos) {
            if (servico.getMecanico() != null) {
                servico.getMecanico().setDisponivel(true);
            }
        }
    
        System.out.println("OS finalizada, veículo e mecânicos liberados!");
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
