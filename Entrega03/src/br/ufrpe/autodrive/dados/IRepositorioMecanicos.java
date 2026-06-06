package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Mecanico;
import java.util.List;

public interface IRepositorioMecanicos {
    //CRUDS padrão dos repositórios!
    void adicionarMecanico(Mecanico m);
    Mecanico procurarMecanico(String nome); // Busca pelo nome, já que Mecanico não tem CPF visível na Main
    void removerMecanico(String nome);
    List<Mecanico> listarTodos();
}
