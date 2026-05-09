package model;

//classe filha de "Veiculo"
public class VeiculoSeminovo extends Veiculo {
	//atributos [são herdados da classe Veiculo]!
	
	//construtor principal
	public VeiculoSeminovo(String chassi, String renavam, String modelo, int ano, double preco, double quilometragem) {
		super(chassi, renavam, modelo, ano, preco, quilometragem);
		if (quilometragem <= 0) {
            throw new IllegalArgumentException("Erro REQ17: Todo Seminovo precisa de quilometragem inicial");
        }
	}
}
