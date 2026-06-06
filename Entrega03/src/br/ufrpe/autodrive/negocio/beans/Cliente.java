package br.ufrpe.autodrive.negocio.beans;

/**
 * Classe Cliente - Agora herdando de Pessoa
 */
public class Cliente extends Pessoa {
	
	// Apenas adiciona o ID específico de versão para o Cliente (Serialization/Persistence)*
	private static final long serialVersionUID = 1L;
	
    private String cnh;
    private String email;

    // Construtor padrão
    public Cliente() { 
        super(); 
    }

    // Construtor principal
    public Cliente (String nome, String cpf, String cnh, String email, String telefone){
        super(nome, cpf, telefone); 
        this.cnh = cnh;
        this.email = email;
    }

    // Overloading (Sobrecarga)
    public Cliente (String nome, String cpf, String cnh){
        this(nome, cpf, cnh, null, null);
    }

    // --- Métodos Específicos do Cliente ---

    public String getCnh() { 
        return cnh; 
    }
    
    public void setCnh(String cnh) { 
        this.cnh = cnh; 
    }

    public String getEmail() { 
        return email; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    // Métodos de lógica que você já tinha
    public void atualizarCadastro(String nome, String cnh, String email, String telefone){
        this.setNome(nome); // Vem da classe Pessoa
        this.setCnh(cnh);
        this.setEmail(email);
        this.setTelefone(telefone); // Vem da classe Pessoa
    }

    public boolean validarCnhCliente() {
        return this.cnh != null && !this.cnh.isEmpty();
    }
    
    @Override
    public String toString() {
        return this.getNome();
    }
}
