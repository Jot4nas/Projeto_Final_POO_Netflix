package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Plano;
import util.ConnectionFactory;

/**
 * DAO (Data Access Object) de Plano: unica classe que "fala SQL" com a tabela plano.
 * O Controller chama estes metodos passando/recebendo objetos Plano e nunca ve SQL;
 * a View nunca ve o DAO. Esse e o motivo do MVC: cada camada com uma responsabilidade.
 *
 * ===== CONCEITOS (valem para TODOS os DAOs) =====
 *
 * 1) PreparedStatement e SQL Injection
 *    No SQL escrevemos "?" no lugar dos valores e depois enviamos os valores com
 *    ps.setString(1, ...), ps.setInt(2, ...). O banco recebe o comando e os dados
 *    SEPARADOS: o dado e sempre tratado como valor, nunca como codigo SQL.
 *    Se concatenassemos ("... WHERE nome = '" + nome + "'") e o usuario digitasse
 *    x' OR '1'='1, ele alteraria o comando (SQL Injection). Com "?" isso nao acontece.
 *    Alem disso o comando e compilado uma vez pelo banco (mais eficiente).
 *
 * 2) try-with-resources
 *    try (Connection con = ...; PreparedStatement ps = ...) { ... }
 *    Tudo que e declarado entre parenteses e fechado AUTOMATICAMENTE ao sair do
 *    bloco, mesmo se der excecao. Sem isso, esqueceriamos de fechar a conexao e o
 *    MySQL acabaria sem conexoes disponiveis ("vazamento de conexao").
 *
 * 3) Mapeamento camelCase (Java) <-> snake_case (banco)
 *    Feito aqui: plano.getValorMensal() -> coluna valor_mensal, e vice-versa.
 *
 * 4) SQLException
 *    Os metodos declaram "throws SQLException" e deixam o Controller decidir como
 *    avisar o usuario. O DAO so acessa dados; nao mostra janelas.
 */
public class PlanoDAO {

    /** INSERT. O id nao entra: o banco gera (AUTO_INCREMENT). */
    public void inserir(Plano p) throws SQLException {
        String sql = "INSERT INTO plano (nome, descricao, valor_mensal, qualidade_maxima, "
                   + "telas_simultaneas, permite_download, data_criacao) VALUES (?, ?, ?, ?, ?, ?, CURDATE())";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, p.getValorMensal());
            ps.setString(4, p.getQualidadeMaxima());
            ps.setInt(5, p.getTelasSimultaneas());
            ps.setBoolean(6, p.isPermiteDownload());

            ps.executeUpdate(); // executeUpdate: INSERT/UPDATE/DELETE (nao retorna tabela)
        }
    }

    /** SELECT * : devolve todos os planos como lista de objetos Plano. */
    public List<Plano> listar() throws SQLException {
        String sql = "SELECT id_plano, nome, descricao, valor_mensal, qualidade_maxima, "
                   + "telas_simultaneas, permite_download FROM plano ORDER BY id_plano";
        List<Plano> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // executeQuery: SELECT -> ResultSet (tabela em memoria)

            // rs.next() avanca uma linha por vez; retorna false quando acabam.
            while (rs.next()) {
                lista.add(montarPlano(rs));
            }
        }
        return lista;
    }

    /** UPDATE: altera todos os campos do plano identificado pelo id (clausula WHERE). */
    public boolean atualizar(Plano p) throws SQLException {
        String sql = "UPDATE plano SET nome = ?, descricao = ?, valor_mensal = ?, qualidade_maxima = ?, "
                   + "telas_simultaneas = ?, permite_download = ? WHERE id_plano = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, p.getValorMensal());
            ps.setString(4, p.getQualidadeMaxima());
            ps.setInt(5, p.getTelasSimultaneas());
            ps.setBoolean(6, p.isPermiteDownload());
            ps.setInt(7, p.getIdPlano()); // sem o WHERE, TODOS os planos seriam alterados!

            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    /**
     * DELETE pelo id. Se o plano tiver contas ligadas, o MySQL recusa (FK) e lanca
     * SQLException; o Controller mostra a mensagem amigavel.
     */
    public boolean eliminar(int idPlano) throws SQLException {
        String sql = "DELETE FROM plano WHERE id_plano = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlano);
            return ps.executeUpdate() > 0; // false = nenhuma linha com esse id
        }
    }

    /**
     * Converte a linha ATUAL do ResultSet em um objeto Plano
     * (snake_case do banco -> camelCase do Java). Evita repetir codigo.
     */
    private Plano montarPlano(ResultSet rs) throws SQLException {
        Plano p = new Plano();
        p.setIdPlano(rs.getInt("id_plano"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setValorMensal(rs.getBigDecimal("valor_mensal"));
        p.setQualidadeMaxima(rs.getString("qualidade_maxima"));
        p.setTelasSimultaneas(rs.getInt("telas_simultaneas"));
        p.setPermiteDownload(rs.getBoolean("permite_download"));
        return p;
    }
}
