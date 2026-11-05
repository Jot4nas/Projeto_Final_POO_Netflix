package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cria o banco "netflix" sozinho, rodando o script sql/netflix.sql que vai
 * DENTRO do Netflix.jar. Assim ninguem do grupo precisa abrir o MySQL Workbench:
 * basta o MySQL Server estar ligado.
 *
 * O script e o mesmo que pode ser aberto no Workbench (pasta Netflix/sql).
 */
public class CriadorBanco {

    private CriadorBanco() {
    }

    /**
     * Cria o banco na primeira execucao: quando ele nao existe OU existe mas esta vazio (sem nenhuma
     * tabela). Um banco "netflix" que ja tem tabelas e reaproveitado e nunca e apagado aqui, para nao
     * perder dados; so o botao "Restaurar dados de exemplo" apaga.
     */
    public static void criarSeNaoExistir() throws SQLException, IOException {
        if (!bancoExiste() || bancoSemTabelas()) {
            recriarBanco();
        }
    }

    /** Pergunta ao MySQL se o banco "netflix" nao tem nenhuma tabela (foi criado vazio, por exemplo). */
    public static boolean bancoSemTabelas() throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'netflix'";
        try (Connection con = ConnectionFactory.getConnectionServidor();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    /** Pergunta ao MySQL se ja existe um banco chamado "netflix". */
    public static boolean bancoExiste() throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = 'netflix'";
        try (Connection con = ConnectionFactory.getConnectionServidor();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /**
     * Apaga e cria de novo o banco com os dados de exemplo (o proprio script
     * comeca com DROP DATABASE IF EXISTS). Usado tambem pelo botao
     * "Restaurar dados de exemplo" do menu.
     */
    public static void recriarBanco() throws SQLException, IOException {
        String script = lerScript();

        // O JDBC executa UM comando por vez. Por isso cortamos o script em cada ";"
        // e executamos os comandos em sequencia. Usamos Statement (e nao
        // PreparedStatement) porque o script nao tem valores vindos do usuario.
        try (Connection con = ConnectionFactory.getConnectionServidor();
             Statement st = con.createStatement()) {
            for (String comando : script.split(";")) {
                if (!comando.trim().isEmpty()) {
                    st.execute(comando);
                }
            }
        }
    }

    /** Le o netflix.sql de dentro do jar e tira os comentarios ("--" ate o fim da linha). */
    private static String lerScript() throws IOException {
        try (InputStream entrada = CriadorBanco.class.getResourceAsStream("/netflix.sql")) {
            if (entrada == null) {
                throw new IOException("Arquivo netflix.sql nao encontrado dentro do sistema.");
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            entrada.transferTo(bytes);
            String texto = bytes.toString(StandardCharsets.UTF_8.name());

            StringBuilder semComentarios = new StringBuilder();
            for (String linha : texto.split("\n")) {
                int comentario = linha.indexOf("--");
                semComentarios.append(comentario >= 0 ? linha.substring(0, comentario) : linha).append('\n');
            }
            return semComentarios.toString();
        }
    }
}
