package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Pecas;

public interface IGerenciadorOficina {
    
    // FUNÇÃO LOCALIZADA: Assinatura modificada para suportar criação automatizada
    boolean abrirOS(String cpfCliente, String chassiVeiculo);
    
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico);
    
    boolean finalizarServico(int numeroOS);
}
