package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Pecas;

public interface IGerenciadorOficina {
    // Agora recebe CPF e Chassi para buscar os objetos no repositório
    boolean abrirOS(int numero, String dataAbertura, String cpfCliente, String chassiVeiculo);
    
    // Métodos de registro
    boolean registrarPecaNaOS(int numeroOS, Pecas peca, int quantidade);
    boolean registrarServicoNaOS(int numeroOS, MaoDeObra servico);
    
    // Finalização
    boolean finalizarServico(int numeroOS);
}
