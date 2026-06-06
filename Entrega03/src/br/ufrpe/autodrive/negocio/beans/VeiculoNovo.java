package br.ufrpe.autodrive.negocio.beans;
//classe filha de "Veiculos"
public class VeiculoNovo extends Veiculo {
	
	// Apenas adiciona o ID específico de versão para o VeiculoNovo (Serialization/Persistence)*
	private static final long serialVersionUID = 1L;
	
	//construtor
	public VeiculoNovo(String chassi, String renavam, String modelo, int ano, double preco) {
			super(chassi, renavam, modelo, ano, preco, 0.0);
	}
}
