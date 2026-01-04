package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cidade")
public class Cidade {

    // RELAÇÃO COM ESTADO
    @ManyToOne
    @JoinColumn(name = "estado_idestado")
    private Estado estado;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcidade")
    private Integer idcidade;

    private String nome;

    private String complemento;

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getIdcidade() {
        return idcidade;
    }

    public void setIdcidade(Integer idcidade) {
        this.idcidade = idcidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
