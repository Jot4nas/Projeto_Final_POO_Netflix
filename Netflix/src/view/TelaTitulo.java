package view;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import controller.TituloController;
import model.Titulo;
import util.Conversor;

/** Tela do CRUD de Titulo (catalogo). */
public class TelaTitulo extends TelaCrud {

    private final TituloController controller = new TituloController();

    public TelaTitulo() {
        super("Títulos", new String[]{"ID", "Nome", "Sinopse", "Tipo", "Ano lançamento", "Classificação",
                "Duração (min)", "Idioma original", "Data adição", "Disponível"});

        adicionarCampo("Nome", new JTextField());
        adicionarCampo("Sinopse", new JTextField());
        adicionarCampo("Tipo", new JComboBox<>(new String[]{"Filme", "Serie", "Documentario", "Stand-up"}));
        adicionarCampo("Ano lançamento", new JTextField());
        adicionarCampo("Classificação", new JComboBox<>(new String[]{"L", "10", "12", "14", "16", "18"}));
        adicionarCampo("Duração (min)", new JTextField());
        adicionarCampo("Idioma original", new JTextField());
        adicionarCampo("Data adição", new JTextField());
        adicionarCampo("Disponível", new JComboBox<>(new String[]{"Sim", "Nao"}));
        iniciar();
    }

    @Override
    protected void carregarTabela() {
        for (Titulo t : controller.listar()) {
            modelo.addRow(new Object[]{t.getIdTitulo(), t.getNome(), t.getSinopse(), t.getTipo(),
                    t.getAnoLancamento(), t.getClassificacaoIndicativa(), t.getDuracaoMinutos(),
                    t.getIdiomaOriginal(), t.getDataAdicaoCatalogo(), Conversor.simNao(t.isDisponivel())});
        }
    }

    @Override
    protected String inserir() {
        return controller.inserir(valor("Nome"), valor("Sinopse"), valor("Tipo"), valor("Ano lançamento"),
                valor("Classificação"), valor("Duração (min)"), valor("Idioma original"),
                valor("Data adição"), valor("Disponível"));
    }

    @Override
    protected String atualizar(String id) {
        return controller.atualizar(id, valor("Nome"), valor("Sinopse"), valor("Tipo"), valor("Ano lançamento"),
                valor("Classificação"), valor("Duração (min)"), valor("Idioma original"),
                valor("Data adição"), valor("Disponível"));
    }

    @Override
    protected String eliminar(String id) {
        return controller.eliminar(id);
    }

    @Override
    protected void definirPadroes() {
        definirValor("Data adição", Conversor.hoje());
    }
}
