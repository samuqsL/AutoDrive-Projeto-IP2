package br.ufrpe.autodrive.negocio;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.Notificacao;

public interface IGerenciadorVenda {

    // 1. Efetuar Venda: Agora recebe o numero da venda e as Strings para busca
    boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada);

    // 2. Listar Alertas: Retorna a lista de notificações para a TelaVenda
    List<Notificacao> listarAlertasRevisao();

    // 3. Procurar: Ajustado para buscar pelo número único
    Venda procurarVenda(int numero);

    // 4. Remover: Ajustado para remover por número (identificador único)
    void removerVenda(int numero);
}
