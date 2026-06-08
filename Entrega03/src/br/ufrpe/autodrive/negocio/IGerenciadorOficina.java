package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import br.ufrpe.autodrive.negocio.beans.Mecanico;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import java.util.List;

public interface IGerenciadorOficina {
    
    int abrirOS(String cpfCliente, String chassiVeiculo);
    
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico);
    boolean finalizarServico(int numeroOS);
    
    void adicionarMecanico(Mecanico m);
    List<OrdemServico> listarHistoricoOSFinalizadas();
}
