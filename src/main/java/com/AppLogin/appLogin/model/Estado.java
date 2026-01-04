package com.AppLogin.appLogin.model;
import jakarta.persistence.*;

@Entity
@Table(name = "estado")
public class Estado {
    @Id
    @Column(name = "idestado")
    private String idestado;

    private String descricao;

    public String getIdestado() {
        return idestado;
    }

    public void setIdestado(String idestado) {
        this.idestado = idestado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
