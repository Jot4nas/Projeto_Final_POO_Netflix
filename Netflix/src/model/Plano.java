package model;

import java.math.BigDecimal;

/**
 * Entidade Plano (tabela "plano").
 * Model = classe "burra": so guarda dados (atributos + getters/setters).
 * Nao acessa banco e nao tem regra de negocio.
 * Mapeamento: camelCase no Java <-> snake_case no banco (feito no DAO).
 *   idPlano -> id_plano | valorMensal -> valor_mensal | ...
 */
public class Plano {

    private int idPlano;
    private String nome;
    private String descricao;
    private BigDecimal valorMensal;   // DECIMAL: BigDecimal evita erro de arredondamento de double com dinheiro
    private String qualidadeMaxima;   // ENUM('SD','HD','Full HD','4K UHD')
    private int telasSimultaneas;
    private boolean permiteDownload;  // TINYINT(1) = boolean

    // Construtor vazio: usado pelo DAO ao montar o objeto a partir do ResultSet.
    public Plano() {
    }

    // Construtor completo (sem id: o id e gerado pelo AUTO_INCREMENT do banco).
    public Plano(String nome, String descricao, BigDecimal valorMensal, String qualidadeMaxima,
                 int telasSimultaneas, boolean permiteDownload) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorMensal = valorMensal;
        this.qualidadeMaxima = qualidadeMaxima;
        this.telasSimultaneas = telasSimultaneas;
        this.permiteDownload = permiteDownload;
    }

    public int getIdPlano() { return idPlano; }
    public void setIdPlano(int idPlano) { this.idPlano = idPlano; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }

    public String getQualidadeMaxima() { return qualidadeMaxima; }
    public void setQualidadeMaxima(String qualidadeMaxima) { this.qualidadeMaxima = qualidadeMaxima; }

    public int getTelasSimultaneas() { return telasSimultaneas; }
    public void setTelasSimultaneas(int telasSimultaneas) { this.telasSimultaneas = telasSimultaneas; }

    public boolean isPermiteDownload() { return permiteDownload; }
    public void setPermiteDownload(boolean permiteDownload) { this.permiteDownload = permiteDownload; }


    // toString: sobrescreve Object.toString() para o objeto aparecer pelo nome (ex.: ao imprimir).
    @Override
    public String toString() {
        return nome;
    }
}
