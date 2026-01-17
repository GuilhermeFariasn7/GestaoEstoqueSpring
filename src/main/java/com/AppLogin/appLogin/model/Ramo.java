package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ramo")
public class Ramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idramo;

    /* ===== DADOS BÁSICOS ===== */

    @Column(nullable = false)
    private String nome;

    private String cpf;

    public Long getIdramo() {
        return idramo;
    }

    public void setIdramo(Long idramo) {
        this.idramo = idramo;
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
}
