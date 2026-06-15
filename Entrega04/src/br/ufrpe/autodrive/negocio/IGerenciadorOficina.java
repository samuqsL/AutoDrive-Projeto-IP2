package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Pecas;

public interface IGerenciadorOficina {
    
    boolean abrirOS(String cpfCliente, String chassiVeiculo, String codigoPeca, int quantidadePeca, String descricaoMaoDeObra, double valorMaoDeObra);
    
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico);
    
    boolean finalizarServico(int numeroOS);
}
