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

    /**
     * 💾 MÉTODO AUXILIAR: Remove a instância antiga e readiciona o veículo com o estado atualizado.
     * Como os métodos nativos do repositório já chamam salvarArquivo() internamente, o arquivo
     * veiculos.dat será atualizado no disco respeitando o encapsulamento privado do sistema.
    */
    private void sincronizarRepositorioVeiculo(String chassi) {
        if (this.repoV != null && chassi != null) {
            // 1. Procura o veículo na memória (que já teve o Status modificado pelo agendar() ou cancelar())
            Veiculo veiculoAtualizado = this.repoV.procurarVeiculo(chassi);
            
            if (veiculoAtualizado != null) {
                // 2. Remove o registro desatualizado da lista e do disco
                this.repoV.removerVeiculo(chassi);
                
                // 3. Adiciona de volta o veículo com as alterações salvas em disco
                this.repoV.adicionarVeiculo(veiculoAtualizado);
            }
        }
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi) {
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
            
            // TRAVA: Se houver conflito de agenda para o carro ou cliente, barra aqui
            if (horarioConflitante(cpf, chassi, dataDigitada)) {
                return false; 
            }
            
            TestDrive novoTD = new TestDrive(c, v, dataDigitada); 
            if (novoTD.agendar()) { // Altera o status do veículo para TEST_DRIVE internamente
                this.repoTD.adicionarTestDrive(novoTD);
                
                // 💾 CORRIGIDO: Passando o chassi para sincronizar e salvar no arquivo veiculos.dat o novo status!
                this.sincronizarRepositorioVeiculo(chassi);
                
                return true;
            }
        }
        return false;
    }
    
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
        TestDrive td = repoTD.procurarTestDrivePorID(id);
        
        if (td != null) {
            String chassiCarro = null;
            
            // Libera o carro voltando o status para DISPONIVEL
            if (td.getVeiculo() != null) {
                td.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
                chassiCarro = td.getVeiculo().getChassi(); // Captura o chassi antes da remoção do agendamento
            }
            
            // Remove do repositório de Test Drives e salva o arquivo test_drives.dat
            repoTD.removerTestDrivePorID(id);
            
            // 💾 CORRIGIDO: Se encontramos um carro válido, sincroniza passando o chassi para salvar como DISPONIVEL no disco!
            if (chassiCarro != null) {
                this.sincronizarRepositorioVeiculo(chassiCarro);
            }
            
            return true;
        }
        return false; 
    }

    private boolean horarioConflitante(String cpf, String chassi, LocalDateTime novaData) {
        if (this.repoTD == null) return false;
        
        List<TestDrive> agendamentosExistentes = this.repoTD.listarTestDrives();
        if (agendamentosExistentes == null) return false;

        for (TestDrive td : agendamentosExistentes) {
            LocalDateTime inicioExistente = td.getDataTestDrive();
            LocalDateTime fimExistente = inicioExistente.plusHours(1); 
            
            boolean conflitoHorario = (novaData.isEqual(inicioExistente) || novaData.isAfter(inicioExistente)) 
                                      && novaData.isBefore(fimExistente);

            if (conflitoHorario) {
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
