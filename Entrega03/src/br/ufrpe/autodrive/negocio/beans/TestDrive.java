package br.ufrpe.autodrive.negocio.beans;

public class TestDrive {

    private Cliente cliente;
    private Veiculo veiculo;

    public TestDrive() {}
    
    public TestDrive(Cliente cliente, Veiculo veiculo) {
        this.cliente = cliente;
        this.veiculo = veiculo;
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
}
