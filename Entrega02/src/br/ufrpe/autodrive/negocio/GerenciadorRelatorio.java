package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.Relatorio; // Import correto
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.dados.IRepositorioOS;

public class GerenciadorRelatorio implements IGerenciadorRelatorio {
    private IRepositorioVendas repoVendas;
    private IRepositorioOS repoOS;

    // O construtor recebe os repositórios (Injeção de Dependência)
    public GerenciadorRelatorio(IRepositorioVendas repoVendas, IRepositorioOS repoOS) {
        this.repoVendas = repoVendas;
        this.repoOS = repoOS;
    }

    @Override
    public Relatorio gerarDadosRelatorio() {
        // Pega os dados brutos e cria o objeto Relatorio que tem as lógicas de filtro
        return new Relatorio(
            repoVendas.listarTodasVendas(), 
            repoOS.listarTodas()
        );
    }
}
