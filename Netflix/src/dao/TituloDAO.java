package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Titulo;
import util.ConnectionFactory;

/**
 * DAO de Titulo. Mesma estrutura do PlanoDAO.
 * Cuidado especial com duracao_minutos, que pode ser NULL (series).
 */
public class TituloDAO {

    public void inserir(Titulo t) throws SQLException {
        String sql = "INSERT INTO titulo (nome, sinopse, tipo, ano_lancamento, classificacao_indicativa, "
                   + "duracao_minutos, idioma_original, data_adicao_catalogo, disponivel) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencher(ps, t);
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    t.setIdTitulo(chaves.getInt(1));
                }
            }
        }
    }

    public List<Titulo> listar() throws SQLException {
        String sql = "SELECT id_titulo, nome, sinopse, tipo, ano_lancamento, classificacao_indicativa, "
                   + "duracao_minutos, idioma_original, data_adicao_catalogo, disponivel "
                   + "FROM titulo ORDER BY id_titulo";
        List<Titulo> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Titulo t = new Titulo();
                t.setIdTitulo(rs.getInt("id_titulo"));
                t.setNome(rs.getString("nome"));
                t.setSinopse(rs.getString("sinopse"));
                t.setTipo(rs.getString("tipo"));
                t.setAnoLancamento(rs.getInt("ano_lancamento"));
                t.setClassificacaoIndicativa(rs.getString("classificacao_indicativa"));

                // getInt devolve 0 quando o banco tem NULL; por isso perguntamos wasNull()
                // logo depois para saber se o valor lido era de fato NULL.
                int duracao = rs.getInt("duracao_minutos");
                t.setDuracaoMinutos(rs.wasNull() ? null : duracao);

                t.setIdiomaOriginal(rs.getString("idioma_original"));
                t.setDataAdicaoCatalogo(rs.getDate("data_adicao_catalogo").toLocalDate());
                t.setDisponivel(rs.getBoolean("disponivel"));
                lista.add(t);
            }
        }
        return lista;
    }

    public boolean atualizar(Titulo t) throws SQLException {
        String sql = "UPDATE titulo SET nome = ?, sinopse = ?, tipo = ?, ano_lancamento = ?, "
                   + "classificacao_indicativa = ?, duracao_minutos = ?, idioma_original = ?, "
                   + "data_adicao_catalogo = ?, disponivel = ? WHERE id_titulo = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            preencher(ps, t);
            ps.setInt(10, t.getIdTitulo());
            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    /** Ao eliminar o titulo, o historico dele e apagado junto (ON DELETE CASCADE). */
    public boolean eliminar(int idTitulo) throws SQLException {
        String sql = "DELETE FROM titulo WHERE id_titulo = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTitulo);
            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    /** Preenche os 9 primeiros "?" (iguais no INSERT e no UPDATE) para nao repetir codigo. */
    private void preencher(PreparedStatement ps, Titulo t) throws SQLException {
        ps.setString(1, t.getNome());
        ps.setString(2, t.getSinopse());
        ps.setString(3, t.getTipo());
        ps.setInt(4, t.getAnoLancamento());
        ps.setString(5, t.getClassificacaoIndicativa());

        // Integer null nao pode ir em setInt (daria NullPointerException):
        // usamos setNull, informando o tipo SQL da coluna.
        if (t.getDuracaoMinutos() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, t.getDuracaoMinutos());
        }

        ps.setString(7, t.getIdiomaOriginal());
        ps.setDate(8, Date.valueOf(t.getDataAdicaoCatalogo()));
        ps.setBoolean(9, t.isDisponivel());
    }
}
