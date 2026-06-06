package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Vendedor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVendedoresArray implements IRepositorioVendedores {
    
	// 1. Instância única para o padrão Singleton
    private static RepositorioVendedoresArray instance;
    
    private List<Vendedor> estoqueVendedores;
    private static final String CAMINHO_ARQUIVO = "dados/vendedores.dat"; //Caminho dos arquivos de persistencia .dat + nome do arquivo a ser criado (Persistence)*
    
    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioVendedoresArray() {
        this.estoqueVendedores = new ArrayList<>();
        this.carregarArquivo();
    }
    
    // 3. Método estático para obter a instância única
    public static RepositorioVendedoresArray getInstance() {
        if (instance == null) {
            instance = new RepositorioVendedoresArray();
        }
        return instance;
    }

    @Override
    public void adicionarVendedor(Vendedor v) {
        if (v != null) {
            this.estoqueVendedores.add(v);
            this.salvarArquivo();
        }
    }

    @Override
    public Vendedor procurarVendedor(String nome) {
        for (Vendedor v : estoqueVendedores) {
            if (v.getNome().equalsIgnoreCase(nome)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void removerVendedor(String nome) {
        Vendedor v = procurarVendedor(nome);
        if (v != null) {
            this.estoqueVendedores.remove(v);
            this.salvarArquivo();
        }
    }

    @Override
    public List<Vendedor> listarTodos() {
        return new ArrayList<>(estoqueVendedores);
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
            oos.writeObject(this.estoqueVendedores); // Mude para ListaDeClientes, estoque, etc., dependendo do repo
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de vendedores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.estoqueVendedores = (ArrayList<Vendedor>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de vendedores, iniciando lista vazia.");
            this.estoqueVendedores = new ArrayList<>();
        }
    }
}
