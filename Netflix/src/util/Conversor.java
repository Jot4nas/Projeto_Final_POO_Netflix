package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.format.DateTimeParseException;

/**
 * Utilitario de conversao: a View entrega TUDO como texto (String) e o Controller
 * usa estes metodos para transformar em int, BigDecimal, LocalDate etc.
 * Se o texto for invalido, lancamos IllegalArgumentException com uma mensagem
 * amigavel; o Controller captura e devolve essa mensagem para a View exibir.
 */
public class Conversor {

    // Formato brasileiro. STRICT recusa datas que nao existem (ex.: 31/02/2026).
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    // Data e hora que o sistema MOSTRA (tabelas e relatorio). Para trocar a ordem, mude so esta linha.
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Resposta quando UPDATE/DELETE nao encontra o id (ex.: o registro foi eliminado em outra janela). */
    public static final String REGISTRO_NAO_ENCONTRADO =
            "Registro não encontrado (ele pode ter sido eliminado em outra janela). A tabela foi atualizada.";

    private Conversor() {
    }

    /** Campo de texto obrigatorio: nao pode estar vazio. */
    public static String obrigatorio(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório.");
        }
        return texto.trim();
    }

    /** Campo opcional: texto vazio vira null (que o DAO grava como NULL no banco). */
    public static String vazioParaNulo(String texto) {
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }

    public static int paraInt(String texto, String campo) {
        try {
            return Integer.parseInt(obrigatorio(texto, campo));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + campo + "' deve ser um número inteiro.");
        }
    }

    /** Inteiro opcional: vazio vira null. */
    public static Integer paraIntOuNulo(String texto, String campo) {
        return vazioParaNulo(texto) == null ? null : paraInt(texto, campo);
    }

    public static BigDecimal paraDecimal(String texto, String campo) {
        try {
            return new BigDecimal(obrigatorio(texto, campo).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + campo + "' deve ser um número (ex.: 39.90).");
        }
    }

    public static LocalDate paraData(String texto, String campo) {
        try {
            return LocalDate.parse(obrigatorio(texto, campo), DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("O campo '" + campo + "' deve ser uma data válida no formato dd/mm/aaaa (ex.: 31/12/2026).");
        }
    }

    /** Combo "Sim"/"Nao" -> boolean. */
    public static boolean paraBoolean(String texto) {
        return "Sim".equals(texto);
    }

    public static String simNao(boolean valor) {
        return valor ? "Sim" : "Nao";
    }

    /**
     * Texto "id - nome" (ex.: "3 - Premium") usado nos combos de chave estrangeira
     * E nas colunas das tabelas. Como os dois usam o mesmo formato, ao clicar numa
     * linha o combo encontra exatamente o mesmo texto e se posiciona sozinho.
     */
    public static String opcao(int id, String nome) {
        return id + " - " + nome;
    }

    /**
     * Os combos de chave estrangeira mostram "3 - Premium" (id - nome).
     * Aqui pegamos so o numero antes do " - ".
     */
    public static int extrairId(String opcao, String campo) {
        if (opcao == null || opcao.trim().isEmpty()) {
            throw new IllegalArgumentException("Selecione um valor em '" + campo + "'.");
        }
        return paraInt(opcao.split(" - ")[0], campo);
    }

    /** Id da linha selecionada na tabela; vazio significa que o usuario nao selecionou nada. */
    public static int paraId(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Selecione um registro na tabela primeiro.");
        }
        return paraInt(texto, "ID");
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora.format(DATA_HORA);
    }

    public static String hoje() {
        return LocalDate.now().format(DATA);
    }

}
