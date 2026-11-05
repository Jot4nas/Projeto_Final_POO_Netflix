package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ContaDAO;
import dao.PlanoDAO;
import model.Conta;
import model.Plano;
import util.Conversor;

/** CONTROLLER de Conta (mesmo padrao do PlanoController). */
public class ContaController {

    private final ContaDAO dao = new ContaDAO();
    private final PlanoDAO planoDAO = new PlanoDAO(); // so para alimentar o combo de planos

    public List<Conta> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas: " + e.getMessage(), e);
        }
    }

    /** Opcoes do combo de planos no formato "id - nome" (a View so exibe). */
    public List<String> opcoesPlanos() {
        try {
            List<String> opcoes = new ArrayList<>();
            for (Plano p : planoDAO.listar()) {
                opcoes.add(Conversor.opcao(p.getIdPlano(), p.getNome()));
            }
            return opcoes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar planos: " + e.getMessage(), e);
        }
    }

    public String inserir(String plano, String email, String senha, String telefone,
                          String pais, String status) {
        try {
            dao.inserir(montar(plano, email, senha, telefone, pais, status));
            return "Conta inserida com sucesso!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return mensagemDeErro(e);
        }
    }

    public String atualizar(String id, String plano, String email, String senha, String telefone,
                            String pais, String status) {
        try {
            int idConta = Conversor.paraId(id);
            Conta c = montar(plano, email, senha, telefone, pais, status);
            c.setIdConta(idConta);
            if (!dao.atualizar(c)) {
                return Conversor.REGISTRO_NAO_ENCONTRADO;
            }
            return "Conta atualizada com sucesso!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return mensagemDeErro(e);
        }
    }

    public String eliminar(String id) {
        try {
            if (!dao.eliminar(Conversor.paraId(id))) {
                return Conversor.REGISTRO_NAO_ENCONTRADO;
            }
            return "Conta eliminada com sucesso!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (SQLException e) {
            // 1451 = o MySQL recusou porque ha perfis nesta conta (chave estrangeira).
            if (e.getErrorCode() == 1451) {
                return "Não é possível eliminar esta conta: ela ainda tem perfis. Elimine os perfis antes.";
            }
            return "Erro no banco de dados: " + e.getMessage();
        }
    }

    /**
     * Traduz o erro do MySQL para uma mensagem que explica o problema de verdade.
     * 1062 = valor repetido em coluna UNIQUE (o e-mail); 1452 = chave estrangeira sem pai (o plano).
     */
    private String mensagemDeErro(SQLException e) {
        if (e.getErrorCode() == 1062) {
            return "Já existe uma conta com esse e-mail.";
        }
        if (e.getErrorCode() == 1452) {
            return "O plano escolhido não existe mais. Reabra a tela e escolha de novo.";
        }
        return "Erro no banco de dados: " + e.getMessage();
    }

    private Conta montar(String plano, String email, String senha, String telefone,
                         String pais, String status) {
        String paisValido = Conversor.obrigatorio(pais, "País");
        if (paisValido.length() != 2) { // regra de negocio: codigo do pais tem 2 letras (CHAR(2))
            throw new IllegalArgumentException("O país deve ter 2 letras (ex.: BR).");
        }
        return new Conta(
                Conversor.extrairId(plano, "Plano"),
                Conversor.obrigatorio(email, "E-mail"),
                Conversor.obrigatorio(senha, "Senha"), // em um sistema real aqui geraria o hash da senha
                Conversor.vazioParaNulo(telefone),
                paisValido.toUpperCase(),
                status);
    }
}
