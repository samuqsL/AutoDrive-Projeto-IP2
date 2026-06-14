package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorEstoquePecas;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private TableColumn<Pecas, Double> colunaPreco;

    @FXML
    private TextField txtQuantidadeReposicao;

    private IGerenciadorEstoquePecas gerenciadorEstoque;
    private ObservableList<Pecas> pecasObservable;

    // Método que deve ser chamado pelo ScreenManager para injetar a dependência antes de mostrar a tela
    public void setGerenciador(IGerenciadorEstoquePecas gerenciadorEstoque) {
        this.gerenciadorEstoque = gerenciadorEstoque;
        carregarTabela();
    }

    @FXML
    public void initialize() {
        // Vincula as colunas da tabela aos atributos do Bean "Pecas"
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
    }

    private void carregarTabela() {
        if (gerenciadorEstoque != null) {
            pecasObservable = FXCollections.observableArrayList(gerenciadorEstoque.listarPecas());
            tabelaPecas.setItems(pecasObservable);
        }
    }

    @FXML
    public void reporPeca() {
        Pecas pecaSelecionada = tabelaPecas.getSelectionModel().getSelectedItem();
        
        if (pecaSelecionada == null) {
            exibirAlerta("Seleção Inválida", "Por favor, selecione uma peça na tabela antes de realizar a reposição.");
            return;
        }

        String qtdTexto = txtQuantidadeReposicao.getText();
        if (qtdTexto == null || qtdTexto.trim().isEmpty()) {
            exibirAlerta("Campo Vazio", "Digite a quantidade que deseja repor no estoque.");
            return;
        }

        try {
            int qtdAdicional = Integer.parseInt(qtdTexto.trim());
            
            // Chama o gerenciador para somar a quantidade
            gerenciadorEstoque.reporEstoque(pecaSelecionada.getCodigo(), qtdAdicional);
            
            txtQuantidadeReposicao.clear();
            carregarTabela(); // Atualiza a tabela na tela com o novo valor
            tabelaPecas.refresh();
            
        } catch (NumberFormatException e) {
            exibirAlerta("Valor Inválido", "A quantidade de reposição deve ser um número inteiro válido.");
        } catch (Exception e) {
            exibirAlerta("Erro na Reposição", e.getMessage());
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
