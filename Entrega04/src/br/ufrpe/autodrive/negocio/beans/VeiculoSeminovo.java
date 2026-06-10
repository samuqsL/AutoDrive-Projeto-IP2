package br.ufrpe.autodrive.negocio.beans;

//classe filha de "Veiculo"
public class VeiculoSeminovo extends Veiculo {
	
	// Apenas adiciona o ID específico de versão para o VeiculoSeminovo (Serialization/Persistence)*
	private static final long serialVersionUID = 1L;
	
	//atributos [são herdados da classe Veiculo]!
	
	//construtor principal
	public VeiculoSeminovo(String chassi, String renavam, String modelo, int ano, double preco, double quilometragem) {
		super(chassi, renavam, modelo, ano, preco, quilometragem);
		if (quilometragem <= 0) {
            throw new IllegalArgumentException("Erro REQ17: Todo Seminovo precisa de quilometragem inicial");
        }
	}
}
