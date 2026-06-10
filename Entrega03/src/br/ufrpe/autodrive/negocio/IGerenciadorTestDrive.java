package br.ufrpe.autodrive.negocio;

import java.time.LocalDateTime;

public interface IGerenciadorTestDrive {
    boolean agendarTestDrive(String cpf, String chassi);
    
    // 🟢 Nova sobrecarga que aceita a data vinda da interface gráfica
    boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada);
}
