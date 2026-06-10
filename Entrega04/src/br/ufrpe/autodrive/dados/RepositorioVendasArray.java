package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Venda;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVendasArray implements IRepositorioVendas {
    
	// 1. Instância única para o padrão Singleton
    private static RepositorioVendasArray instance;
    
    private List<Venda> ListaDeVendas;
    private static final String CAMINHO_ARQUIVO = "dados/vendas.dat"; //Caminho dos arquivos de persistencia .dat + nome do arquivo a ser criado (Persistence)*
    
    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioVendasArray() {
        this.ListaDeVendas = new ArrayList<>();
        this.carregarArquivo();
    }
    
    // 3. Método estático para obter a instância única
    public static RepositorioVendasArray getInstance() {
        if (instance == null) {
            instance = new RepositorioVendasArray();
        }
        return instance;
    }

    @Override
    public void adicionarVenda(Venda venda) {
        if (venda != null) {
            this.ListaDeVendas.add(venda);
            this.salvarArquivo();
        }
    }

    @Override
    public Venda procurarVenda(int numero) {
        for (Venda v : ListaDeVendas) {
            if (v.getNumero() == numero) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void removerVenda(int numero) {
        Venda alvo = procurarVenda(numero);
        if (alvo != null) {
            this.ListaDeVendas.remove(alvo);
            this.salvarArquivo();
        }
    }

    @Override
    public List<Venda> listarTodasVendas() {
        return new ArrayList<>(ListaDeVendas);
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
            oos.writeObject(this.ListaDeVendas); // Mude para ListaDeClientes, estoque, etc., dependendo do repo
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de vendedores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.ListaDeVendas = (ArrayList<Venda>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de vendas, iniciando lista vazia.");
            this.ListaDeVendas = new ArrayList<>();
        }
    }
}
