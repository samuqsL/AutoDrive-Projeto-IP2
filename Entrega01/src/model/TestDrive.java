package model;

public class TestDrive {

    private Cliente cliente;
    private Veiculo veiculo;

    //Construtor default (sem valores - precisa settar atributos posteriormente!)
    public TestDrive() {}
    
    // construtor
    public TestDrive(Cliente cliente, Veiculo veiculo) {
        this.cliente = cliente;
        this.veiculo = veiculo;
    }

    public void agendar() {

        if (!cliente.validarCnhCliente()) {
            System.out.println("CNH inválida");
            return;
        }

        if (veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO) {
            System.out.println("Veículo indisponível");
            return;
        }

        veiculo.setStatus(StatusVeiculo.TEST_DRIVE);

        System.out.println("Test-drive agendado");
    }
}
