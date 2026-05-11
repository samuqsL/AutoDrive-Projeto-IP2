package br.ufrpe.autodrive.negocio;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

public interface IGerenciadorRelatorio {
    List<Venda> gerarRelatorioVendas();
    List<OrdemServico> gerarRelatorioOS();
}
