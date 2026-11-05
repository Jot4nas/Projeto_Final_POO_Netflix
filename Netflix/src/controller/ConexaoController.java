package controller;

import java.io.IOException;
import java.sql.SQLException;

import util.ConnectionFactory;
import util.Conversor;
import util.CriadorBanco;

/**
 * CONTROLLER da conexao com o MySQL (usado pela TelaConexao e pelo menu).
 * Junta tres passos que antes o grupo fazia na mao:
 *  1) informar usuario/senha do MySQL (antes: editar o codigo e recompilar);
 *  2) criar o banco netflix com os dados de exemplo (antes: rodar o script no Workbench);
 *  3) guardar a configuracao para a proxima vez (config.properties).
 */
public class ConexaoController {

    // So le o config.properties na PRIMEIRA vez. O menu tambem cria um ConexaoController (para o botao
    // "Restaurar dados de exemplo"); sem esta trava, ele releria o arquivo e poderia trocar a senha
    // recem-digitada por uma antiga, caso o arquivo nao tenha podido ser atualizado.
    private static boolean configuracaoCarregada = false;

    // Aviso (nao e erro) do ultimo conectar(): ex.: a configuracao nao pode ser salva.
    private String aviso;

    /** Ao criar o controller pela primeira vez, le o config.properties (se existir) para preencher a tela. */
    public ConexaoController() {
        if (!configuracaoCarregada) {
            ConnectionFactory.carregarConfiguracao();
            configuracaoCarregada = true;
        }
    }

    /** Aviso do ultimo conectar() que deu certo (ou null se nao houve nenhum). */
    public String getAviso() { return aviso; }

    public String servidorSalvo() { return ConnectionFactory.getServidor(); }
    public String portaSalva() { return String.valueOf(ConnectionFactory.getPorta()); }
    public String usuarioSalvo() { return ConnectionFactory.getUsuario(); }
    public String senhaSalva() { return ConnectionFactory.getSenha(); }

    /** Tenta conectar. Devolve null se deu certo, ou a mensagem de erro para a tela mostrar. */
    public String conectar(String servidor, String porta, String usuario, String senha) {
        try {
            ConnectionFactory.configurar(
                    Conversor.obrigatorio(servidor, "Servidor"),
                    Conversor.paraInt(porta, "Porta"),
                    Conversor.obrigatorio(usuario, "Usuário"),
                    senha);
            CriadorBanco.criarSeNaoExistir(); // so cria na primeira vez
            aviso = null;
            try {
                ConnectionFactory.salvarConfiguracao();
            } catch (IOException e) {
                // Pasta protegida ou somente leitura: nao e motivo para barrar o usuario. O sistema
                // abre normalmente e so avisa que vai pedir a senha de novo na proxima vez.
                aviso = "Conectado, mas não foi possível salvar a configuração (" + e.getMessage()
                        + ").\nNa próxima vez será preciso digitar a senha de novo.";
            }
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return "Não foi possível conectar ao MySQL. Confira se ele está ligado e se o usuário/senha "
                    + "estão certos.\n\nDetalhe: " + e.getMessage();
        } catch (IOException e) {
            return "Erro ao ler o script do banco ou salvar a configuração: " + e.getMessage();
        }
    }

    /** Botao "Restaurar dados de exemplo": apaga o banco e cria de novo com os dados do script. */
    public String restaurarDadosExemplo() {
        try {
            CriadorBanco.recriarBanco();
            return "Banco recriado com os dados de exemplo!";
        } catch (SQLException | IOException e) {
            return "Erro ao recriar o banco: " + e.getMessage();
        }
    }
}
