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
    public List<TestDrive> listarTestDrives() {
        return this.repoTD.listarTestDrives();
    }
    
    @Override
    public boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada) {
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); 

        if (c != null && v != null) {
            if (dataDigitada == null) {
                dataDigitada = LocalDateTime.now();
            }
            
            // NOVA TRAVA: Se houver conflito de agenda para o carro ou cliente, barra aqui
            if (horarioConflitante(cpf, chassi, dataDigitada)) {
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
    
    // metodos novos
    @Override
    public List<Cliente> listarTodosClientes() {
        return this.repoC.listarClientes();
    }

    @Override
    public List<Veiculo> listarTodosVeiculos() {
        return this.repoV.listarTodos();
    }

    @Override
    public boolean cancelarTestDrive(String id) {
        // 1. Acha o agendamento
        TestDrive td = repoTD.procurarTestDrivePorID(id);
        
        if (td != null) {
            // 2. Libera o carro para voltar a ser vendido/agendado!
            td.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
            
            // 3. Remove do repositório de Test Drives
            repoTD.removerTestDrivePorID(id);
            return true;
        }
        return false; // Não achou o ID
    }

    /**
     * Método Auxiliar de Validação
     * Varre a lista de test-drives agendados para checar conflitos.
     * Considera uma janela de tolerância/duração de 1 hora para o evento.
     */
    private boolean horarioConflitante(String cpf, String chassi, LocalDateTime novaData) {
        if (this.repoTD == null) return false;
        
        List<TestDrive> agendamentosExistentes = this.repoTD.listarTestDrives();
        if (agendamentosExistentes == null) return false;

        for (TestDrive td : agendamentosExistentes) {
            LocalDateTime inicioExistente = td.getDataTestDrive();
            LocalDateTime fimExistente = inicioExistente.plusHours(1); // O test-drive dura 1 hora
            
            // 🟢 CORREÇÃO DA CONDIÇÃO: Verifica se a nova data sobrepõe o intervalo de 1 hora da antiga
            boolean conflitoHorario = (novaData.isEqual(inicioExistente) || novaData.isAfter(inicioExistente)) 
                                      && novaData.isBefore(fimExistente);

            if (conflitoHorario) {
                // Se for no mesmo intervalo, bloqueia se for o mesmo carro OU o mesmo cliente
                if (td.getVeiculo().getChassi().equalsIgnoreCase(chassi)) {
                    return true;
                }
                if (td.getCliente().getCpf().equals(cpf)) {
                    return true;
                }
            }
        }
        return false;
    }
}