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
import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário de Cadastro ---
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Veiculo> cbVeiculo;
    @FXML private TextField txtDescricaoMaoDeObra;
    @FXML private ComboBox<Pecas> cbPeca;
    @FXML private Label lblEstoquePeca;
    @FXML private TextField txtQuantidadePeca;
    @FXML private TextField txtValorMaoDeObra;
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
    @FXML private TableColumn<OrdemServico, String> colHistValorTotal;
    
    public TelaOficina() {}

    @FXML
    public void initialize() {
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

        if (colHistValorTotal != null) {
            colHistValorTotal.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                String valor = (os != null && os.getValorTotal() != null) ? String.format("R$ %.2f", os.getValorTotal()) : "R$ 0,00";
                return new javafx.beans.property.SimpleStringProperty(valor);
            });
        }

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

    private void carregarComboBoxes() {
        try {
            List<Cliente> clientes = RepositorioClientesArray.getInstance().listarClientes();
            if (cbCliente != null) cbCliente.setItems(FXCollections.observableArrayList(clientes));

            List<Veiculo> veiculos = RepositorioVeiculosArray.getInstance().listarTodos();
            if (cbVeiculo != null) cbVeiculo.setItems(FXCollections.observableArrayList(veiculos));

            List<Pecas> pecas = RepositorioPecasArray.getInstance().listarTodas();
            if (cbPeca != null) {
                cbPeca.setItems(FXCollections.observableArrayList(pecas));
                
                cbPeca.setCellFactory(lv -> new ListCell<Pecas>() {
                    @Override
                    protected void updateItem(Pecas item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNome() + " (R$ " + item.getPreco() + ")");
                    }
                });
                cbPeca.setButtonCell(new ListCell<Pecas>() {
                    @Override
                    protected void updateItem(Pecas item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNome() + " (R$ " + item.getPreco() + ")");
                    }
                });

                cbPeca.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && lblEstoquePeca != null) {
                        lblEstoquePeca.setText("Estoque disponível: " + newVal.getQuantidade() + " unidades");
                    } else if (lblEstoquePeca != null) {
                        lblEstoquePeca.setText("");
                    }
                });
            }
        } catch (Exception e) {
            // Silenciado para evitar impressões em console, guiando para tela apenas
        }
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            Cliente clienteSelecionado = cbCliente.getValue();
            Veiculo veiculoSelecionado = cbVeiculo.getValue();
            Pecas pecaSelecionada = cbPeca.getValue();
            
            String descMaoObra = txtDescricaoMaoDeObra.getText() != null ? txtDescricaoMaoDeObra.getText().trim() : "";
            String qtdPecaStr = txtQuantidadePeca.getText() != null ? txtQuantidadePeca.getText().trim() : "0";
            String valorMaoObraStr = txtValorMaoDeObra.getText() != null ? txtValorMaoDeObra.getText().trim() : "0";

            if (clienteSelecionado == null || veiculoSelecionado == null) {
                exibirMensagemErro("Selecione um Cliente e um Veículo para abrir a OS.");
                return;
            }

            if (descMaoObra.isEmpty() || valorMaoObraStr.isEmpty()) {
                exibirMensagemErro("A descrição e o valor da Mão de Obra são obrigatórios.");
                return;
            }

            int qtdRequisitada = 0;
            double valorMaoObra = 0.0;
            try {
                if (!qtdPecaStr.isEmpty()) qtdRequisitada = Integer.parseInt(qtdPecaStr);
                valorMaoObra = Double.parseDouble(valorMaoObraStr.replace(",", "."));
            } catch (NumberFormatException e) {
                exibirMensagemErro("Digite um número válido para quantidade e valor de mão de obra.");
                return;
            }

            if (pecaSelecionada != null && qtdRequisitada > pecaSelecionada.getQuantidade()) {
                exibirMensagemErro("Estoque insuficiente da peça selecionada! Quantidade atual: " + pecaSelecionada.getQuantidade());
                return;
            }

            // OBRIGATORIEDADE DO ÓLEO - Checa a existência e a quantidade do óleo.
            Pecas oleoPadrao = RepositorioPecasArray.getInstance().buscarPorCodigo("EST-001");
            if (oleoPadrao == null || oleoPadrao.getQuantidade() < 1) {
                exibirMensagemErro("Falta óleo em estoque (EST-001)! É obrigatório para o serviço.");
                return;
            }

            String cpf = clienteSelecionado.getCpf();
            String chassi = veiculoSelecionado.getChassi();

            if (control != null && control.abrirOS(cpf, chassi)) {
                
                // Resgata a OS recém aberta que está vinculada a esse chassi
                List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
                OrdemServico osAberta = null;
                for (int i = todasOS.size() - 1; i >= 0; i--) {
                    OrdemServico os = todasOS.get(i);
                    if (os.getVeiculo().getChassi().equals(chassi) && os.getStatus() != StatusOS.FINALIZADA && os.getStatus() != StatusOS.PAGA) {
                        osAberta = os;
                        break;
                    }
                }

                if (osAberta != null) {
                    // Retira a unidade de óleo obrigatório do BD e adiciona cópia da peça à OS
                    oleoPadrao.retirarDoEstoque(1);
                    RepositorioPecasArray.getInstance().salvar(oleoPadrao);
                    osAberta.getListaPecas().add(new Pecas(oleoPadrao.getNome(), oleoPadrao.getCodigo(), oleoPadrao.getPreco(), 1));

                    // Retira a peça selecionada opcional do BD e adiciona cópia da peça à OS
                    if (pecaSelecionada != null && qtdRequisitada > 0) {
                        pecaSelecionada.retirarDoEstoque(qtdRequisitada);
                        RepositorioPecasArray.getInstance().salvar(pecaSelecionada);
                        osAberta.getListaPecas().add(new Pecas(pecaSelecionada.getNome(), pecaSelecionada.getCodigo(), pecaSelecionada.getPreco(), qtdRequisitada));
                    }

                    MaoDeObra maoDeObra = new MaoDeObra(descMaoObra, valorMaoObra, osAberta.getMecanico());
                    osAberta.getListaServicos().add(maoDeObra);

                    osAberta.calcularTotal(); // Atualiza o Total após a injeção
                    RepositorioOsArray.getInstance().salvar(osAberta); // Atualiza os dados persistidos
                }

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
                lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); 
            }
            return;
        }

        try {
            int numeroOS = Integer.parseInt(selecionada.split(" - ")[0].trim());
            
            boolean sucesso = control.finalizarServico(numeroOS);
            
            if (sucesso) {
                if (lblMensagem != null) {
                    lblMensagem.setText("OS Nº " + numeroOS + " Paga e Finalizada com sucesso!");
                    lblMensagem.setStyle("-fx-text-fill: #27ae60;");
                }
                atualizarTabelas(); 
                cbFinalizarOS.getSelectionModel().clearSelection();
            } else {
                if (lblMensagem != null) {
                    lblMensagem.setText("Falha ao finalizar o serviço da OS.");
                    lblMensagem.setStyle("-fx-text-fill: #e74c3c;"); 
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
            // Silenciado
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
        if (cbPeca != null) {
            cbPeca.setValue(null); 
            cbPeca.getSelectionModel().clearSelection(); 
        }
        if (txtDescricaoMaoDeObra != null) txtDescricaoMaoDeObra.clear();
        if (txtQuantidadePeca != null) txtQuantidadePeca.clear();
        if (txtValorMaoDeObra != null) txtValorMaoDeObra.clear();
        if (lblEstoquePeca != null) lblEstoquePeca.setText("");
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
