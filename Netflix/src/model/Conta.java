package model;


/**
 * Entidade Conta (tabela "conta").
 * Relacionamento: cada Conta pertence a UM Plano (FK id_plano).
 * Guardamos apenas o id do plano (idPlano), igual ao banco;
 * o nome do plano pode ser buscado depois, se a tela precisar.
 */
public class Conta {

    private int idConta;
    private int idPlano;             // FK -> plano.id_plano
    private String email;
    private String senhaHash;        // senha_hash
    private String telefone;         // pode ser NULL no banco
    private String pais;             // CHAR(2), ex.: "BR"
    private String status;           // ENUM('Ativa','Suspensa','Cancelada')

    // Campo so de EXIBICAO (nao existe na tabela conta): preenchido pelo INNER JOIN
    // com a tabela plano no ContaDAO.listar(), para a tela mostrar "3 - Premium".
    private String nomePlano;

    public Conta() {
    }

    public Conta(int idPlano, String email, String senhaHash, String telefone,
                 String pais, String status) {
        this.idPlano = idPlano;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.pais = pais;
        this.status = status;
    }

    public int getIdConta() { return idConta; }
    public void setIdConta(int idConta) { this.idConta = idConta; }

    public int getIdPlano() { return idPlano; }
    public void setIdPlano(int idPlano) { this.idPlano = idPlano; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNomePlano() { return nomePlano; }
    public void setNomePlano(String nomePlano) { this.nomePlano = nomePlano; }

    @Override
    public String toString() {
        return email;
    }
}
