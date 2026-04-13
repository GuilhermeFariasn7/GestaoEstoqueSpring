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

    // IDs dos relacionamentos
    private Integer tipoId;
    private Integer grupoId;
    private String unidadeMedidaId;

    private String codigoBarras;
    private String fotoUrl;
    private String tipoDescricao;
    private String grupoDescricao;
    private String unidadeMedidaNome;
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

    public ProdutoDTO() {}

    public ProdutoDTO(com.AppLogin.appLogin.model.Produto p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.descricao = p.getDescricao();
        this.codigoBarras = p.getCodigoBarras();
        this.precoCusto = p.getPrecoCusto();
        this.precoVenda = p.getPrecoVenda();
        this.estoqueAtual = p.getEstoqueAtual();
        this.estoqueMinimo = p.getEstoqueMinimo();
        this.estoqueMaximo = p.getEstoqueMaximo();
        this.dataCadastro = p.getDataCadastro();
        this.ativo = p.getAtivo();
        this.fotoUrl = p.getFotoUrl();

        if (p.getTipo() != null) this.tipoId = p.getTipo().getIdTipoProduto();
        if (p.getGrupo() != null) this.grupoId = p.getGrupo().getIdGrupoProduto();
        if (p.getUnidadeMedida() != null) this.unidadeMedidaId = p.getUnidadeMedida().getIdUnidadeMedida();
        if (p.getTipo() != null) this.tipoDescricao = p.getTipo().getDescricao();
        if (p.getGrupo() != null) this.grupoDescricao = p.getGrupo().getDescricao();
        if (p.getUnidadeMedida() != null) this.unidadeMedidaNome = p.getUnidadeMedida().getNome();
    }

    public String getTipoDescricao() {
        return tipoDescricao;
    }

    public void setTipoDescricao(String tipoDescricao) {
        this.tipoDescricao = tipoDescricao;
    }

    public String getGrupoDescricao() {
        return grupoDescricao;
    }

    public void setGrupoDescricao(String grupoDescricao) {
        this.grupoDescricao = grupoDescricao;
    }

    public String getUnidadeMedidaNome() {
        return unidadeMedidaNome;
    }

    public void setUnidadeMedidaNome(String unidadeMedidaNome) {
        this.unidadeMedidaNome = unidadeMedidaNome;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getTipoId() { return tipoId; }
    public void setTipoId(Integer tipoId) { this.tipoId = tipoId; }

    public Integer getGrupoId() { return grupoId; }
    public void setGrupoId(Integer grupoId) { this.grupoId = grupoId; }

    public String getUnidadeMedidaId() { return unidadeMedidaId; }
    public void setUnidadeMedidaId(String unidadeMedidaId) { this.unidadeMedidaId = unidadeMedidaId; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public BigDecimal getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public BigDecimal getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(BigDecimal estoqueAtual) { this.estoqueAtual = estoqueAtual; }

    public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public BigDecimal getEstoqueMaximo() { return estoqueMaximo; }
    public void setEstoqueMaximo(BigDecimal estoqueMaximo) { this.estoqueMaximo = estoqueMaximo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}