package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.ConexaoController;

/**
 * Primeira tela do sistema: pede os dados do MySQL de quem esta rodando.
 * Os campos ja vem com o que foi salvo da ultima vez (config.properties).
 * Ao conectar, o banco "netflix" e criado sozinho se ainda nao existir.
 */
public class TelaConexao extends JFrame {

    private final ConexaoController controller = new ConexaoController();

    private final JTextField campoServidor = new JTextField(controller.servidorSalvo());
    private final JTextField campoPorta = new JTextField(controller.portaSalva());
    private final JTextField campoUsuario = new JTextField(controller.usuarioSalvo());
    private final JPasswordField campoSenha = new JPasswordField(controller.senhaSalva());

    public TelaConexao() {
        super("Netflix - Conectar ao MySQL");

        JPanel painel = new JPanel(new GridLayout(0, 2, 5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(new JLabel("Servidor:"));
        painel.add(campoServidor);
        painel.add(new JLabel("Porta:"));
        painel.add(campoPorta);
        painel.add(new JLabel("Usuário:"));
        painel.add(campoUsuario);
        painel.add(new JLabel("Senha do MySQL:"));
        painel.add(campoSenha);

        JButton btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(e -> conectar());
        getRootPane().setDefaultButton(btnConectar); // Enter tambem conecta

        add(new JLabel("  Informe os dados do MySQL instalado neste computador:"), BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);
        add(btnConectar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void conectar() {
        String erro = controller.conectar(campoServidor.getText(), campoPorta.getText(),
                campoUsuario.getText(), new String(campoSenha.getPassword()));
        if (erro == null) {          // deu certo: fecha esta tela e abre o menu
            if (controller.getAviso() != null) {
                JOptionPane.showMessageDialog(this, controller.getAviso(), "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            Navegador.trocar(this, new TelaPrincipal()); // abre o menu com fade e fecha esta tela
        } else {
            JOptionPane.showMessageDialog(this, erro, "Erro de conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
}
