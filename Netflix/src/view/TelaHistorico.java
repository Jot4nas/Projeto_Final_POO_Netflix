package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import controller.HistoricoController;
import model.HistoricoVisualizacao;
import util.Conversor;

/**
 * Tela do CRUD PRINCIPAL: Historico de Visualizacao.
 * A tabela mostra o resultado do INNER JOIN (listarComNomes): as colunas "Perfil"
 * e "Título" exibem "1 - Ana" e "1 - Stranger Things", e nao so os IDs guardados no banco.
 */
public class TelaHistorico extends TelaCrud {

    private final HistoricoController controller = new HistoricoController();
    private final JComboBox<String> comboPerfil = new JComboBox<>();
    private final JComboBox<String> comboTitulo = new JComboBox<>();

    public TelaHistorico() {
        super("Histórico de Visualização", new String[]{"ID", "Perfil", "Título", "Início",
                "Última visualização", "Minuto de parada", "Concluído", "Dispositivo", "Conta", "Plano"});

        adicionarCampo("Perfil", comboPerfil);
        adicionarCampo("Título", comboTitulo);
        adicionarCampo("Início", new JTextField());
        adicionarCampo("Última visualização", new JTextField());
        adicionarCampo("Minuto de parada", new JTextField());
        adicionarCampo("Concluído", new JComboBox<>(new String[]{"Nao", "Sim"}));
        adicionarCampo("Dispositivo", new JComboBox<>(new String[]{"TV", "Celular", "Notebook", "Tablet", "Console"}));
        iniciar();
    }

    @Override
    protected void carregarCombos() {
        preencherCombo(comboPerfil, controller.opcoesPerfis());
        preencherCombo(comboTitulo, controller.opcoesTitulos());
    }

    /**
     * Cada objeto da lista do INNER JOIN vira uma linha. getNomePerfil()/getNomeTitulo()
     * foram preenchidos pelo JOIN. O ID do registro (coluna 0) e o que os botoes
     * Atualizar e Eliminar usam.
     */
    @Override
    protected void carregarTabela() {
        for (HistoricoVisualizacao h : controller.listarComNomes()) {
            modelo.addRow(new Object[]{h.getIdVisualizacao(),
                    Conversor.opcao(h.getIdPerfil(), h.getNomePerfil()),
                    Conversor.opcao(h.getIdTitulo(), h.getNomeTitulo()),
                    Conversor.formatarDataHora(h.getDataInicio()),
                    Conversor.formatarDataHora(h.getDataUltimaVisualizacao()),
                    h.getMinutoParada(), Conversor.simNao(h.isConcluido()), h.getDispositivo(),
                    h.getEmailConta(), h.getNomePlano()}); // Conta e Plano: so exibicao (vem do JOIN)
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Perfil"), valor("Título"), valor("Início"),
                valor("Última visualização"), valor("Minuto de parada"), valor("Concluído"), valor("Dispositivo"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Perfil"), valor("Título"), valor("Início"),
                valor("Última visualização"), valor("Minuto de parada"), valor("Concluído"), valor("Dispositivo"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

    @Override
    protected void definirPadroes() {
        definirValor("Início", Conversor.agora());
        definirValor("Última visualização", Conversor.agora());
        definirValor("Minuto de parada", "0");
    }
}
