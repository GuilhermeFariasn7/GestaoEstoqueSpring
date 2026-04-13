package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unidade_medida")
public class UnidadeMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String idUnidadeMedida;

    @Column(nullable = false, name = "nome")
    private String nome;

    public String getIdUnidadeMedida() {
        return idUnidadeMedida;
    }

    public void setIdUnidadeMedida(String idUnidadeMedida) {
        this.idUnidadeMedida = idUnidadeMedida;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
