package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grupo")
public class GrupoProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idGrupoProduto;

    @Column(nullable = false, name = "descricao")
    private String descricao;

    public Integer getIdGrupoProduto() {
        return idGrupoProduto;
    }

    public void setIdGrupoProduto(Integer idGrupoProduto) {
        this.idGrupoProduto = idGrupoProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
