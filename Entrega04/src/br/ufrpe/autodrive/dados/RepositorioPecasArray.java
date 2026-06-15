package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Pecas;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPecasArray implements IRepositorioPecas {
    
    private static RepositorioPecasArray instance;
    private List<Pecas> listaPecas;
    private static final String CAMINHO_ARQUIVO = "dados/pecas.dat";

    private RepositorioPecasArray() {
        this.listaPecas = new ArrayList<>();
        this.carregarArquivo();
    }

    public static synchronized RepositorioPecasArray getInstance() {
        if (instance == null) {
            instance = new RepositorioPecasArray();
        }
        return instance;
    }

    @Override
    public void salvar(Pecas peca) {
        if (peca != null) {
            Pecas pecaExistente = buscarPorCodigo(peca.getCodigo());
            if (pecaExistente != null) {
                int indice = this.listaPecas.indexOf(pecaExistente);
                this.listaPecas.set(indice, peca);
            } else {
                this.listaPecas.add(peca);
            }
            this.salvarArquivo();
        }
    }

    @Override
    public List<Pecas> listarTodas() {
        return new ArrayList<>(this.listaPecas);
    }

    @Override
    public Pecas buscarPorCodigo(String codigo) {
        if (codigo != null) {
            for (Pecas p : this.listaPecas) {
                if (p.getCodigo().equalsIgnoreCase(codigo.trim())) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void removerPorCodigo(String codigo) {
        Pecas p = buscarPorCodigo(codigo);
        if (p != null) {
            this.listaPecas.remove(p);
            this.salvarArquivo();
        }
    }

    /**
     * 🚀 MÉTODO EXTRA PARA A TELA DE REABASTECIMENTO
     * Facilita o REQ08: Altera a quantidade física diretamente pelo código da peça
     */
    @Override
    public boolean alterarQuantidadeEstoque(String codigo, int novaQtd) {
        Pecas p = buscarPorCodigo(codigo);
        if (p != null) {
            p.setQuantidade(novaQtd); // Altera usando o setter do seu Bean
            this.salvarArquivo();
            return true;
        }
        return false;
    }

    // =========================================================================
    // 💾 MÉTODOS DE PERSISTÊNCIA EM ARQUIVOS (.DAT)
    // =========================================================================
    
    private void salvarArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (arquivo.getParentFile() != null && !arquivo.getParentFile().exists()) {
            arquivo.getParentFile().mkdirs();
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMINHO_ARQUIVO))) {
            oos.writeObject(this.listaPecas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de peças: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.listaPecas = (ArrayList<Pecas>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de peças, iniciando lista vazia.");
            this.listaPecas = new ArrayList<>();
        }
    }
}
