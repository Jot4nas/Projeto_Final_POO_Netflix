package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.ConexaoController;

/** Tela Inicio (menu principal). Cada botao abre uma tela; o fluxo da apresentacao e de cima para baixo. */
public class TelaPrincipal extends JFrame {

    private final ConexaoController conexaoController = new ConexaoController();

    public TelaPrincipal() {
        super("Netflix - Sistema de Gerenciamento");

        JButton btnPlanos = new JButton("1. Planos");
        JButton btnContas = new JButton("2. Contas");
        JButton btnPerfis = new JButton("3. Perfis");
        JButton btnTitulos = new JButton("4. Títulos");
        JButton btnHistorico = new JButton("5. Histórico de Visualização");
        JButton btnRelatorio = new JButton("6. Relatório: visualizações por período");
        JButton btnRestaurar = new JButton("Restaurar dados");

        // Cada clique cria a tela e a mostra. As telas tratam os erros de banco sozinhas.
        btnPlanos.addActionListener(e -> Navegador.ir(this, new TelaPlano()));
        btnContas.addActionListener(e -> Navegador.ir(this, new TelaConta()));
        btnPerfis.addActionListener(e -> Navegador.ir(this, new TelaPerfil()));
        btnTitulos.addActionListener(e -> Navegador.ir(this, new TelaTitulo()));
        btnHistorico.addActionListener(e -> Navegador.ir(this, new TelaHistorico()));
        btnRelatorio.addActionListener(e -> Navegador.ir(this, new TelaRelatorio()));
        btnRestaurar.addActionListener(e -> restaurar());

        JPanel painel = new JPanel(new GridLayout(0, 1, 8, 8)); // 1 coluna, um botao por linha
        painel.setBorder(BorderFactory.createEmptyBorder(15, 150, 15, 150));
        painel.add(btnPlanos);
        painel.add(btnContas);
        painel.add(btnPerfis);
        painel.add(btnTitulos);
        painel.add(btnHistorico);
        painel.add(btnRelatorio);

        // A barra do topo e a mesma de todas as telas; aqui acrescentamos o botao "Restaurar dados" a direita.
        add(new BarraNavegacao(this, "Início", btnRestaurar), BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);
        pack();
        setSize(Math.max(getWidth(), 950), Math.max(getHeight(), 450)); // largura para caber a barra
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // fechar o menu encerra o programa
    }

    /** Apaga o banco e cria de novo com os dados do script (util antes da apresentacao). */
    private void restaurar() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Isso apaga TODOS os dados e volta aos dados de exemplo. Continuar?",
                "Restaurar dados de exemplo", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, conexaoController.restaurarDadosExemplo());
        }
    }
}
