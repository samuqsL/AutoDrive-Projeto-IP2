package br.ufrpe.autodrive.negocio.beans;

import java.time.LocalDateTime;

public class TestDrive {

    private Cliente cliente;
    private Veiculo veiculo;
    private LocalDateTime dataTestDrive; // 🟢 Novo Atributo

    public TestDrive() {
        this.dataTestDrive = LocalDateTime.now(); // 🟢 Garante uma data padrão se nascer vazio
    }
    
    public TestDrive(Cliente cliente, Veiculo veiculo) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataTestDrive = LocalDateTime.now(); // 🟢 Evita efeito cascata em chamadas antigas
    }

    // 🟢 Novo construtor para quando a tela ou a Main quiserem passar uma data específica
    public TestDrive(Cliente cliente, Veiculo veiculo, LocalDateTime dataTestDrive) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataTestDrive = dataTestDrive;
    }

    public boolean agendar() {
        if (cliente == null || !cliente.validarCnhCliente()) {
            return false;
        }

        if (veiculo == null || veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO) {
            return false;
        }

        veiculo.setStatus(StatusVeiculo.TEST_DRIVE);
        return true;
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    
    // 🟢 Novos Getters e Setters
    public LocalDateTime getDataTestDrive() { return dataTestDrive; }
    public void setDataTestDrive(LocalDateTime dataTestDrive) { this.dataTestDrive = dataTestDrive; }
}
