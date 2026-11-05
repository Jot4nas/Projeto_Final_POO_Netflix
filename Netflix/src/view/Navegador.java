package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Troca de tela sem "janelas empilhadas": a tela nova abre NO MESMO LUGAR e com o MESMO TAMANHO da
 * atual, a atual e fechada e o conteudo da nova aparece com um fade-in curto.
 *
 * Por que nao usar Window.setOpacity? Porque ele nao funciona em janela com a barra de titulo do
 * Windows (lanca IllegalComponentStateException). Entao o fade e feito DENTRO da janela, com um
 * "vidro" (glass pane) pintado por cima do conteudo, que vai ficando transparente.
 */
public final class Navegador {

    private Navegador() {
    }

    /** Fecha 'atual' e mostra 'nova' no mesmo lugar e com o mesmo tamanho (e maximizada, se a atual estava). */
    public static void ir(JFrame atual, JFrame nova) {
        nova.setBounds(atual.getBounds());
        nova.setExtendedState(atual.getExtendedState());
        mostrarComFade(nova);
        atual.dispose(); // a tela antiga some; a nova ja esta no lugar dela
    }

    /** Fecha 'origem' e abre 'nova' centralizada na tela (usado so da tela de conexao para o menu). */
    public static void trocar(JFrame origem, JFrame nova) {
        nova.setLocationRelativeTo(null);
        mostrarComFade(nova);
        origem.dispose();
    }

    private static void mostrarComFade(JFrame nova) {
        Veu veu = new Veu(nova.getContentPane().getBackground()); // veu da mesma cor do fundo da tela
        nova.setGlassPane(veu);
        veu.setVisible(true);
        nova.setVisible(true);

        // A cada 16 ms (uns 60 quadros por segundo) o veu fica um pouco mais transparente; em 9 passos some.
        Timer relogio = new Timer(16, null);
        relogio.addActionListener(e -> {
            veu.alfa -= 0.11f;
            if (veu.alfa <= 0f) {
                relogio.stop();
                veu.setVisible(false); // o vidro sai de cima e a tela volta a receber cliques
            }
            veu.repaint();
        });
        relogio.start();
    }

    /** Painel transparente que cobre a janela com uma cor; o 'alfa' vai de 1 (cobre tudo) a 0. */
    private static final class Veu extends JPanel {
        float alfa = 1f;
        private final Color cor;

        Veu(Color cor) {
            this.cor = cor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(),
                    Math.max(0, Math.min(255, (int) (alfa * 255)))));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
