package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário de Cadastro ---
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Veiculo> cbVeiculo;
    @FXML private Label lblMensagem;

    // --- Componente de Finalização ---
    @FXML private ComboBox<String> cbFinalizarOS;

    // --- Componentes de Filtro ---
    @FXML private DatePicker dpFiltroData;

    // --- Componentes da Tabela: Fila de Espera / Manutenção ---
    @FXML private TableView<OrdemServico> tbFila;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, String> colFilaVeiculo; // 🟢 NOVA COLUNA DECLARADA
    @FXML private TableColumn<OrdemServico, StatusOS> colFilaStatus;
    @FXML private TableColumn<OrdemServico, String> colFilaData;

    // --- Componentes da Tabela: Histórico ---
    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistVeiculo; // 🟢 NOVA COLUNA DECLARADA
    @FXML private TableColumn<OrdemServico, StatusOS> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;
    

    public TelaOficina() {}

    @FXML
    public void initialize() {
        // Vincula dinamicamente a Fila (OS + Nome)
        if (colFilaOS != null) {
            colFilaOS.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                String clienteNome = (os != null && os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                return new javafx.beans.property.SimpleStringProperty(os.getNumero() + " - " + clienteNome);
            });
        }
        
        // 🟢 MAPEAMENTO DA NOVA COLUNA DE VEÍCULO (FILA)
        if (colFilaVeiculo != null) {
            colFilaVeiculo.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                Veiculo v = (os != null) ? os.getVeiculo() : null;
                String infoVeiculo = (v != null) ? v.getModelo() + " (" + v.getChassi() + ")" : "Não informado";
                return new javafx.beans.property.SimpleStringProperty(infoVeiculo);
            });
        }
        
        if (colFilaStatus != null) colFilaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colFilaData != null) colFilaData.setCellValueFactory(new PropertyValueFactory<>("dataAbertura"));

        // Vincula dinamicamente o Histórico (OS + Nome)
        if (colHistOS != null) {
            colHistOS.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                String clienteNome = (os != null && os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                return new javafx.beans.property.SimpleStringProperty(os.getNumero() + " - " + clienteNome);
            });
        }
        
        // 🟢 MAPEAMENTO DA NOVA COLUNA DE VEÍCULO (HISTÓRICO)
        if (colHistVeiculo != null) {
            colHistVeiculo.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                Veiculo v = (os != null) ? os.getVeiculo() : null;
                String infoVeiculo = (v != null) ? v.getModelo() + " (" + v.getChassi() + ")" : "Não informado";
                return new javafx.beans.property.SimpleStringProperty(infoVeiculo);
            });
        }
        
        if (colHistStatus != null) colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colHistData != null) colHistData.setCellValueFactory(new PropertyValueFactory<>("dataFechamento"));

        // Efeito Cascata do DatePicker: Atualiza tabelas instantaneamente ao selecionar uma data
        if (dpFiltroData != null) {
            dpFiltroData.valueProperty().addListener((observable, oldValue, newValue) -> {
                atualizarTabelas();
            });
        }
    }

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        carregarComboBoxes();
        atualizarTabelas();
    }

    // Carrega os dados dos repositórios diretamente para os ComboBoxes
    private void carregarComboBoxes() {
        try {
            List<Cliente> clientes = RepositorioClientesArray.getInstance().listarClientes();
            if (cbCliente != null) cbCliente.setItems(FXCollections.observableArrayList(clientes));

            List<Veiculo> veiculos = RepositorioVeiculosArray.getInstance().listarTodos();
            if (cbVeiculo != null) cbVeiculo.setItems(FXCollections.observableArrayList(veiculos));
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar repositórios nos ComboBoxes.");
        }
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            Cliente clienteSelecionado = cbCliente.getValue();
            Veiculo veiculoSelecionado = cbVeiculo.getValue();

            // Validação direta baseada nas seleções
            if (clienteSelecionado == null || veiculoSelecionado == null) {
                exibirMensagemErro("Selecione um Cliente e um Veículo para abrir a OS.");
                return;
            }

            // Extrai as chaves necessárias para o Gerenciador
            String cpf = clienteSelecionado.getCpf();
            String chassi = veiculoSelecionado.getChassi();

            if (control != null && control.abrirOS(cpf, chassi)) {
                lblMensagem.setText("✓ Sucesso: Nova Ordem de Serviço inserida na Fila.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparCamposCadastro();
                atualizarTabelas();
            } else {
                exibirMensagemErro("Não foi possível abrir a OS. Verifique os dados (pode estar Em Manutenção).");
            }
        } catch (Exception e) {
            exibirMensagemErro("Ocorreu uma falha ao tentar abrir a Ordem de Serviço.");
        }
    }

    @FXML
    public void botaoFinalizarOS() {
        String selecionada = cbFinalizarOS.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            if (lblMensagem != null) {
                lblMensagem.setText("Selecione uma OS válida em manutenção para finalizar!");
                lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); // Vermelho
            }
            return;
        }

        try {
            // Extrai o número da OS antes do hífen (ex: "12345 - João" vira 12345)
            int numeroOS = Integer.parseInt(selecionada.split(" - ")[0].trim());
            
            // Chama o método correto do GerenciadorOficina com todas as regras salvas
            boolean sucesso = control.finalizarServico(numeroOS);
            
            if (sucesso) {
                if (lblMensagem != null) {
                    lblMensagem.setText("OS Nº " + numeroOS + " Paga e Finalizada com sucesso!");
                    lblMensagem.setStyle("-fx-text-fill: #27ae60;"); // Verde
                }
                atualizarTabelas(); // Chama a sua função original com o filtro atualizado!
                cbFinalizarOS.getSelectionModel().clearSelection();
            } else {
                if (lblMensagem != null) {
                    lblMensagem.setText("Falha ao finalizar o serviço da OS.");
                    lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); // Vermelho
                }
            }
        } catch (Exception e) {
            if (lblMensagem != null) {
                lblMensagem.setText("Erro ao processar a finalização.");
                lblMensagem.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    @FXML
    public void limparFiltroData() {
        if (dpFiltroData != null) {
            dpFiltroData.setValue(null);
        }
    }

    private void atualizarTabelas() {
        try {
            // Mantém os ComboBoxes de Clientes/Veículos sincronizados com os repositórios
            carregarComboBoxes(); 

            List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
            if (todasOS == null) return;

            LocalDate dataFiltro = (dpFiltroData != null) ? dpFiltroData.getValue() : null;

            // =====================================================================
            // CORREÇÃO DO FILTRO: Agora exibe na tabela superior tanto quem está 
            // na fila de espera (ABERTA) quanto quem já está na oficina (PROCESSO_MANUTENCAO)
            // =====================================================================
            List<OrdemServico> filaAtiva = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .filter(os -> verificaDataFiltro(os.getDataAbertura(), dataFiltro))
                .collect(Collectors.toList());

            // 2. Atualiza Histórico (FINALIZADA / PAGA)
            List<OrdemServico> historicoConcluido = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
                .filter(os -> verificaDataFiltro(os.getDataFechamento(), dataFiltro))
                .collect(Collectors.toList());

            // 3. Atualiza o ComboBox de Finalizar (APENAS ordens ativas em manutenção)
            List<String> opcoesFinalizar = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .map(os -> {
                    String nome = (os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                    return os.getNumero() + " - " + nome; 
                })
                .collect(Collectors.toList());

            // Seta e atualiza os componentes gráficos com segurança
            if (tbFila != null) {
                tbFila.setItems(FXCollections.observableArrayList(filaAtiva));
                tbFila.refresh();
            }
            if (tbHistorico != null) {
                tbHistorico.setItems(FXCollections.observableArrayList(historicoConcluido));
                tbHistorico.refresh();
            }
            if (cbFinalizarOS != null) {
                cbFinalizarOS.setItems(FXCollections.observableArrayList(opcoesFinalizar));
            }
        } catch (Exception e) {
            System.err.println("Erro crítico ao sincronizar dados: " + e.getMessage());
        }
    }

    private boolean verificaDataFiltro(Object dataObjeto, LocalDate dataFiltro) {
        if (dataFiltro == null) return true; 
        if (dataObjeto == null) return false;

        try {
            String dataStr = dataObjeto.toString().trim();
            
            // Se a data vier no formato "dd/MM/yyyy HH:mm:ss" ou semelhante, isola o início
            if (dataStr.contains(" ")) {
                dataStr = dataStr.split(" ")[0];
            }
            
            // Trata formato brasileiro: dd/MM/yyyy
            if (dataStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                DateTimeFormatter formatoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dataStr, formatoBR).equals(dataFiltro);
            }
            
            // Trata formato internacional/banco: yyyy-MM-dd
            if (dataStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(dataStr).equals(dataFiltro);
            }
            
            // Fallback seguro por texto contido
            String filtroStr = dataFiltro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return dataStr.contains(filtroStr) || dataStr.contains(dataFiltro.toString());
            
        } catch (Exception e) {
            System.err.println("Aviso no filtro de data: " + e.getMessage());
            return dataObjeto.toString().contains(dataFiltro.toString());
        }
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

   private void limparCamposCadastro() {
        if (cbCliente != null) {
            cbCliente.setValue(null); // 🟢 Reseta o valor interno
            cbCliente.getSelectionModel().clearSelection(); // Limpa a seleção ativa
        }
        if (cbVeiculo != null) {
            cbVeiculo.setValue(null); // 🟢 Reseta o valor interno
            cbVeiculo.getSelectionModel().clearSelection(); // Limpa a seleção ativa
        }
    }

    private void limparTudo() {
        limparCamposCadastro();      
        if (dpFiltroData != null) {
            dpFiltroData.setValue(null);
        }
        if (cbFinalizarOS != null) {
            cbFinalizarOS.setValue(null); // 🟢 Garante o promptText da OS em manutenção também!
            cbFinalizarOS.getSelectionModel().clearSelection();
        }     
        if (lblMensagem != null) {
            lblMensagem.setText("Pronto para operar"); 
            lblMensagem.setStyle("-fx-text-fill: black;"); 
        }
    }

    private void exibirMensagemErro(String msg) {
        if (lblMensagem != null) {
            lblMensagem.setText("X Erro: " + msg);
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }
}
