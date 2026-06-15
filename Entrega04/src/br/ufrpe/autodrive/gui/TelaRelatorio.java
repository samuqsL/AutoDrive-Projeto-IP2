package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.ufrpe.autodrive.dados.RepositorioVendedoresArray;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.Relatorio;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.Vendedor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TelaRelatorio {

    public VBox painelMenuRelatorios;
    private IGerenciadorRelatorio control;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    @FXML private TextArea txtAreaConsole;


    public void injetarGerenciador(IGerenciadorRelatorio gR) {
        this.control = gR;
    }

    @FXML
    public void carregarVendasGerais() {
        if (control != null) {
            Relatorio dados = control.gerarDadosRelatorio();
            exibirVendasNoTexto(dados.getListaVendas(), "GERAL");
            RelatorioPdfService.exportarVendas(dados.getListaVendas(), "Geral");

        }
    }

    @FXML
    public void carregarVendasPorVendedor() {
        if (control == null) return;

        // 1. Acessa o Singleton do repositório e busca todos os vendedores cadastrados
        List<Vendedor> todosOsVendedores = RepositorioVendedoresArray.getInstance().listarTodos();

        // Extrai apenas os nomes dos vendedores e ordena em ordem alfabética
        List<String> nomesVendedores = todosOsVendedores.stream()
                .map(Vendedor::getNome)
                .sorted()
                .collect(Collectors.toList());

        // Se o repositório estiver vazio, avisa o usuário no console
        if (nomesVendedores.isEmpty()) {
            txtAreaConsole.setText("⚠️ Não há vendedores cadastrados no sistema.");
            return;
        }

        // 2. Criar a janela pop-up customizada
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Filtro por Vendedor");
        dialog.setHeaderText("Selecione o vendedor na lista abaixo:");

        // 3. Criar os botões de Confirmar (Filtrar) e Cancelar
        ButtonType botaoConfirmar = new ButtonType("Filtrar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(botaoConfirmar, ButtonType.CANCEL);

        // 4. Criar o componente de Menu Cascata (ComboBox)
        ComboBox<String> comboVendedores = new ComboBox<>();
        comboVendedores.getItems().addAll(nomesVendedores);
        comboVendedores.getSelectionModel().selectFirst(); // Deixa o primeiro selecionado por padrão
        comboVendedores.setPrefWidth(250);

        // 5. Organizar os elementos no painel visual
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        grid.add(new Label("Vendedor:"), 0, 0);
        grid.add(comboVendedores, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // 6. Converter o clique do botão no nome selecionado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == botaoConfirmar) {
                return comboVendedores.getValue();
            }
            return null;
        });

        // 7. Exibir a tela e processar o filtro com o nome escolhido
        Optional<String> resultado = dialog.showAndWait();

        resultado.ifPresent(nome -> {
            if (nome != null && !nome.trim().isEmpty()) {
                Relatorio dados = control.gerarDadosRelatorio();
                List<Venda> filtradas = dados.filtrarPorVendedor(nome);
                exibirVendasNoTexto(filtradas, "VENDEDOR: " + nome);
                RelatorioPdfService.exportarVendas(filtradas, "Vendedor_" + nome);
            }
        });
    }

    @FXML
    public void carregarVendasPorPeriodo() {
        if (control == null) return;

        // 1. Criar a janela pop-up customizada
        Dialog<List<LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Filtro por Período");
        dialog.setHeaderText("Escolha as datas de início e fim no calendário:");

        // 2. Criar os botões de Confirmar (OK) e Cancelar
        ButtonType botaoConfirmar = new ButtonType("Filtrar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(botaoConfirmar, ButtonType.CANCEL);

        // 3. Instanciar os componentes de Calendário (DatePicker)
        DatePicker pickerInicio = new DatePicker();
        DatePicker pickerFim = new DatePicker();

        // Configura a data inicial padrão como hoje
        pickerInicio.setValue(LocalDate.now());

        // 4. Bloquear datas retroativas no calendário de FIM com base no INÍCIO
        pickerInicio.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Se o usuário limpar o início, reseta o fim
            if (newValue == null) {
                pickerFim.setValue(null);
                pickerFim.setDisable(true);
                return;
            }

            pickerFim.setDisable(false);

            // Se a data de fim atual for anterior à nova data de início, ajusta automaticamente
            if (pickerFim.getValue() != null && pickerFim.getValue().isBefore(newValue)) {
                pickerFim.setValue(newValue);
            }
        });

        // Customiza a renderização dos dias na tela do calendário de FIM
        pickerFim.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Se houver uma data de início, desabilita todos os dias anteriores a ela
                if (pickerInicio.getValue() != null && date.isBefore(pickerInicio.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;"); // Deixa cinza
                }
            }
        });

        // Inicializa a data fim casada com a inicial
        pickerFim.setValue(pickerInicio.getValue());

        // 5. Organizar os elementos numa tabela (GridPane)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Data de Início:"), 0, 0);
        grid.add(pickerInicio, 1, 0);
        grid.add(new Label("Data de Fim:"), 0, 1);
        grid.add(pickerFim, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // 6. Converter o clique do botão "Filtrar" no resultado das duas datas
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == botaoConfirmar) {
                return List.of(pickerInicio.getValue(), pickerFim.getValue());
            }
            return null;
        });

        // 7. Exibir o calendário na tela e esperar o usuário interagir
        Optional<List<LocalDate>> resultado = dialog.showAndWait();

        // 8. Processar o resultado caso o usuário tenha clicado em Filtrar
        resultado.ifPresent(datas -> {
            LocalDate ini = datas.get(0);
            LocalDate fim = datas.get(1);

            if (ini != null && fim != null) {
                Relatorio dados = control.gerarDadosRelatorio();
                List<Venda> filtradas = dados.filtrarPorPeriodo(ini, fim);
                exibirVendasNoTexto(filtradas, "PERÍODO");
                RelatorioPdfService.exportarVendas(filtradas, "Periodo_" + ini.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            } else {
                txtAreaConsole.setText("❌ Erro: Ambas as datas precisam ser selecionadas!");
            }
        });
    }

    @FXML
    public void carregarGeralOficina() {
        if (control != null) {
            Relatorio dados = control.gerarDadosRelatorio();
            StringBuilder sb = new StringBuilder();
            sb.append("--- RELATÓRIO DE OFICINA ---\n");
            if (dados.getListaOs().isEmpty()) sb.append("Nenhuma OS encontrada.\n");

            for (OrdemServico os : dados.getListaOs()) {
                sb.append("OS Nº: ").append(os.getNumero())
                        .append(" | Data: ").append(os.getDataAbertura()) // <--- Usando a data aqui!
                        .append(" | Cliente: ").append(os.getCliente().getNome())
                        .append(" | Status: ").append(os.getStatus()).append("\n");
            }
            txtAreaConsole.setText(sb.toString());

            RelatorioPdfService.exportarOficina(dados.getListaOs());
        }
    }

    @FXML
    public void carregarLucratividade() {
        if (control != null) {
            double[] lucros = control.gerarDadosRelatorio().calcularLucratividade();
            String resumo = String.format(
                    "--- RESUMO FINANCEIRO ---\n" +
                            "Receita de Peças: R$ %.2f\n" +
                            "Receita de Serviços: R$ %.2f\n" +
                            "TOTAL ACUMULADO: R$ %.2f\n",
                    lucros[0], lucros[1], (lucros[0] + lucros[1])
            );
            txtAreaConsole.setText(resumo);
            RelatorioPdfService.exportarLucratividade(lucros);
        }
    }

    @FXML
    public void voltarParaMenu() {
        limparCamposConsole();
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void exibirVendasNoTexto(List<Venda> lista, String titulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- RELATÓRIO DE VENDAS (").append(titulo).append(") ---\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum registro encontrado.\n");
        } else {
            for (Venda v : lista) {
                String dataFormatada = (v.getDataVenda() != null) ? v.getDataVenda().format(fmt) : "Sem Data";
                sb.append("Nº: ").append(v.getNumero())
                        .append(" | Data: ").append(dataFormatada)
                        .append(" | Vendedor: ").append(v.getVendedor().getNome())
                        .append(" | Cliente: ").append(v.getCliente().getNome())
                        .append(" | Veiculo: ").append(v.getVeiculo().getModelo())
                        .append(" | Entrada: ").append(v.getEntrada())
                        .append(" | Total: R$ ").append(v.getValorTotal()).append("\n");
            }
        }
        txtAreaConsole.setText(sb.toString());
    }
    /**
     * Método auxiliar de limpeza de interface
     */
    private void limparCamposConsole() {
        txtAreaConsole.clear();
    }

  }