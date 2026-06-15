package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Pecas;
import java.util.List;

public interface IGerenciadorOficina {
    
    boolean abrirOS(String cpfCliente, String chassiVeiculo, String codigoPeca, int quantidadePeca, String descricaoMaoDeObra, double valorMaoDeObra) throws Exception;
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, br.ufrpe.autodrive.negocio.beans.MaoDeObra servico);
    boolean finalizarServico(int numeroOS);
    List<Pecas> listarPecasDisponiveis();
}
