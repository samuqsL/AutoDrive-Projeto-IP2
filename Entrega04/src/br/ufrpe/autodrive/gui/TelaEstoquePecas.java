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
    private TableView<Pecas> tabelaPecas;
    
    @FXML
    private TableColumn<Pecas, String> colunaCodigo;
    
    @FXML
    private TableColumn<Pecas, String> colunaNome;
    
    @FXML
    private TableColumn<Pecas, Integer> colunaQuantidade;

    @FXML
    private ComboBox<Pecas> cbPecas;

    @FXML
    private TextField txtQuantidadeReposicao;

    private IGerenciadorEstoquePecas gerenciadorEstoque;
    private ObservableList<Pecas> observablePecas;

    public void setGerenciador(IGerenciadorEstoquePecas gerenciadorEstoque) {
        this.gerenciadorEstoque = gerenciadorEstoque;
        carregarDados();
    }

    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        // Configura o ComboBox para exibir o nome da peça em vez da referência do objeto
        cbPecas.setCellFactory(param -> new ListCell<Pecas>() {
            @Override
            protected void updateItem(Pecas item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome());
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
                    setText(item.getNome());
                }
            }
        });
    }

    private void carregarDados() {
        if (gerenciadorEstoque != null) {
            observablePecas = FXCollections.observableArrayList(gerenciadorEstoque.listarPecas());
            tabelaPecas.setItems(observablePecas);
            cbPecas.setItems(observablePecas);
        }
    }

    @FXML
    public void confirmarReposicao() {
        // A seleção agora vem exclusivamente do ComboBox
        Pecas pecaSelecionada = cbPecas.getValue();

        if (pecaSelecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma peça no menu para repor.");
            return;
        }

        try {
            int quantidade = Integer.parseInt(txtQuantidadeReposicao.getText());
            
            // Repassa para a regra de negócio
            gerenciadorEstoque.reporEstoque(pecaSelecionada.getCodigo(), quantidade);
            
            // Limpa os campos e atualiza visualmente
            txtQuantidadeReposicao.clear();
            cbPecas.getSelectionModel().clearSelection();
            carregarDados(); 
            
            mostrarAlerta("Sucesso", "Estoque da peça atualizado com sucesso!");
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Por favor, digite apenas números inteiros na quantidade.");
        } catch (Exception e) {
            mostrarAlerta("Erro", e.getMessage()); // Exibe a mensagem de quantidade <= 0
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
