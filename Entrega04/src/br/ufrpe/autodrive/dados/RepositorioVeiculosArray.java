package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Veiculo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVeiculosArray implements IRepositorioVeiculos {
    
	// 1. Instância única para o padrão Singleton
    private static RepositorioVeiculosArray instance;
    
    private List<Veiculo> estoque;
    private static final String CAMINHO_ARQUIVO = "dados/veiculos.dat"; //Caminho dos arquivos de persistencia .dat + nome do arquivo a ser criado (Persistence)*
    
    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioVeiculosArray() {
        this.estoque = new ArrayList<>();
        this.carregarArquivo();
    }
    
    // 3. Método estático para obter a instância única
    public static RepositorioVeiculosArray getInstance() {
        if (instance == null) {
            instance = new RepositorioVeiculosArray();
        }
        return instance;
    }

    @Override
    public void adicionarVeiculo(Veiculo v) {
        if (v != null) { // 🟢 ADICIONEI ESTA VALIDAÇÃO
            this.estoque.add(v);
            this.salvarArquivo();
        }
    }

    @Override
    public Veiculo procurarVeiculo(String chassi) {
        for (Veiculo v : estoque) {
            if (v.getChassi().equals(chassi)) return v;
        }
        return null;
    }

    @Override
    public List<Veiculo> listarTodos() {
        return new ArrayList<Veiculo>(this.estoque);
    }

    @Override
    public void removerVeiculo(String chassi) {
        Veiculo v = procurarVeiculo(chassi);
        if (v != null) {
            estoque.remove(v);
            this.salvarArquivo();
        }
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
            oos.writeObject(this.estoque); // Mude para ListaDeClientes, estoque, etc., dependendo do repo
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de vendedores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.estoque = (ArrayList<Veiculo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de veículos, iniciando estoque vazio.");
            this.estoque = new ArrayList<>();
        }
    }
}
