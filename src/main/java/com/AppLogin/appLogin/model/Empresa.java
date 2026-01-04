package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idempresa")
    private Integer idempresa;

    @Column(nullable = false)
    private String nome;

    private String cpf;

    private String cnpj;

    // getters e setters
    public Integer getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(Integer idempresa) {
        this.idempresa = idempresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
