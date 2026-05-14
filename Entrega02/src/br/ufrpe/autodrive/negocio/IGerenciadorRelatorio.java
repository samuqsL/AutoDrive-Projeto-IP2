package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.Relatorio;

public interface IGerenciadorRelatorio {
    // Método que a Tela vai chamar para obter o "pacote" de dados
    public Relatorio gerarDadosRelatorio();
}
