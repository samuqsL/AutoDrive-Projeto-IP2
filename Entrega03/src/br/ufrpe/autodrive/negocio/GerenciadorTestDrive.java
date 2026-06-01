package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.time.LocalDateTime;
import java.util.List;

public class GerenciadorTestDrive implements IGerenciadorTestDrive {
    private IRepositorioTD repoTD;
    private IRepositorioClientes repoC;
    private IRepositorioVeiculos repoV; 

    public GerenciadorTestDrive(IRepositorioTD repoTD, IRepositorioClientes repoC, IRepositorioVeiculos repoV) {
        this.repoTD = repoTD;
        this.repoC = repoC;
        this.repoV = repoV;
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi) {
        // 🟢 Reaproveita a lógica de baixo passando o horário atual
        return agendarTestDrive(cpf, chassi, LocalDateTime.now());
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada) {
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); 

        if (c != null && v != null) {
            if (dataDigitada == null) {
                dataDigitada = LocalDateTime.now();
            }
            
            // 🛑 NOVA TRAVA: Se houver conflito de agenda para o carro ou cliente, barra aqui
            if (horarioConflitante(cpf, chassi, dataDigitada)) {
                System.out.println("-> [Erro] Conflito de agenda: Cliente ou Veículo ocupados nesse horário.");
                return false; 
            }
            
            // Usa o novo construtor passando a data capturada
            TestDrive novoTD = new TestDrive(c, v, dataDigitada); 
            if (novoTD.agendar()) {
                this.repoTD.adicionarTestDrive(novoTD);
                return true;
            }
        }
        return false;
    }

    /**
     * 🕵️‍♂️ Método Auxiliar de Validação
     * Varre a lista de test-drives agendados para checar conflitos.
     * Considera uma janela de tolerância/duração de 1 hora para o evento.
     */
    private boolean horarioConflitante(String cpf, String chassi, LocalDateTime novaData) {
        if (this.repoTD == null) return false;
        
        List<TestDrive> agendamentosExistentes = this.repoTD.listarTestDrives();
        if (agendamentosExistentes == null) return false;

        for (TestDrive td : agendamentosExistentes) {
            LocalDateTime inicioExistente = td.getDataTestDrive();
            LocalDateTime fimExistente = inicioExistente.plusHours(1); // Define a janela de 1 hora
            
            // Verifica se a data digitada bate ou cai dentro do intervalo de algum agendamento ativo
            boolean bateNoIntervalo = (novaData.isEqual(inicioExistente) || novaData.isAfter(inicioExistente)) 
                                      && novaData.isBefore(fimExistente);

            if (bateNoIntervalo) {
                // Conflito 1: O mesmo veículo não pode estar em dois lugares ao mesmo tempo
                if (td.getVeiculo().getChassi().equalsIgnoreCase(chassi)) {
                    return true;
                }
                // Conflito 2: O mesmo cliente não pode pilotar dois carros ao mesmo tempo
                if (td.getCliente().getCpf().equals(cpf)) {
                    return true;
                }
            }
        }
        return false;
    }
}
