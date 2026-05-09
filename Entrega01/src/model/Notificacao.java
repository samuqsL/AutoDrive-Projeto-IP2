package model;

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

    // REQ10: revisão preventiva por tempo/km
    public boolean gerarAlerta() {
        int proxima = revisaoNumero + 1;
        // É mais intuitivo: "A quilometragem é maior ou igual ao alvo da próxima?"
        return (quilometragem >= proxima * 10000) || (mesesUso >= proxima * 12);
    }
    

    public void notificarCliente() {
        if (cliente == null || veiculo == null) {
        System.out.println("Erro: Falha ao enviar notificação. Dados incompletos.");
        return;
        }

        // Se não houver necessidade de revisão, encerra o método
        if (!gerarAlerta()) {
            return;
        }
        revisaoNumero++;
        
        System.out.println("ENVIANDO NOTIFICAÇÃO...");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Veículo: " + veiculo.getModelo());
        System.out.println("Data: " + data);
        System.out.println("KM atual: " + quilometragem);
        System.out.println("Mensagem: Revisão preventiva necessária.");

    }
}
