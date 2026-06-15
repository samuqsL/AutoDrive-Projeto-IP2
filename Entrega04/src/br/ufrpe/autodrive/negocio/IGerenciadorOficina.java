package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Pecas;

public interface IGerenciadorOficina {
    
    // FUNÇÃO LOCALIZADA: Assinatura original mantida para os testes do Main funcionarem
    boolean abrirOS(String cpfCliente, String chassiVeiculo);
    
    // 🟢 NOVA ASSINATURA: Adicionada para a Tela FXML suportar o fluxo completo (Peças, Óleo e Mão de Obra)
    boolean abrirOSCompleta(String cpfCliente, String chassiVeiculo, Pecas peca, int qtd, String descServico, double valorServico);
    
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico);
    
    boolean finalizarServico(int numeroOS);
}
