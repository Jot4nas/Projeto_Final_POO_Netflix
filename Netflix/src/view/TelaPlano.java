package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import controller.PlanoController;
import model.Plano;
import util.Conversor;

/** Tela do CRUD de Plano. Estrutura igual para todas as telas: campos + 4 metodos que chamam o Controller. */
public class TelaPlano extends TelaCrud {

    private final PlanoController controller = new PlanoController();

    // Guardados em atributos porque a descricao automatica precisa ler estes dois campos.
    private final JComboBox<String> comboTelas = new JComboBox<>(new String[]{"1", "2", "4"});
    private final JComboBox<String> comboQualidade = new JComboBox<>(new String[]{"SD", "HD", "Full HD", "4K UHD"});

    public TelaPlano() {
        super("Planos", new String[]{"ID", "Nome", "Descrição", "Valor mensal", "Qualidade máxima",
                "Telas simultâneas", "Permite download"});

        // A descricao nao e digitada: o campo fica so para leitura e e preenchido pelo Controller.
        JTextField campoDescricao = new JTextField();
        campoDescricao.setEditable(false);

        adicionarCampo("Nome", new JTextField());
        adicionarCampo("Descrição (automática)", campoDescricao);
        adicionarCampo("Valor mensal", new JTextField());
        adicionarCampo("Qualidade máxima", comboQualidade);
        adicionarCampo("Telas simultâneas", comboTelas);
        adicionarCampo("Permite download", new JComboBox<>(new String[]{"Sim", "Nao"}));

        // Sempre que o usuario escolhe a qualidade ou o numero de telas (combos), a
        // descricao e recalculada. A tela so PEDE o texto ao Controller; a regra
        // de como a descricao e montada fica no Controller (MVC).
        comboQualidade.addActionListener(e -> atualizarDescricao());
        comboTelas.addActionListener(e -> atualizarDescricao());
        iniciar();
    }

    private void atualizarDescricao() {
        definirValor("Descrição (automática)",
                controller.gerarDescricao((String) comboTelas.getSelectedItem(), (String) comboQualidade.getSelectedItem()));
    }

    /** Preenche o DefaultTableModel: para cada Plano da lista do DAO, uma linha (Object[]) na tabela. */
    @Override
    protected void carregarTabela() {
        for (Plano p : controller.listar()) {
            modelo.addRow(new Object[]{p.getIdPlano(), p.getNome(), p.getDescricao(), p.getValorMensal(),
                    p.getQualidadeMaxima(), p.getTelasSimultaneas(), Conversor.simNao(p.isPermiteDownload())});
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Nome"), valor("Valor mensal"),
                valor("Qualidade máxima"), valor("Telas simultâneas"), valor("Permite download"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Nome"), valor("Valor mensal"),
                valor("Qualidade máxima"), valor("Telas simultâneas"), valor("Permite download"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

    @Override
    protected void definirPadroes() {
        atualizarDescricao(); // campo de telas vazio -> descricao vazia
    }
}
