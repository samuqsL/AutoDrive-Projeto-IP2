package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorEstoquePecas;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaEstoquePecas {

    @FXML
    private ComboBox<Pecas> cbPecas;

    @FXML
    private TextField txtQuantidadeReposicao;

    @FXML
    private TableView<Pecas> tabelaReposicao;

    @FXML
    private TableColumn<Pecas, String> colunaNome;

    @FXML
    private TableColumn<Pecas, Integer> colunaQuantidade;

    private IGerenciadorEstoquePecas gerenciadorEstoque;
    
    // Lista observável que armazena os itens que o gerente adicionou para repor juntos
    private ObservableList<Pecas> listaItensReposicao = FXCollections.observableArrayList();

    // Método que o seu ScreenManager chamará para injetar o gerenciador
    public void setGerenciador(IGerenciadorEstoquePecas gerenciadorEstoque) {
        this.gerenciadorEstoque = gerenciadorEstoque;
        carregarComboBox();
    }

    @FXML
    public void initialize() {
        // Configura as colunas da tabela de lote temporário
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tabelaReposicao.setItems(listaItensReposicao);

        // Customiza o ComboBox para mostrar o Nome da peça e o Estoque Atual ao mesmo tempo
        cbPecas.setCellFactory(param -> new ListCell<Pecas>() {
            @Override
            protected void updateItem(Pecas item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPercent() + item.getNome() + " (Estoque Atual: " + item.getQuantidade() + ")");
                }
            }
        });

        cbPecas.setButtonCell(new ListCell<Pecas>() {
            @Override
            protected void updateItem(Pecas item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " (Estoque Atual: " + item.getQuantidade() + ")");
                }
            }
        });
    }

    private void carregarComboBox() {
        if (gerenciadorEstoque != null) {
            cbPecas.setItems(FXCollections.observableArrayList(gerenciadorEstoque.listarPecas()));
        }
    }

    @FXML
    public void adicionarNaLista() {
        Pecas pecaSelecionada = cbPecas.getValue();

        if (pecaSelecionada == null) {
            mostrarAlerta("Aviso", "Por favor, selecione uma peça no menu.");
            return;
        }

        try {
            int quantidade = Integer.parseInt(txtQuantidadeReposicao.getText());
            
            if (quantidade <= 0) {
                mostrarAlerta("Erro", "A quantidade para repor deve ser maior que zero.");
                return;
            }

            // Se o gerente adicionar a mesma peça de novo, apenas soma a quantidade na tabela
            for (Pecas p : listaItensReposicao) {
                if (p.getCodigo().equals(pecaSelecionada.getCodigo())) {
                    p.setQuantidade(p.getQuantidade() + quantidade);
                    tabelaReposicao.refresh();
                    txtQuantidadeReposicao.clear();
                    return;
                }
            }

            // Cria um objeto cópia temporário para listar na tabela com a quantidade informada
            Pecas itemLote = new Pecas(pecaSelecionada.getNome(), pecaSelecionada.getCodigo(), pecaSelecionada.getPreco(), quantidade);
            listaItensReposicao.add(itemLote);

            // Limpa o campo de texto para a próxima inserção
            txtQuantidadeReposicao.clear();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Por favor, digite apenas números inteiros válidos.");
        }
    }

    @FXML
    public void confirmarReposicaoTotal() {
        if (listaItensReposicao.isEmpty()) {
            mostrarAlerta("Aviso", "Nenhuma peça foi adicionada ao lote de reposição.");
            return;
        }

        try {
            // Varre toda a lista/tabela temporária atualizando o repositório peça por peça
            for (Pecas item : listaItensReposicao) {
                gerenciadorEstoque.reporEstoque(item.getCodigo(), item.getQuantidade());
            }

            mostrarAlerta("Sucesso", "Estoque de todas as peças atualizado com sucesso!");
            
            // Limpa as seleções e a tabela após salvar tudo
            listaItensReposicao.clear();
            cbPecas.getSelectionModel().clearSelection();
            carregarComboBox(); // Recarrega o ComboBox com os novos valores de estoque salvos

        } catch (Exception e) {
            mostrarAlerta("Erro", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
