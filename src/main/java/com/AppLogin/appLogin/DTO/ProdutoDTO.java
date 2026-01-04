package com.AppLogin.appLogin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;
    private String tipo;
    private String grupo;

    private String codigoBarras;
    private String unidadeMedida;
    private String nomeImagem;

    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de custo deve ser maior ou igual a zero")
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de venda deve ser maior ou igual a zero")
    private BigDecimal precoVenda = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Estoque atual deve ser maior ou igual a zero")
    private BigDecimal estoqueAtual = BigDecimal.ZERO;

    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    private BigDecimal estoqueMaximo = BigDecimal.ZERO;

    private LocalDateTime dataCadastro;

    private Boolean ativo = true;

    public ProdutoDTO() {
        // Valores padrão para evitar NullPointerException
        this.precoCusto = BigDecimal.ZERO;
        this.precoVenda = BigDecimal.ZERO;
        this.estoqueAtual = BigDecimal.ZERO;
        this.estoqueMinimo = BigDecimal.ZERO;
        this.estoqueMaximo = BigDecimal.ZERO;
        this.ativo = true;
    }

    public ProdutoDTO(com.AppLogin.appLogin.model.Produto p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.descricao = p.getDescricao();
        this.tipo = p.getTipo();
        this.grupo = p.getGrupo();
        this.codigoBarras = p.getCodigoBarras();
        this.unidadeMedida = p.getUnidadeMedida();
        this.precoCusto = p.getPrecoCusto() != null ? p.getPrecoCusto() : BigDecimal.ZERO;
        this.precoVenda = p.getPrecoVenda() != null ? p.getPrecoVenda() : BigDecimal.ZERO;
        this.estoqueAtual = p.getEstoqueAtual() != null ? p.getEstoqueAtual() : BigDecimal.ZERO;
        this.estoqueMinimo = p.getEstoqueMinimo() != null ? p.getEstoqueMinimo() : BigDecimal.ZERO;
        this.estoqueMaximo = p.getEstoqueMaximo() != null ? p.getEstoqueMaximo() : BigDecimal.ZERO;
        this.dataCadastro = p.getDataCadastro();
        this.ativo = p.isAtivo();
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

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public String getNomeImagem() { return nomeImagem; }
    public void setNomeImagem(String nomeImagem) { this.nomeImagem = nomeImagem; }

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
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Boolean getAtivo() { return ativo != null ? ativo : true; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo != null ? ativo : true; }
}