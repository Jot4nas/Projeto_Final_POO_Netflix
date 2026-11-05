package controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import dao.PlanoDAO;
import model.Plano;
import util.Conversor;

/**
 * CONTROLLER de Plano: o "meio de campo" entre View e DAO.
 *
 *   View  --(Strings digitadas)-->  Controller  --(objeto Plano)-->  DAO  --> MySQL
 *
 * O que o Controller faz:
 *  1) recebe os dados como String da View;
 *  2) valida e converte (Conversor);
 *  3) cria o Model (new Plano) e chama o DAO;
 *  4) devolve para a View uma MENSAGEM de resultado (sucesso ou erro).
 * A View nao sabe nada de banco; o DAO nao sabe nada de telas.
 * Os outros controllers seguem exatamente este mesmo padrao.
 */
public class PlanoController {

    private final PlanoDAO dao = new PlanoDAO();

    /** Devolve a lista para a View preencher o JTable. */
    public List<Plano> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            // listar devolve uma lista (nao uma mensagem), entao o erro sobe como
            // RuntimeException e a TelaCrud mostra a mensagem dele.
            throw new RuntimeException("Erro ao listar planos: " + e.getMessage(), e);
        }
    }

    public String inserir(String nome, String valorMensal, String qualidadeMaxima,
                          String telas, String permiteDownload) {
        try {
            dao.inserir(montar(nome, valorMensal, qualidadeMaxima, telas, permiteDownload));
            return "Plano inserido com sucesso!";
        } catch (IllegalArgumentException e) { // erro de validacao (Conversor)
            return e.getMessage();
        } catch (SQLException e) {             // erro do banco (inclusive MySQL desligado)
            return "Erro no banco de dados: " + e.getMessage();
        }
    }

    public String atualizar(String id, String nome, String valorMensal, String qualidadeMaxima,
                            String telas, String permiteDownload) {
        try {
            int idPlano = Conversor.paraId(id);
            Plano p = montar(nome, valorMensal, qualidadeMaxima, telas, permiteDownload);
            p.setIdPlano(idPlano);
            if (!dao.atualizar(p)) {
                return Conversor.REGISTRO_NAO_ENCONTRADO;
            }
            return "Plano atualizado com sucesso!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return "Erro no banco de dados: " + e.getMessage();
        }
    }

    public String eliminar(String id) {
        try {
            if (!dao.eliminar(Conversor.paraId(id))) {
                return Conversor.REGISTRO_NAO_ENCONTRADO;
            }
            return "Plano eliminado com sucesso!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            // 1451 = o MySQL recusou porque ha contas usando este plano (chave estrangeira).
            if (e.getErrorCode() == 1451) {
                return "Não é possível eliminar este plano: há contas que usam ele. "
                        + "Troque o plano dessas contas (ou elimine-as) antes.";
            }
            return "Erro no banco de dados: " + e.getMessage();
        }
    }

    /**
     * REGRA DE NEGOCIO: a descricao do plano NAO e digitada, e gerada a partir do
     * numero de telas e da qualidade maxima. Ex.: ("2", "Full HD") -> "Duas telas, qualidade Full HD".
     * A tela usa este metodo para mostrar a descricao ao vivo; o proprio controller
     * o usa de novo na hora de salvar (assim a descricao gravada nunca fica
     * diferente das telas e da qualidade do plano).
     * Devolve "" se o numero de telas ainda for invalido (a tela mostra o campo vazio).
     */
    public String gerarDescricao(String telas, String qualidadeMaxima) {
        int quantidade;
        try {
            quantidade = Integer.parseInt(telas.trim());
        } catch (NumberFormatException e) {
            return "";
        }
        if (quantidade < 1) {
            return "";
        }
        String[] porExtenso = {"", "Uma tela", "Duas telas", "Três telas", "Quatro telas"};
        String telasTexto = quantidade < porExtenso.length ? porExtenso[quantidade] : quantidade + " telas";
        return telasTexto + ", qualidade " + qualidadeMaxima;
    }

    /** Monta o Model a partir dos textos; reaproveitado por inserir e atualizar. */
    private Plano montar(String nome, String valorMensal, String qualidadeMaxima,
                         String telas, String permiteDownload) {
        BigDecimal valor = Conversor.paraDecimal(valorMensal, "Valor mensal");
        if (valor.signum() <= 0) { // regra: um plano precisa custar mais que zero
            throw new IllegalArgumentException("O valor mensal deve ser maior que zero.");
        }
        int quantidadeTelas = Conversor.paraInt(telas, "Telas simultâneas");
        if (quantidadeTelas < 1) { // regra: um plano precisa de pelo menos 1 tela
            throw new IllegalArgumentException("O plano precisa ter pelo menos 1 tela simultânea.");
        }
        return new Plano(
                Conversor.obrigatorio(nome, "Nome"),
                gerarDescricao(telas, qualidadeMaxima), // descricao automatica
                valor,
                qualidadeMaxima,
                quantidadeTelas,
                Conversor.paraBoolean(permiteDownload));
    }
}
