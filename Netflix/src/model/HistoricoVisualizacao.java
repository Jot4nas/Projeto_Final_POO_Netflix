package model;

import java.time.LocalDateTime;

/**
 * Entidade HistoricoVisualizacao (tabela "historico_visualizacao").
 * E a tabela ASSOCIATIVA que resolve a relacao N:N entre Perfil e Titulo:
 * um perfil assiste varios titulos e um titulo e assistido por varios perfis.
 * Por isso ela guarda duas FKs (idPerfil e idTitulo) e dados proprios da
 * "assistida" (quando, ate que minuto, em que aparelho).
 */
public class HistoricoVisualizacao {

    private int idVisualizacao;
    private int idPerfil;                         // FK -> perfil.id_perfil
    private int idTitulo;                         // FK -> titulo.id_titulo
    private LocalDateTime dataInicio;             // DATETIME
    private LocalDateTime dataUltimaVisualizacao; // DATETIME
    private int minutoParada;
    private boolean concluido;                    // TINYINT(1)
    private String dispositivo;                   // ENUM('TV','Celular','Notebook','Tablet','Console')

    // ---- Campos so de EXIBICAO (NAO existem na tabela) ----
    // Sao preenchidos apenas pelo INNER JOIN, para mostrar NOMES em vez de IDs:
    // perfil, titulo e tambem a conta (e-mail) e o plano a que o perfil pertence.
    // No INSERT/UPDATE eles sao ignorados.
    private String nomePerfil;
    private String nomeTitulo;
    private String emailConta;
    private String nomePlano;

    public HistoricoVisualizacao() {
    }

    public HistoricoVisualizacao(int idPerfil, int idTitulo, int minutoParada,
                                 boolean concluido, String dispositivo) {
        this.idPerfil = idPerfil;
        this.idTitulo = idTitulo;
        this.minutoParada = minutoParada;
        this.concluido = concluido;
        this.dispositivo = dispositivo;
    }

    public int getIdVisualizacao() { return idVisualizacao; }
    public void setIdVisualizacao(int idVisualizacao) { this.idVisualizacao = idVisualizacao; }

    public int getIdPerfil() { return idPerfil; }
    public void setIdPerfil(int idPerfil) { this.idPerfil = idPerfil; }

    public int getIdTitulo() { return idTitulo; }
    public void setIdTitulo(int idTitulo) { this.idTitulo = idTitulo; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataUltimaVisualizacao() { return dataUltimaVisualizacao; }
    public void setDataUltimaVisualizacao(LocalDateTime dataUltimaVisualizacao) { this.dataUltimaVisualizacao = dataUltimaVisualizacao; }

    public int getMinutoParada() { return minutoParada; }
    public void setMinutoParada(int minutoParada) { this.minutoParada = minutoParada; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

    public String getDispositivo() { return dispositivo; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }

    public String getNomePerfil() { return nomePerfil; }
    public void setNomePerfil(String nomePerfil) { this.nomePerfil = nomePerfil; }

    public String getNomeTitulo() { return nomeTitulo; }
    public void setNomeTitulo(String nomeTitulo) { this.nomeTitulo = nomeTitulo; }

    public String getEmailConta() { return emailConta; }
    public void setEmailConta(String emailConta) { this.emailConta = emailConta; }

    public String getNomePlano() { return nomePlano; }
    public void setNomePlano(String nomePlano) { this.nomePlano = nomePlano; }
}
