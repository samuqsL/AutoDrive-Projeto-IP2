package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Mecanico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMecanicosArray implements IRepositorioMecanicos {

    // Instância única para o padrão Singleton
    private static RepositorioMecanicosArray instance;

    private ArrayList<Mecanico> listaMecanicos;
    private static final String CAMINHO_ARQUIVO = "dados/mecanicos.dat";

    // Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioMecanicosArray() {
        this.listaMecanicos = new ArrayList<>();
        this.carregarArquivo();
    }

    // Método estático para obter a instância única
    public static RepositorioMecanicosArray getInstance() {
        if (instance == null) {
            instance = new RepositorioMecanicosArray();
        }
        return instance;
    }

    @Override
    public void adicionarMecanico(Mecanico m) {
        if (m != null) {
            this.listaMecanicos.add(m);
            this.salvarArquivo(); // Salva no arquivo automaticamente
        }
    }

    @Override
    public Mecanico procurarMecanico(String nome) {
        for (Mecanico m : listaMecanicos) {
            if (m.getNome().equalsIgnoreCase(nome)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public void removerMecanico(String nome) {
        Mecanico m = procurarMecanico(nome);
        if (m != null) {
            this.listaMecanicos.remove(m);
            this.salvarArquivo(); // Salva após a remoção
        }
    }

    @Override
    public List<Mecanico> listarTodos() {
        return new ArrayList<>(this.listaMecanicos);
    }

    @Override
    public void atualizarMecanico(Mecanico m) {
        // Como a lista em memória já possui as referências alteradas,
        // apenas invocamos a gravação do arquivo para persistir o novo estado.
        this.salvarArquivo();
    }

    // =========================================================================
    // 💾 MÉTODOS DE PERSISTÊNCIA EM ARQUIVOS
    // =========================================================================
    
    private void salvarArquivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMINHO_ARQUIVO))) {
            oos.writeObject(this.listaMecanicos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de mecânicos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return; // Se não existe arquivo prévio, inicia vazio

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.listaMecanicos = (ArrayList<Mecanico>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de mecânicos, iniciando lista vazia.");
            this.listaMecanicos = new ArrayList<>();
        }
    }
}
