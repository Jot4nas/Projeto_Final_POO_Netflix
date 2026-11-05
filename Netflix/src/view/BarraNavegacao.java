package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Barra do topo, igual em todas as telas: a marca NETFLIX e um botao para cada tela
 * (Inicio, Planos, Contas, Perfis, Titulos e Historico). O botao da tela
 * onde o usuario esta fica desabilitado.
 *
 * Cada botao troca de tela pelo Navegador (a tela nova abre no mesmo lugar da atual).
 * Esta classe so monta a interface: nao tem SQL nem regra de negocio.
 */
public class BarraNavegacao extends JPanel {

    /**
     * @param dona    a janela onde a barra esta (ela sera trocada pela tela escolhida)
     * @param atual   nome da tela atual, igual ao texto do botao ("Início", "Planos"...)
     * @param direita componente opcional no canto direito (ex.: botao "Restaurar dados"); pode ser null
     */
    public BarraNavegacao(JFrame dona, String atual, Component direita) {
        super(new BorderLayout());

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        esquerda.add(new JLabel("NETFLIX"));
        esquerda.add(botao("Início", atual, e -> Navegador.ir(dona, new TelaPrincipal())));
        esquerda.add(botao("Planos", atual, e -> Navegador.ir(dona, new TelaPlano())));
        esquerda.add(botao("Contas", atual, e -> Navegador.ir(dona, new TelaConta())));
        esquerda.add(botao("Perfis", atual, e -> Navegador.ir(dona, new TelaPerfil())));
        esquerda.add(botao("Títulos", atual, e -> Navegador.ir(dona, new TelaTitulo())));
        esquerda.add(botao("Histórico", atual, e -> Navegador.ir(dona, new TelaHistorico())));
        add(esquerda, BorderLayout.WEST);

        if (direita != null) {
            JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            painelDireita.add(direita);
            add(painelDireita, BorderLayout.EAST);
        }
    }

    private JButton botao(String texto, String atual, ActionListener acao) {
        JButton b = new JButton(texto);
        if (texto.equals(atual)) {
            b.setEnabled(false); // o botao da tela atual nao faz nada
        } else {
            b.addActionListener(acao);
        }
        return b;
    }
}
