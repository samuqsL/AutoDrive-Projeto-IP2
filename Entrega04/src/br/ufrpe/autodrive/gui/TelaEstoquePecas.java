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
    
    private ObservableList<Pecas> listaItensReposicao = FXCollections.observableArrayList();

    public void setGerenciador(IGerenciadorEstoquePecas gerenciadorEstoque) {
        this.gerenciadorEstoque = gerenciadorEstoque;
        carregarComboBox();
    }

    @FXML
    public void initialize() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tabelaReposicao.setItems(listaItensReposicao);

        // CORRIGIDO: Removido o getPercent() inexistente
        cbPecas.setCellFactory(param -> new ListCell<Pecas>() {
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

            for (Pecas p : listaItensReposicao) {
                if (p.getCodigo().equals(pecaSelecionada.getCodigo())) {
                    p.setQuantidade(p.getQuantidade() + quantidade);
                    tabelaReposicao.refresh();
                    txtQuantidadeReposicao.clear();
                    return;
                }
            }

            Pecas itemLote = new Pecas(pecaSelecionada.getNome(), pecaSelecionada.getCodigo(), pecaSelecionada.getPreco(), quantidade);
            listaItensReposicao.add(itemLote);

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
            for (Pecas item : listaItensReposicao) {
                gerenciadorEstoque.reporEstoque(item.getCodigo(), item.getQuantidade());
            }

            mostrarAlerta("Sucesso", "Estoque de todas as peças atualizado com sucesso!");
            
            listaItensReposicao.clear();
            cbPecas.getSelectionModel().clearSelection();
            carregarComboBox();

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
