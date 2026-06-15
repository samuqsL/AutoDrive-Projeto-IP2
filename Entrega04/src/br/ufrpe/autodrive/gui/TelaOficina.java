package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.dados.RepositorioPecasArray;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário de Cadastro ---
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Veiculo> cbVeiculo;
    @FXML private TextField txtDescricaoMaoObra; // Nova Mão de Obra
    @FXML private ComboBox<Pecas> cbPecas;       // Nova Peça
    @FXML private TextField txtQtdPecas;         // Nova Qtd
    @FXML private TextField txtValorMaoObra;     // Novo Valor Mão de Obra
    @FXML private Label lblMensagem;

    // --- Componente de Finalização ---
    @FXML private ComboBox<String> cbFinalizarOS;

    // --- Componentes de Filtro ---
    @FXML private DatePicker dpFiltroData;

    // --- Componentes da Tabela: Fila de Espera / Manutenção ---
    @FXML private TableView<OrdemServico> tbFila;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, String> colFilaVeiculo; 
    @FXML private TableColumn<OrdemServico, StatusOS> colFilaStatus;
    @FXML private TableColumn<OrdemServico, String> colFilaData;

    // --- Componentes da Tabela: Histórico ---
    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistVeiculo; 
    @FXML private TableColumn<OrdemServico, StatusOS> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;
    @FXML private TableColumn<OrdemServico, String> colHistValorTotal; // 🟢 NOVA COLUNA DECLARADA (Valor total pago)
    

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

        // 🟢 MAPEAMENTO DA NOVA COLUNA (Histórico Valor Pago)
        if (colHistValorTotal != null) {
            colHistValorTotal.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                return new javafx.beans.property.SimpleStringProperty(String.format("R$ %.2f", (os != null ? os.getValorTotal() : 0.0)));
            });
        }

        // Conversor para formatar o nome no ComboBox de peças e exibir o estoque na tela
        if (cbPecas != null) {
            cbPecas.setConverter(new StringConverter<Pecas>() {
                @Override
                public String toString(Pecas p) {
                    if (p == null) return null;
                    return p.getNome() + " (Estoque: " + p.getQuantidade() + ")";
                }
                @Override
                public Pecas fromString(String string) { return null; }
            });
        }

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

            List<Pecas> pecas = RepositorioPecasArray.getInstance().listarTodas();
            if (cbPecas != null) cbPecas.setItems(FXCollections.observableArrayList(pecas));

        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar repositórios nos ComboBoxes.");
        }
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            Cliente clienteSelecionado = cbCliente.getValue();
            Veiculo veiculoSelecionado = cbVeiculo.getValue();
            String descMaoObra = txtDescricaoMaoObra.getText();
            Pecas pecaSelecionada = cbPecas.getValue();
            String qtdStr = txtQtdPecas.getText();
            String valorStr = txtValorMaoObra.getText();

            // Validação direta baseada nas seleções dos campos obrigatórios
            if (clienteSelecionado == null || veiculoSelecionado == null || descMaoObra.trim().isEmpty() || valorStr.trim().isEmpty()) {
                exibirMensagemErro("Preencha todos os campos obrigatórios (Cliente, Veículo, Descrição e Valor).");
                return;
            }

            int qtdPecas = 0;
            if (pecaSelecionada != null) {
                if (qtdStr == null || qtdStr.trim().isEmpty()) {
                    exibirMensagemErro("Informe a quantidade para a peça selecionada.");
                    return;
                }
                qtdPecas = Integer.parseInt(qtdStr.trim());
                // Checa o estoque de imediato e bloqueia se faltar na prateleira da peça genérica
                if (qtdPecas > pecaSelecionada.getQuantidade()) {
                    exibirMensagemErro("Atenção: A quantidade requisitada (" + qtdPecas + ") é maior que o estoque em loja (" + pecaSelecionada.getQuantidade() + ").");
                    return;
                }
            }

            double valorMaoObra = Double.parseDouble(valorStr.replace(",", "."));

            // =========================================================
            // 🛑 GATILHO ESTOQUE ÓLEO (VERIFICA ANTES DE CONSTRUIR O RESTO)
            // =========================================================
            Pecas oleo = RepositorioPecasArray.getInstance().buscarPorCodigo("EST-001");
            if (oleo == null || oleo.getQuantidade() < 1) {
                exibirMensagemErro("Não é possível abrir OS! Falta de óleo no estoque. O óleo é obrigatório para as manutenções.");
                return;
            }

            // Realiza um Casting seguro para acessar o novo método 'abrirOSCompleta' se a interface não o tiver definido ainda.
            if (control instanceof br.ufrpe.autodrive.negocio.GerenciadorOficina) {
                br.ufrpe.autodrive.negocio.GerenciadorOficina ger = (br.ufrpe.autodrive.negocio.GerenciadorOficina) control;
                
                boolean sucesso = ger.abrirOSCompleta(clienteSelecionado.getCpf(), veiculoSelecionado.getChassi(), pecaSelecionada, qtdPecas, descMaoObra, valorMaoObra);
                
                if (sucesso) {
                    lblMensagem.setText("✓ Sucesso: Nova Ordem de Serviço inserida na Fila.");
                    lblMensagem.setStyle("-fx-text-fill: green;");
                    limparCamposCadastro();
                    atualizarTabelas();
                } else {
                    exibirMensagemErro("Não foi possível abrir a OS. Verifique os dados (pode estar Em Manutenção).");
                }
            } else {
                exibirMensagemErro("Erro de Instância do Gerenciador de Oficina.");
            }

        } catch (NumberFormatException nfe) {
            exibirMensagemErro("Por favor, digite apenas números válidos para Quantidade e Valor (Use '.' para decimais).");
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
            carregarComboBoxes(); 

            List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
            if (todasOS == null) return;

            LocalDate dataFiltro = (dpFiltroData != null) ? dpFiltroData.getValue() : null;

            List<OrdemServico> filaAtiva = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .filter(os -> verificaDataFiltro(os.getDataAbertura(), dataFiltro))
                .collect(Collectors.toList());

            List<OrdemServico> historicoConcluido = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
                .filter(os -> verificaDataFiltro(os.getDataFechamento(), dataFiltro))
                .collect(Collectors.toList());

            List<String> opcoesFinalizar = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .map(os -> {
                    String nome = (os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                    return os.getNumero() + " - " + nome; 
                })
                .collect(Collectors.toList());

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
            if (dataStr.contains(" ")) {
                dataStr = dataStr.split(" ")[0];
            }
            if (dataStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                DateTimeFormatter formatoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dataStr, formatoBR).equals(dataFiltro);
            }
            if (dataStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(dataStr).equals(dataFiltro);
            }
            String filtroStr = dataFiltro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return dataStr.contains(filtroStr) || dataStr.contains(dataFiltro.toString());
        } catch (Exception e) {
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
            cbCliente.setValue(null); 
            cbCliente.getSelectionModel().clearSelection(); 
        }
        if (cbVeiculo != null) {
            cbVeiculo.setValue(null); 
            cbVeiculo.getSelectionModel().clearSelection(); 
        }
        if (txtDescricaoMaoObra != null) txtDescricaoMaoObra.clear();
        
        if (cbPecas != null) {
            cbPecas.setValue(null);
            cbPecas.getSelectionModel().clearSelection();
        }
        if (txtQtdPecas != null) txtQtdPecas.clear();
        if (txtValorMaoObra != null) txtValorMaoObra.clear();
    }

    private void limparTudo() {
        limparCamposCadastro();      
        if (dpFiltroData != null) {
            dpFiltroData.setValue(null);
        }
        if (cbFinalizarOS != null) {
            cbFinalizarOS.setValue(null); 
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
