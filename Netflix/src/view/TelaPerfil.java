package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import controller.PerfilController;
import model.Perfil;
import util.Conversor;

/** Tela do CRUD de Perfil. O combo de contas vem do Controller. */
public class TelaPerfil extends TelaCrud {

    private final PerfilController controller = new PerfilController();
    private final JComboBox<String> comboConta = new JComboBox<>();

    public TelaPerfil() {
        super("Perfis", new String[]{"ID", "Conta", "Nome", "Avatar", "Perfil infantil", "PIN"});

        adicionarCampo("Conta", comboConta);
        adicionarCampo("Nome", new JTextField());
        adicionarCampo("Avatar", new JComboBox<>(new String[]{"azul", "vermelho", "verde", "amarelo"})); // so escolhe, nao digita
        adicionarCampo("Perfil infantil", new JComboBox<>(new String[]{"Nao", "Sim"}));
        adicionarCampo("PIN", new JTextField());
        iniciar();
    }

    @Override
    protected void carregarCombos() {
        preencherCombo(comboConta, controller.opcoesContas());
    }

    /** A coluna Conta mostra "1 - ana.silva@email.com" (id + e-mail vindo do INNER JOIN). */
    @Override
    protected void carregarTabela() {
        for (Perfil p : controller.listar()) {
            modelo.addRow(new Object[]{p.getIdPerfil(), Conversor.opcao(p.getIdConta(), p.getEmailConta()),
                    p.getNome(), p.getAvatar(), Conversor.simNao(p.isPerfilInfantil()),
                    p.getPin()});
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Conta"), valor("Nome"), valor("Avatar"), valor("Perfil infantil"), valor("PIN"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Conta"), valor("Nome"), valor("Avatar"), valor("Perfil infantil"), valor("PIN"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

}
