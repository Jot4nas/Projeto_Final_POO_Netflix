package model;

import java.time.LocalDate;

/**
 * Entidade Perfil (tabela "perfil").
 * Relacionamento: cada Perfil pertence a UMA Conta (FK id_conta);
 * uma Conta pode ter varios Perfis (1:N).
 */
public class Perfil {

    private int idPerfil;
    private int idConta;             // FK -> conta.id_conta
    private String nome;
    private String avatar;
    private LocalDate dataCriacao;   // DATE
    private boolean perfilInfantil;  // perfil_infantil (TINYINT(1))
    private String pin;              // CHAR(4), opcional

    // Campo so de EXIBICAO (nao existe na tabela perfil): preenchido pelo INNER JOIN
    // com a tabela conta no PerfilDAO.listar(), para a tela mostrar "1 - ana@email.com".
    private String emailConta;

    public Perfil() {
    }

    public Perfil(int idConta, String nome, String avatar, LocalDate dataCriacao,
                  boolean perfilInfantil, String pin) {
        this.idConta = idConta;
        this.nome = nome;
        this.avatar = avatar;
        this.dataCriacao = dataCriacao;
        this.perfilInfantil = perfilInfantil;
        this.pin = pin;
    }

    public int getIdPerfil() { return idPerfil; }
    public void setIdPerfil(int idPerfil) { this.idPerfil = idPerfil; }

    public int getIdConta() { return idConta; }
    public void setIdConta(int idConta) { this.idConta = idConta; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public boolean isPerfilInfantil() { return perfilInfantil; }
    public void setPerfilInfantil(boolean perfilInfantil) { this.perfilInfantil = perfilInfantil; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getEmailConta() { return emailConta; }
    public void setEmailConta(String emailConta) { this.emailConta = emailConta; }

    @Override
    public String toString() {
        return nome;
    }
}
