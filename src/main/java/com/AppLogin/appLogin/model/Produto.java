package com.AppLogin.appLogin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(length = 50)
    private String tipo;

    @Column(length = 50)
    private String grupo;

    @Column(name = "nome_imagem")
    private String nomeImagem;


    @Column(name = "codigo_barras", unique = true, length = 100)
    private String codigoBarras;

    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de custo deve ser maior ou igual a zero")
    @Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de venda deve ser maior ou igual a zero")
    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda = BigDecimal.ZERO;

    @NotNull(message = "Estoque atual é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Estoque atual deve ser maior ou igual a zero")
    @Column(name = "estoque_atual", nullable = false, precision = 10, scale = 3)
    private BigDecimal estoqueAtual = BigDecimal.ZERO;

    @Column(name = "estoque_minimo", precision = 10, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @Column(name = "estoque_maximo", precision = 10, scale = 3)
    private BigDecimal estoqueMaximo = BigDecimal.ZERO;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private boolean ativo = true;


    public Produto() {
        // Valores padrão
        this.precoCusto = BigDecimal.ZERO;
        this.precoVenda = BigDecimal.ZERO;
        this.estoqueAtual = BigDecimal.ZERO;
        this.estoqueMinimo = BigDecimal.ZERO;
        this.estoqueMaximo = BigDecimal.ZERO;
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    public Produto(String nome, BigDecimal precoCusto, BigDecimal precoVenda) {
        this();
        this.nome = nome;
        this.precoCusto = precoCusto != null ? precoCusto : BigDecimal.ZERO;
        this.precoVenda = precoVenda != null ? precoVenda : BigDecimal.ZERO;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getNomeImagem() { return nomeImagem; }
    public void setNomeImagem(String nomeImagem) { this.nomeImagem = nomeImagem; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public BigDecimal getPrecoCusto() {
        return precoCusto != null ? precoCusto : BigDecimal.ZERO;
    }
    public void setPrecoCusto(BigDecimal precoCusto) {
        this.precoCusto = precoCusto != null ? precoCusto : BigDecimal.ZERO;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda != null ? precoVenda : BigDecimal.ZERO;
    }
    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda != null ? precoVenda : BigDecimal.ZERO;
    }

    public BigDecimal getEstoqueAtual() {
        return estoqueAtual != null ? estoqueAtual : BigDecimal.ZERO;
    }
    public void setEstoqueAtual(BigDecimal estoqueAtual) {
        this.estoqueAtual = estoqueAtual != null ? estoqueAtual : BigDecimal.ZERO;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo != null ? estoqueMinimo : BigDecimal.ZERO;
    }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo != null ? estoqueMinimo : BigDecimal.ZERO;
    }

    public BigDecimal getEstoqueMaximo() {
        return estoqueMaximo != null ? estoqueMaximo : BigDecimal.ZERO;
    }
    public void setEstoqueMaximo(BigDecimal estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo != null ? estoqueMaximo : BigDecimal.ZERO;
    }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro != null ? dataCadastro : LocalDateTime.now();
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }


    public boolean temEstoqueBaixo() {
        return estoqueMinimo != null && estoqueAtual.compareTo(estoqueMinimo) <= 0;
    }

    public boolean temEstoqueExcedente() {
        return estoqueMaximo != null && estoqueAtual.compareTo(estoqueMaximo) > 0;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", precoVenda=" + precoVenda +
                ", ativo=" + ativo +
                '}';
    }
}