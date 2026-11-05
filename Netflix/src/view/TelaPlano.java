package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controller.PlanoController;
import model.Plano;
import util.Conversor;

/** Tela do CRUD de Plano. Estrutura igual para todas as telas: campos + 4 metodos que chamam o Controller. */
public class TelaPlano extends TelaCrud {

    private final PlanoController controller = new PlanoController();

    // Guardados em atributos porque a descricao automatica precisa ler estes dois campos.
    private final JTextField campoTelas = new JTextField();
    private final JComboBox<String> comboQualidade = new JComboBox<>(new String[]{"SD", "HD", "Full HD", "4K UHD"});

    public TelaPlano() {
        super("Planos", new String[]{"ID", "Nome", "Descrição", "Valor mensal", "Qualidade máxima",
                "Telas simultâneas", "Permite download", "Data criação"});

        // A descricao nao e digitada: o campo fica so para leitura e e preenchido pelo Controller.
        JTextField campoDescricao = new JTextField();
        campoDescricao.setEditable(false);

        adicionarCampo("Nome", new JTextField());
        adicionarCampo("Descrição (automática)", campoDescricao);
        adicionarCampo("Valor mensal", new JTextField());
        adicionarCampo("Qualidade máxima", comboQualidade);
        adicionarCampo("Telas simultâneas", campoTelas);
        adicionarCampo("Permite download", new JComboBox<>(new String[]{"Sim", "Nao"}));
        adicionarCampo("Data criação", new JTextField());

        // Sempre que o usuario muda a qualidade (combo) ou digita o numero de telas,
        // a descricao e recalculada. A tela so PEDE o texto ao Controller; a regra
        // de como a descricao e montada fica no Controller (MVC).
        comboQualidade.addActionListener(e -> atualizarDescricao());

        // DocumentListener avisa quando o TEXTO do campo muda, seja digitando ou pelo
        // codigo (ex.: ao clicar numa linha da tabela, que preenche os campos sozinha).
        campoTelas.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarDescricao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarDescricao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarDescricao();
            }
        });
        iniciar();
    }

    private void atualizarDescricao() {
        definirValor("Descrição (automática)",
                controller.gerarDescricao(campoTelas.getText(), (String) comboQualidade.getSelectedItem()));
    }

    /** Preenche o DefaultTableModel: para cada Plano da lista do DAO, uma linha (Object[]) na tabela. */
    @Override
    protected void carregarTabela() {
        for (Plano p : controller.listar()) {
            modelo.addRow(new Object[]{p.getIdPlano(), p.getNome(), p.getDescricao(), p.getValorMensal(),
                    p.getQualidadeMaxima(), p.getTelasSimultaneas(), Conversor.simNao(p.isPermiteDownload()),
                    p.getDataCriacao()});
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Nome"), valor("Valor mensal"),
                valor("Qualidade máxima"), valor("Telas simultâneas"), valor("Permite download"),
                valor("Data criação"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Nome"), valor("Valor mensal"),
                valor("Qualidade máxima"), valor("Telas simultâneas"), valor("Permite download"),
                valor("Data criação"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

    @Override
    protected void definirPadroes() {
        definirValor("Data criação", Conversor.hoje());
        atualizarDescricao(); // campo de telas vazio -> descricao vazia
    }
}
