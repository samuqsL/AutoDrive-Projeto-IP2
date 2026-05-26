package br.ufrpe.autodrive.negocio.beans;

public class Notificacao {
    private double quilometragem;
    private int revisaoNumero;
    private String data;
    private int mesesUso;
    private Cliente cliente;
    private Veiculo veiculo;

    public Notificacao(double quilometragem, int revisaoNumero, String data, int mesesUso, Cliente cliente, Veiculo veiculo) {
        this.quilometragem = quilometragem;
        this.revisaoNumero = revisaoNumero;
        this.data = data;
        this.mesesUso = mesesUso;
        this.cliente = cliente;
        this.veiculo = veiculo;
    }
    public Cliente getCliente() { return cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public double getQuilometragem() { return quilometragem; }

    // REQ10: revisão preventiva por tempo/km
    public boolean gerarAlerta() {
        int proxima = revisaoNumero + 1;
        // É mais intuitivo: "A quilometragem é maior ou igual ao alvo da próxima?"
        return (quilometragem >= proxima * 10000) || (mesesUso >= proxima * 12);
    }
}
