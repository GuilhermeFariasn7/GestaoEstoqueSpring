package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filial")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfilial")
    private Integer idfilial;

    @Column(nullable = false)
    private String nome;

    private Integer fone;

    private String email;

    private String tipo;

    // RELAÇÃO COM EMPRESA
    @ManyToOne
    @JoinColumn(name = "empresa_idempresa")
    private Empresa empresa;

    // getters e setters
    public Integer getIdfilial() {
        return idfilial;
    }

    public void setIdfilial(Integer idfilial) {
        this.idfilial = idfilial;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getFone() {
        return fone;
    }

    public void setFone(Integer fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
