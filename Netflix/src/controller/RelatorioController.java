package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.HistoricoVisualizacaoDAO;
import dao.PerfilDAO;
import model.HistoricoVisualizacao;
import model.Perfil;
import util.Conversor;

/**
 * CONTROLLER do RELATORIO da rotina principal:
 * "Visualizacoes por periodo (e, se quiser, por perfil)".
 *
 * Ele trabalha com o CRUD principal (historico_visualizacao) usando a consulta
 * com INNER JOIN do DAO, e calcula os totais (regra de negocio fica aqui, nao na tela).
 */
public class RelatorioController {

    public static final String TODOS = "Todos";

    private final HistoricoVisualizacaoDAO dao = new HistoricoVisualizacaoDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();

    /** Opcoes do filtro de perfil: "Todos" + "id - nome" de cada perfil. */
    public List<String> opcoesPerfis() {
        try {
            List<String> opcoes = new ArrayList<>();
            opcoes.add(TODOS);
            for (Perfil p : perfilDAO.listar()) {
                opcoes.add(Conversor.opcao(p.getIdPerfil(), p.getNome()));
            }
            return opcoes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar perfis: " + e.getMessage(), e);
        }
    }

    /**
     * Valida o filtro e busca as visualizacoes do periodo.
     * Erros de validacao saem como IllegalArgumentException e erros de banco como
     * RuntimeException; a tela captura as duas e mostra a mensagem.
     */
    public List<HistoricoVisualizacao> gerar(String dataInicial, String dataFinal, String perfil) {
        LocalDate inicio = Conversor.paraData(dataInicial, "Data inicial");
        LocalDate fim = Conversor.paraData(dataFinal, "Data final");
        if (fim.isBefore(inicio)) { // regra: periodo invalido
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
        int idPerfil = TODOS.equals(perfil) ? 0 : Conversor.extrairId(perfil, "Perfil"); // 0 = todos

        try {
            return dao.listarPorPeriodo(inicio, fim, idPerfil);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar o relatório: " + e.getMessage(), e);
        }
    }

    /** Totais do relatorio, mostrados abaixo da tabela. */
    public String resumo(List<HistoricoVisualizacao> lista) {
        int concluidas = 0;
        int minutos = 0;
        for (HistoricoVisualizacao h : lista) {
            if (h.isConcluido()) {
                concluidas++;
            }
            minutos += h.getMinutoParada();
        }
        return "Total de visualizações: " + lista.size()
                + "   |   Concluídas: " + concluidas
                + "   |   Em andamento: " + (lista.size() - concluidas)
                + "   |   Minutos assistidos: " + minutos;
    }

    /** Gera o relatorio em um arquivo .txt e devolve a mensagem para a tela. */
    public String salvarTxt(String caminho, List<HistoricoVisualizacao> lista,
                            String dataInicial, String dataFinal, String perfil) {
        StringBuilder texto = new StringBuilder();
        texto.append("RELATÓRIO DE VISUALIZAÇÕES POR PERÍODO\n");
        texto.append("Período: ").append(dataInicial).append(" a ").append(dataFinal)
             .append("   Perfil: ").append(perfil).append("\n\n");
        for (HistoricoVisualizacao h : lista) {
            texto.append(Conversor.formatarDataHora(h.getDataInicio())).append(" | ")
                 .append(h.getNomePerfil()).append(" (").append(h.getEmailConta())
                 .append(", plano ").append(h.getNomePlano()).append(") | ")
                 .append(h.getNomeTitulo()).append(" | parou no minuto ")
                 .append(h.getMinutoParada()).append(" | concluído: ")
                 .append(Conversor.simNao(h.isConcluido())).append(" | ")
                 .append(h.getDispositivo()).append("\n");
        }
        texto.append("\n").append(resumo(lista)).append("\n");

        try {
            Files.write(Path.of(caminho), texto.toString().getBytes(StandardCharsets.UTF_8));
            return "Relatório salvo em: " + caminho;
        } catch (IOException e) {
            return "Não foi possível salvar o arquivo: " + e.getMessage();
        }
    }
}
