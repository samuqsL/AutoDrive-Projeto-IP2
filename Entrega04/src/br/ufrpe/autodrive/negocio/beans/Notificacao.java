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
    public String getData() { return data; }

    // REQ10: revisão preventiva por tempo/km
    public boolean gerarAlerta() {
        // Multiplica pelo número da próxima revisão para o cálculo fazer sentido matematicamente
        int proxima = this.revisaoNumero + 1; 
        
        // Regra flexível para testes: dispara com 5.000km ou 6 meses na primeira revisão
        boolean precisaPorKm = this.quilometragem >= (proxima * 5000.0); 
        boolean precisaPorTempo = this.mesesUso >= (proxima * 6);

        return precisaPorKm || precisaPorTempo;
    }
}