package com.AppLogin.appLogin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Long id;

    @NotEmpty
    private String nome;

    @NotEmpty
    private String email;

    @NotEmpty
    private String senha;

    @NotEmpty
    private String login;

    private String cep;

    private Boolean ativo;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    private String fone;

    // ================= RELACIONAMENTOS =================

    @ManyToOne
    @JoinColumn(name = "empresa_idempresa")
    private Empresa empresa;
    @ManyToOne
    @JoinColumn(name = "filial_idfilial")
    private Filial filial;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "cidade_idcidade", referencedColumnName = "idcidade"),
            @JoinColumn(name = "cidade_estado_idestado", referencedColumnName = "estado_idestado")
    })
    private Cidade cidade;

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    @ManyToOne
    @JoinColumn(name = "tipo_usuario_idtipo_usuario")
    private TipoUsuario tipoUsuario;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getLogin() {
        return login;
    }

    public String getFone() {
        return fone;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
