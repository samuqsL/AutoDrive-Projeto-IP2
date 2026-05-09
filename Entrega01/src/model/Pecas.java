package model;

public class Pecas {
    private String nome;
    private String codigo;
    private double preco;
    private int quantidade;

    public Pecas() {}

    public Pecas(String nome, String codigo, double preco, int quantidade) {
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public double custoPecas() {
        return preco * quantidade;
    }

    // Controle de estoque
    public boolean retirarDoEstoque(int qtd) {
        if (qtd <= quantidade) {
            quantidade -= qtd;
            return true;
        }
        return false;
    }

    public void adicionarEstoque(int qtd) {
        quantidade += qtd;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
