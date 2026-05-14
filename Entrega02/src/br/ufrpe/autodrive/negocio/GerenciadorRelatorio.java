package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Relatorio;
import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.dados.IRepositorioOS;

public class GerenciadorRelatorio implements IGerenciadorRelatorio {
    private IRepositorioVendas repoVendas;
    private IRepositorioOS repoOS;

    // O construtor recebe as interfaces dos repositórios (Injeção de Dependência)
    public GerenciadorRelatorio(IRepositorioVendas repoVendas, IRepositorioOS repoOS) {
        this.repoVendas = repoVendas;
        this.repoOS = repoOS;
    }

    @Override
    public Relatorio gerarDadosRelatorio() {
        // Buscamos as listas completas (que já retornam cópias seguras nos repositórios)
        // e instanciamos o objeto Relatorio que contém as regras de filtro e cálculo.
        return new Relatorio(
            repoVendas.listarTodasVendas(), 
            repoOS.listarTodas()
        );
    }
}
