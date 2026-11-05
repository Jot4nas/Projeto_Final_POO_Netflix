package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.HistoricoVisualizacao;
import util.ConnectionFactory;

/**
 * DAO do CRUD principal (tabela associativa entre Perfil e Titulo).
 * Conceitos de PreparedStatement e try-with-resources: veja o PlanoDAO.
 */
public class HistoricoVisualizacaoDAO {

    /**
     * Parte comum das duas consultas com INNER JOIN (listarComNomes e listarPorPeriodo).
     *
     * Problema: o historico guarda so NUMEROS (id_perfil, id_titulo), e o usuario
     * quer ler NOMES. Solucao: juntar as tres tabelas.
     *
     *   FROM historico_visualizacao h
     *   INNER JOIN perfil p ON p.id_perfil = h.id_perfil   -> liga cada linha do historico ao seu perfil
     *   INNER JOIN titulo t ON t.id_titulo = h.id_titulo   -> e ao seu titulo
     *
     * - "ON" diz QUAL coluna casa com QUAL (PK da tabela pai = FK do historico).
     * - INNER JOIN so devolve linhas que tem correspondencia nas duas tabelas.
     *   (Como as FKs garantem que perfil e titulo existem, nenhuma linha se perde.)
     * - "h", "p", "t" sao apelidos (aliases) para encurtar o SQL.
     * - "AS nome_perfil" / "AS nome_titulo": as duas tabelas tem uma coluna "nome";
     *   sem apelido nao daria para distinguir as duas no ResultSet.
     */
    private static final String SELECT_COM_NOMES =
            "SELECT h.id_visualizacao, h.id_perfil, h.id_titulo, h.data_inicio, "
          + "h.data_ultima_visualizacao, h.minuto_parada, h.concluido, h.dispositivo, "
          + "p.nome AS nome_perfil, t.nome AS nome_titulo, "
          + "c.email AS email_conta, pl.nome AS nome_plano "
          + "FROM historico_visualizacao h "
          + "INNER JOIN perfil p ON p.id_perfil = h.id_perfil "
          + "INNER JOIN titulo t ON t.id_titulo = h.id_titulo "
          + "INNER JOIN conta c ON c.id_conta = p.id_conta "
          + "INNER JOIN plano pl ON pl.id_plano = c.id_plano ";

    /** INSERT: grava as duas FKs (idPerfil, idTitulo) e os dados da sessao. */
    public void inserir(HistoricoVisualizacao h) throws SQLException {
        String sql = "INSERT INTO historico_visualizacao (id_perfil, id_titulo, data_inicio, "
                   + "data_ultima_visualizacao, minuto_parada, concluido, dispositivo) "
                   + "VALUES (?, ?, NOW(), NOW(), ?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, h.getIdPerfil());
            ps.setInt(2, h.getIdTitulo());
            ps.setInt(3, h.getMinutoParada());
            ps.setBoolean(4, h.isConcluido());
            ps.setString(5, h.getDispositivo());
            // nomePerfil e nomeTitulo NAO sao gravados: nao existem nesta tabela.
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    h.setIdVisualizacao(chaves.getInt(1));
                }
            }
        }
    }

    /** READ do CRUD principal: todas as visualizacoes, com NOMES (INNER JOIN), mais recentes primeiro. */
    public List<HistoricoVisualizacao> listarComNomes() throws SQLException {
        String sql = SELECT_COM_NOMES + "ORDER BY h.data_ultima_visualizacao DESC";
        List<HistoricoVisualizacao> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    /**
     * CONSULTA DO RELATORIO: visualizacoes iniciadas entre duas datas, com nomes (INNER JOIN).
     * Se idPerfil for 0 traz todos os perfis; senao, so o perfil escolhido.
     *
     * DATE(h.data_inicio) tira a hora do DATETIME, para comparar so a data.
     * BETWEEN ? AND ? inclui as duas pontas (data inicial e data final).
     */
    public List<HistoricoVisualizacao> listarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal, int idPerfil)
            throws SQLException {
        String sql = SELECT_COM_NOMES + "WHERE DATE(h.data_inicio) BETWEEN ? AND ? ";
        if (idPerfil > 0) {
            sql += "AND h.id_perfil = ? "; // filtro opcional: so acrescentamos se um perfil foi escolhido
        }
        sql += "ORDER BY h.data_inicio";
        List<HistoricoVisualizacao> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dataInicial));
            ps.setDate(2, Date.valueOf(dataFinal));
            if (idPerfil > 0) {
                ps.setInt(3, idPerfil);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    /** UPDATE: altera o registro identificado pelo id (WHERE). Sem o WHERE, todas as linhas seriam alteradas. */
    public boolean atualizar(HistoricoVisualizacao h) throws SQLException {
        String sql = "UPDATE historico_visualizacao SET id_perfil = ?, id_titulo = ?, "
                   + "data_ultima_visualizacao = NOW(), minuto_parada = ?, concluido = ?, dispositivo = ? "
                   + "WHERE id_visualizacao = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getIdPerfil());
            ps.setInt(2, h.getIdTitulo());
            ps.setInt(3, h.getMinutoParada());
            ps.setBoolean(4, h.isConcluido());
            ps.setString(5, h.getDispositivo());
            ps.setInt(6, h.getIdVisualizacao()); // o "?" do WHERE
            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    public boolean eliminar(int idVisualizacao) throws SQLException {
        String sql = "DELETE FROM historico_visualizacao WHERE id_visualizacao = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVisualizacao);
            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    /**
     * Converte a linha ATUAL do ResultSet em um objeto. As colunas da propria tabela
     * vao para os atributos normais; as que vieram do JOIN (nome_perfil, nome_titulo, email_conta, nome_plano)
     * vao para os campos so de exibicao.
     */
    private HistoricoVisualizacao montar(ResultSet rs) throws SQLException {
        HistoricoVisualizacao h = new HistoricoVisualizacao();
        h.setIdVisualizacao(rs.getInt("id_visualizacao"));
        h.setIdPerfil(rs.getInt("id_perfil"));
        h.setIdTitulo(rs.getInt("id_titulo"));
        h.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        h.setDataUltimaVisualizacao(rs.getTimestamp("data_ultima_visualizacao").toLocalDateTime());
        h.setMinutoParada(rs.getInt("minuto_parada"));
        h.setConcluido(rs.getBoolean("concluido"));
        h.setDispositivo(rs.getString("dispositivo"));

        // Colunas vindas do INNER JOIN (5 tabelas), lidas pelo apelido (alias) do SELECT:
        h.setNomePerfil(rs.getString("nome_perfil"));
        h.setNomeTitulo(rs.getString("nome_titulo"));
        h.setEmailConta(rs.getString("email_conta"));
        h.setNomePlano(rs.getString("nome_plano"));
        return h;
    }
}
