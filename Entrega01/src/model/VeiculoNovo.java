package model;
//classe filha de "Veiculos"
public class VeiculoNovo extends Veiculo {
	//construtor
	public VeiculoNovo(String chassi, String renavam, String modelo, int ano, double preco) {
			super(chassi, renavam, modelo, ano, preco, 0.0);
	}
}
