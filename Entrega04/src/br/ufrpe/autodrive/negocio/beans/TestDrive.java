package br.ufrpe.autodrive.negocio.beans;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.UUID; // 🟢 Adicionado para gerar IDs únicos automaticamente

//Serialização da classe (Serialization/Persistence)*
public class TestDrive implements Serializable {
    
    // É uma excelente prática de POO colocar essa constante de controle (Serialization/Persistence)*
    private static final long serialVersionUID = 1L;
    
    private String id; // 🟢 NOVO ATRIBUTO: Identificador único do agendamento
    private Cliente cliente;
    private Veiculo veiculo;
    private LocalDateTime dataTestDrive; 

    public TestDrive() {
        this.id = geradorDeId();
        this.dataTestDrive = LocalDateTime.now(); 
    }
    
    public TestDrive(Cliente cliente, Veiculo veiculo) {
        this.id = geradorDeId();
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataTestDrive = LocalDateTime.now(); 
    }

    public TestDrive(Cliente cliente, Veiculo veiculo, LocalDateTime dataTestDrive) {
        this.id = geradorDeId();
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataTestDrive = dataTestDrive;
    }

    // Método auxiliar privado para gerar uma string curta e única de ID (pega os primeiros 8 caracteres do UUID)
    private String geradorDeId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public boolean agendar() {
        if (cliente == null || !cliente.validarCnhCliente()) {
            return false;
        }
        
        if (veiculo == null) {
            return false;
        }

        // 🛑 MELHORIA CRÍTICA: O veículo só pode ser agendado se estiver DISPONIVEL ou em ESTOQUE.
        // Se estiver VENDIDO, RESERVADO ou EM_MANUTENCAO, barra na hora.
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL && veiculo.getStatus() != StatusVeiculo.ESTOQUE) {
            return false;
        }

        veiculo.setStatus(StatusVeiculo.TEST_DRIVE);
        return true;
    }

    // 🟢 NOVO GETTER (Essencial para o Repositório e para a TableView do JavaFX)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    
    public LocalDateTime getDataTestDrive() { return dataTestDrive; }
    public void setDataTestDrive(LocalDateTime dataTestDrive) { this.dataTestDrive = dataTestDrive; }
}