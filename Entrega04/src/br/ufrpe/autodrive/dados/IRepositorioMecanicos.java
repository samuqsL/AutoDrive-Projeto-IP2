package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Mecanico;
import java.util.List;

public interface IRepositorioMecanicos {
    // CRUDS padrão dos repositórios
    void adicionarMecanico(Mecanico m);
    Mecanico procurarMecanico(String nome); 
    void removerMecanico(String nome);
    List<Mecanico> listarTodos();
    
    // Novo método para persistência de alterações de estado dos mecânicos
    void atualizarMecanico(Mecanico m);
}
