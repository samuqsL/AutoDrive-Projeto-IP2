package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Cliente;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class RepositorioClientesArray implements IRepositorioClientes {
	
	// 1. Instância única para o padrão Singleton
    private static RepositorioClientesArray instance;
    
    private ArrayList<Cliente> ListaDeClientes;
    private static final String CAMINHO_ARQUIVO = "dados/clientes.dat"; //Caminho dos arquivos de persistencia .dat + nome do arquivo a ser criado (Persistence)*
    
    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioClientesArray() {
        this.ListaDeClientes = new ArrayList<>();
        this.carregarArquivo();
    }
    
    // 3. Método estático para obter a instância única
    public static RepositorioClientesArray getInstance() {
        if (instance == null) {
            instance = new RepositorioClientesArray();
        }
        return instance;
    }

    @Override
    public void adicionarCliente(Cliente novoCliente) {
        if (cliente != null) {
            this.ListaDeClientes.add(cliente);
            this.salvarArquivo();
        }
    }

    @Override
    public Cliente procurarCliente(String cpf) {
        for (Cliente c : ListaDeClientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void removerCliente(Cliente cliente) {
        this.ListaDeClientes.remove(cliente);
        this.salvarArquivo();
    }

    @Override
    public List<Cliente> listarClientes() {
        return new ArrayList<>(this.ListaDeClientes);
    }

    // =========================================================================
    // 💾 MÉTODOS DE PERSISTÊNCIA EM ARQUIVOS
    // =========================================================================
    
    private void salvarArquivo() {
    	// 1. Cria a referência do arquivo baseada na constante "dados/nome_do_arquivo.dat"
        File arquivo = new File(CAMINHO_ARQUIVO);
        
        // 2. Verifica se a pasta mãe (dados) existe; se não existir, cria!
        if (arquivo.getParentFile() != null && !arquivo.getParentFile().exists()) {
            arquivo.getParentFile().mkdirs();
        }
    	
        // 3. Abre o fluxo de gravação e despeja a lista correspondente
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMINHO_ARQUIVO))) {
            oos.writeObject(this.ListaDeClientes); // Mude para ListaDeClientes, estoque, etc., dependendo do repo
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de vendedores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.ListaDeClientes = (ArrayList<Cliente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de clientes, iniciando lista vazia.");
            this.ListaDeClientes = new ArrayList<>();
        }
    }
}
