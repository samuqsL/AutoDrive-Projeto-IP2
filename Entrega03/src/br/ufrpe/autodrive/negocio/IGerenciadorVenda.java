package br.ufrpe.autodrive.negocio;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.Notificacao;

public interface IGerenciadorVenda {

    boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada);
    
    // 🟢 NOVA SOBRECARGA PARA RECEBER A DATA DA TELA
    boolean efetuarVenda(int numero, String cpfCliente, String chassi, String nomeVendedor, double entrada, java.time.LocalDateTime dataDigitada);

    List<Notificacao> listarAlertasRevisao();
    Venda procurarVenda(int numero);
    void removerVenda(int numero);
}
