package br.ufrpe.autodrive.negocio;

import java.util.List;
import br.ufrpe.autodrive.dados.IRepositorioClientes;
import br.ufrpe.autodrive.dados.IRepositorioVeiculos;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import br.ufrpe.autodrive.negocio.beans.VeiculoNovo;
import br.ufrpe.autodrive.negocio.beans.VeiculoSeminovo;

public class GerenciadorCadastro implements IGerenciadorCadastro {
    
    private IRepositorioClientes repoClientes;
    private IRepositorioVeiculos repoVeiculos;

    // Construtor padrão que o seu ScreenManager/Main vai utilizar
    public GerenciadorCadastro(IRepositorioClientes repoClientes, IRepositorioVeiculos repoVeiculos) {
        this.repoClientes = repoClientes;
        this.repoVeiculos = repoVeiculos;
    }

    // =========================================================================
    // REGRA DE NEGÓCIO: CADASTRO DE CLIENTE
    // =========================================================================
    @Override
    public void cadastrarCliente(String nome, String cpf, String cnh, String email, String telefone) throws Exception {
        // 1. Validação de presença de dados obrigatórios (REQ05)
        if (nome == null || nome.trim().isEmpty()) throw new Exception("Erro: O nome do cliente é obrigatório!");
        if (cpf == null || cpf.trim().isEmpty()) throw new Exception("Erro: O CPF do cliente é obrigatório!");
        if (cnh == null || cnh.trim().isEmpty()) throw new Exception("Erro: A CNH do cliente é obrigatória para habilitar Test-Drives!");

        // Remove espaços extras nas pontas
        String nomeLimpo = nome.trim();
        String cpfLimpo = cpf.trim();
        String cnhLimpo = cnh.trim();
        String emailLimpo = (email != null) ? email.trim() : "";
        String telLimpo = (telefone != null) ? telefone.trim() : "";

        // 2. Travas de Duplicidade varrendo a lista atual
        for (Cliente c : repoClientes.listarClientes()) {
            if (c.getCpf().equals(cpfLimpo)) {
                throw new Exception("Erro de Duplicidade: Já existe um cliente cadastrado com este CPF!");
            }
            if (c.getCnh().equals(cnhLimpo)) {
                throw new Exception("Erro de Duplicidade: Já existe um cliente cadastrado com esta CNH!");
            }
            if (c.getNome().equalsIgnoreCase(nomeLimpo)) {
                throw new Exception("Erro de Duplicidade: Já existe um cliente registrado com este mesmo Nome!");
            }
            // Só valida e-mail e telefone se eles não forem vazios
            if (!emailLimpo.isEmpty() && c.getEmail() != null && c.getEmail().equalsIgnoreCase(emailLimpo)) {
                throw new Exception("Erro de Duplicidade: Este E-mail já está associado a outro cliente!");
            }
            if (!telLimpo.isEmpty() && c.getTelefone() != null && c.getTelefone().equals(telLimpo)) {
                throw new Exception("Erro de Duplicidade: Este Telefone já está associado a outro cliente!");
            }
        }

        // 3. Salvando a entidade no Banco de Dados/Arquivo usando o CRUD do Repositório
        Cliente novoCliente = new Cliente(nomeLimpo, cpfLimpo, cnhLimpo, emailLimpo, telLimpo);
        repoClientes.adicionarCliente(novoCliente);
    }

    // =========================================================================
    // REGRA DE NEGÓCIO: CADASTRO DE VEÍCULO (REQ01, REQ02, REQ12, REQ17)
    // =========================================================================
    @Override
    public void cadastrarVeiculo(String chassi, String renavam, String modelo, int ano, double preco, boolean ehSeminovo, double kmInicial) throws Exception {
        // 1. Validação de campos fundamentais (REQ01)
        if (chassi == null || chassi.trim().isEmpty()) throw new Exception("Erro: O número do Chassi é obrigatório!");
        if (modelo == null || modelo.trim().isEmpty()) throw new Exception("Erro: O modelo/versão do veículo é obrigatório!");
        if (ano <= 0) throw new Exception("Erro: Ano de fabricação inválido!");
        if (preco <= 0) throw new Exception("Erro: O preço base deve ser maior que zero!");

        String chassiLimpo = chassi.trim();
        String renavamLimpo = (renavam != null) ? renavam.trim() : "";
        String modeloLimpo = modelo.trim();

        // 2. Bloqueio de duplicidade por Chassi (Id Único do carro)
        if (repoVeiculos.procurarVeiculo(chassiLimpo) != null) {
            throw new Exception("Erro de Duplicidade: Um veículo com este número de Chassi já consta no estoque!");
        }

        // 3. Fábrica Polymorfica baseada no tipo selecionado (REQ02, REQ17)
        Veiculo novoVeiculo;
        
        if (ehSeminovo) {
            // REQ17: Não permite cadastro de seminovos sem registro de quilometragem inicial.
            if (kmInicial <= 0) {
                throw new Exception("Erro [REQ17]: Veículos Seminovos exigem uma quilometragem inicial maior que zero!");
            }
            novoVeiculo = new VeiculoSeminovo(chassiLimpo, renavamLimpo, modeloLimpo, ano, preco, kmInicial);
        } else {
            // Veículo Novo assume 0.0 na quilometragem nativamente no construtor
            novoVeiculo = new VeiculoNovo(chassiLimpo, renavamLimpo, modeloLimpo, ano, preco);
        }

        // 4. Executa a inserção física usando o CRUD do repositório
        repoVeiculos.adicionarVeiculo(novoVeiculo);
    }

    // =========================================================================
    // IMPLEMENTAÇÃO DOS MÉTODOS DE LEITURA (Para suprir o JavaFX)
    // =========================================================================
    @Override
    public List<Cliente> listarClientes() {
        return repoClientes.listarClientes();
    }

    @Override
    public List<Veiculo> listarVeiculos() {
        return repoVeiculos.listarTodos();
    }
}
