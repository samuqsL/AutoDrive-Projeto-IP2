package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Notificacao;
import br.ufrpe.autodrive.negocio.beans.Venda;
import java.util.List;

public interface IGerenciadorVenda {
    // Agora recebe o nome do vendedor para buscá-lo no repositório
    boolean efetuarVenda(String cpfCliente, String nomeVendedor, double entrada);
    List<Notificacao> listarAlertasRevisao();
    Venda procurarVenda(String cpf);
    void removerVenda();
}
