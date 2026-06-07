package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioOsArray implements IRepositorioOS {
    
    // 1. Instância única para o padrão Singleton
    private static RepositorioOsArray instance;
    
    private List<OrdemServico> listaOS;
    private static final String CAMINHO_ARQUIVO = "dados/ordens_servico.dat";

    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioOsArray() {
        this.listaOS = new ArrayList<>();
        this.carregarArquivo();
    }

    // 3. Método estático para obter a instância única
    public static synchronized RepositorioOsArray getInstance() {
        if (instance == null) {
            instance = new RepositorioOsArray();
        }
        return instance;
    }

    @Override
    public void salvar(OrdemServico os) {
        if (os != null) {
            OrdemServico osExistente = buscarPorNumero(os.getNumero());
            if (osExistente != null) {
                int indice = this.listaOS.indexOf(osExistente);
                this.listaOS.set(indice, os);
            } else {
                this.listaOS.add(os);
            }
            this.salvarArquivo();
        }
    }

    @Override
    public List<OrdemServico> listarTodas() {
        return new ArrayList<>(this.listaOS);
    }

    @Override
    public OrdemServico buscarPorNumero(int numero) {
        for (OrdemServico os : this.listaOS) {
            if (os.getNumero() == numero) {
                return os; 
            }
        }
        return null;
    }

    @Override
    public void remover(int numero) {
        OrdemServico osParaRemover = buscarPorNumero(numero);
        if (osParaRemover != null) {
            this.listaOS.remove(osParaRemover);
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
            oos.writeObject(this.listaOS);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de ordens de serviço: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.listaOS = (ArrayList<OrdemServico>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de ordens de serviço, iniciando lista vazia.");
            this.listaOS = new ArrayList<>();
        }
    }
}
