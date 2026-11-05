package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import controller.ContaController;
import model.Conta;
import util.Conversor;

/** Tela do CRUD de Conta. O combo de planos e preenchido com opcoes que o Controller fornece. */
public class TelaConta extends TelaCrud {

    private final ContaController controller = new ContaController();
    private final JComboBox<String> comboPlano = new JComboBox<>(); // itens vem do banco (carregarCombos)

    public TelaConta() {
        super("Contas", new String[]{"ID", "Plano", "E-mail", "Senha", "Telefone", "País",
                "Status"});

        adicionarCampo("Plano", comboPlano);
        adicionarCampo("E-mail", new JTextField());
        adicionarCampo("Senha", new JTextField());
        adicionarCampo("Telefone", new JTextField());
        adicionarCampo("País", new JTextField());
        adicionarCampo("Status", new JComboBox<>(new String[]{"Ativa", "Suspensa", "Cancelada"}));
        iniciar();
    }

    @Override
    protected void carregarCombos() {
        preencherCombo(comboPlano, controller.opcoesPlanos());
    }

    /** A coluna Plano mostra "3 - Premium" (id + nome vindo do INNER JOIN), igual ao combo. */
    @Override
    protected void carregarTabela() {
        for (Conta c : controller.listar()) {
            modelo.addRow(new Object[]{c.getIdConta(), Conversor.opcao(c.getIdPlano(), c.getNomePlano()),
                    c.getEmail(), c.getSenhaHash(), c.getTelefone(), c.getPais(),
                    c.getStatus()});
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Plano"), valor("E-mail"), valor("Senha"), valor("Telefone"),
                valor("País"), valor("Status"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Plano"), valor("E-mail"), valor("Senha"), valor("Telefone"),
                valor("País"), valor("Status"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

    @Override
    protected void definirPadroes() {
        definirValor("País", "BR");
    }
}
