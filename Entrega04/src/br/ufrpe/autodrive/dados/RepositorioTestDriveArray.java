package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.TestDrive;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioTestDriveArray implements IRepositorioTD {

    // 1. Instância única para o padrão Singleton
    private static RepositorioTestDriveArray instance;

    private List<TestDrive> testDrives;
    private static final String CAMINHO_ARQUIVO = "dados/test_drives.dat";

    // 2. Construtor privado que carrega os dados salvos do arquivo dat
    private RepositorioTestDriveArray() {
        this.testDrives = new ArrayList<>();
        this.carregarArquivo();
    }

    // 3. Método estático para obter a instância única
    public static synchronized RepositorioTestDriveArray getInstance() {
        if (instance == null) {
            instance = new RepositorioTestDriveArray();
        }
        return instance;
    }

    @Override
    public void adicionarTestDrive(TestDrive td) {
        if (td != null) {
            this.testDrives.add(td);
            this.salvarArquivo();
        }
    }

    @Override
    public List<TestDrive> listarTestDrives() {
        return new ArrayList<>(this.testDrives);
    }

    @Override
    public TestDrive procurarTestDrive(String chassi) {
        for (TestDrive td : testDrives) {
            if (td.getVeiculo().getChassi().equals(chassi)) {
                return td;
            }
        }
        return null;
    }

    @Override
    public void removerTestDrive(String chassi) {
        TestDrive tdEncontrado = procurarTestDrive(chassi);
        if (tdEncontrado != null) {
            this.testDrives.remove(tdEncontrado);
            this.salvarArquivo();
        }
    }
    
    // parte de remover por ID
    @Override
    public TestDrive procurarTestDrivePorID(String id) {
        if (id == null) return null;
        
        for (TestDrive td : testDrives) {
            // Checa se o agendamento e o ID dele existem antes de comparar
            if (td != null && td.getId() != null) {
                if (td.getId().equals(id)) {
                    return td;
                }
            }
        }
        return null;
    }
    @Override
    public void removerTestDrivePorID(String id) {
        TestDrive tdEncontrado = procurarTestDrivePorID(id);
        if (tdEncontrado != null) {
            this.testDrives.remove(tdEncontrado);
            this.salvarArquivo();
        }
    }

    // =========================================================================
    //  MÉTODOS DE PERSISTÊNCIA EM ARQUIVOS
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
            oos.writeObject(this.testDrives);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de test drives: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarArquivo() {
        File f = new File(CAMINHO_ARQUIVO);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.testDrives = (ArrayList<TestDrive>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de test drives, iniciando lista vazia.");
            this.testDrives = new ArrayList<>();
        }
    }
}