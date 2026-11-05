import javax.swing.SwingUtilities;

import view.TelaConexao;

/**
 * PONTO DE ENTRADA do sistema (o Netflix.jar chama este main).
 * Abre a tela de conexao; depois de conectar, ela abre o menu principal.
 */
public class Main {

    public static void main(String[] args) {
        // O Swing exige que as telas sejam criadas na thread de eventos (Event Dispatch Thread).
        SwingUtilities.invokeLater(() -> new TelaConexao().setVisible(true));
    }
}
