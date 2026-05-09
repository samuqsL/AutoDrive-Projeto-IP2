package model;

public class MaoDeObra {
    private String descricao;
    private double valor;
    private double horas;
    private Mecanico mecanico;

    public MaoDeObra() {}

    public MaoDeObra(String descricao, double valor, double horas, Mecanico mecanico) {
        //AJUSTE(REQ20): checa se mecanico tá ocupado, ou disponivel!
        if (!mecanico.isDisponivel()) {
            throw new IllegalArgumentException("Mecânico ocupado!");
        }
        this.descricao = descricao;
        this.valor = valor;
        this.horas = horas;
        this.mecanico = mecanico;
    }

    public double calcularCusto() {
        return valor * horas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getHoras() {
        return horas;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }
}
