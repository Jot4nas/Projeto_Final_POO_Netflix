package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.RelatorioController;
import model.HistoricoVisualizacao;
import util.Conversor;

/**
 * RELATORIO da rotina principal: visualizacoes por periodo (e por perfil).
 *
 * Nao herda de TelaCrud porque nao e um cadastro: so tem filtros, uma tabela
 * (resultado do INNER JOIN) e os totais calculados pelo Controller.
 */
public class TelaRelatorio extends JFrame {

    private final RelatorioController controller = new RelatorioController();

    private final JTextField campoDataInicial = new JTextField("01/01/2025", 10);
    private final JTextField campoDataFinal = new JTextField(Conversor.hoje(), 10);
    private final JComboBox<String> comboPerfil = new JComboBox<>();
    private final JLabel labelResumo = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Início", "Perfil", "Conta", "Plano", "Título", "Minuto de parada", "Concluído", "Dispositivo"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    // Ultimo resultado gerado (usado pelo botao "Salvar .txt").
    private List<HistoricoVisualizacao> resultado = new ArrayList<>();

    // Filtros que foram USADOS para gerar o 'resultado' acima. O "Salvar .txt" usa estes valores e nao o
    // que estiver digitado nos campos agora, senao o arquivo teria linhas de um periodo e cabecalho de outro.
    private String filtroDataInicial = "";
    private String filtroDataFinal = "";
    private String filtroPerfil = RelatorioController.TODOS;

    public TelaRelatorio() {
        super("Relatório - Visualizações por período");

        JButton btnGerar = new JButton("Gerar relatório");
        JButton btnSalvar = new JButton("Salvar .txt");
        btnGerar.addActionListener(e -> gerar());
        btnSalvar.addActionListener(e -> salvar());

        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.add(new JLabel("Data inicial (dd/mm/aaaa):"));
        painelFiltros.add(campoDataInicial);
        painelFiltros.add(new JLabel("Data final:"));
        painelFiltros.add(campoDataFinal);
        painelFiltros.add(new JLabel("Perfil:"));
        painelFiltros.add(comboPerfil);
        painelFiltros.add(btnGerar);
        painelFiltros.add(btnSalvar);

        labelResumo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Topo: a barra de navegacao (igual em todas as telas) e, embaixo dela, os filtros.
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(new BarraNavegacao(this, "Relatório", null), BorderLayout.NORTH);
        topo.add(painelFiltros, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        add(labelResumo, BorderLayout.SOUTH);

        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // so ha uma janela por vez; fechar encerra o programa

        try {
            for (String opcao : controller.opcoesPerfis()) {
                comboPerfil.addItem(opcao);
            }
            gerar(); // ja abre mostrando o relatorio do periodo padrao
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pede o relatorio ao Controller e mostra as linhas e os totais. */
    private void gerar() {
        try {
            resultado = controller.gerar(campoDataInicial.getText(), campoDataFinal.getText(),
                    (String) comboPerfil.getSelectedItem());
        } catch (RuntimeException e) { // validacao (datas) ou erro de banco
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        filtroDataInicial = campoDataInicial.getText();
        filtroDataFinal = campoDataFinal.getText();
        filtroPerfil = (String) comboPerfil.getSelectedItem();

        modelo.setRowCount(0);
        for (HistoricoVisualizacao h : resultado) {
            modelo.addRow(new Object[]{h.getIdVisualizacao(), Conversor.formatarDataHora(h.getDataInicio()),
                    h.getNomePerfil(), h.getEmailConta(), h.getNomePlano(), h.getNomeTitulo(), h.getMinutoParada(),
                    Conversor.simNao(h.isConcluido()), h.getDispositivo()});
        }
        labelResumo.setText(controller.resumo(resultado));
    }

    /** Abre a janela "Salvar como" e pede ao Controller para gravar o .txt. */
    private void salvar() {
        JFileChooser escolher = new JFileChooser();
        escolher.setSelectedFile(new File("relatorio_visualizacoes.txt"));
        if (escolher.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String mensagem = controller.salvarTxt(escolher.getSelectedFile().getAbsolutePath(), resultado,
                    filtroDataInicial, filtroDataFinal, filtroPerfil);
            JOptionPane.showMessageDialog(this, mensagem);
        }
    }
}
