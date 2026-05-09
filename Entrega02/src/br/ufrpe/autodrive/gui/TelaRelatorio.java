/**
 * ORIENTAÇÃO DE IMPLEMENTAÇÃO (PADRÃO GUI):
 * * 1. MÉTODO exibir(): Deve conter um loop (while) que mantém a tela ativa 
 * até que o usuário digite '0'. Use um switch/case para ler as opções.
 
 * * 2. LÓGICA DE NAVEGAÇÃO: Ao escolher '0', o loop deve encerrar. Isso fará 
 * com que o método termine e o controle retorne automaticamente para o MenuPrincipal.
 
 * * 3. MÉTODOS "BOTAO": As opções do switch devem chamar os métodos específicos 
 * da classe (ex: BotaoRealizarVenda).
 
 * * 4. FLUXO DE DADOS: O método da tela lê os dados (Scanner) -> Chama o Gerenciador 
 * (Control) -> Recebe um boolean -> A tela decide qual mensagem exibir (if/else).
 * * NOTA: Nenhuma lógica de negócio ou print deve existir fora das classes GUI.
 */
package br.ufrpe.autodrive.gui;

public class TelaRelatorio {

 //exibir() é Metodo pra todas as classes! (painelzinho com opções(1, 2, ..., 0[sair]) 
 //Fiz um na classe "TelaVenda" --- se quiserem uma referência.
 public void exibir(){//Metodo//}

}
