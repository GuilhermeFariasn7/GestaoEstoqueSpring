package com.AppLogin.appLogin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== DADOS BÁSICOS ===== */

    @Column(nullable = false)
    private String nome;

    private String cpf;

    private String cnpj;

    private String telefone;

    private String email;

    @Column(nullable = false)
    private String status;

    private String tipo;

    @Column(length = 150)
    private String descricao;

    /* ===== ENTREGA ===== */

    @Column(name = "entrega_programada")
    private Boolean entregaProgramada;

    @Column(name = "dias_entrega_programada")
    private Integer diasEntregaProgramada;

    /* ===== RELACIONAMENTOS ===== */

    @ManyToOne
    @JoinColumns({
            @JoinColumn(
                    name = "cidade_idcidade",
                    referencedColumnName = "idcidade",
                    nullable = false
            ),
            @JoinColumn(
                    name = "cidade_estado_idestado",
                    referencedColumnName = "estado_idestado",
                    nullable = false
            )
    })
    private Cidade cidade;

    @ManyToOne
    @JoinColumn(name = "ramo_idramo", nullable = false)
    private Ramo ramo;

    /* ===== GETTERS E SETTERS ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getEntregaProgramada() {
        return entregaProgramada;
    }

    public void setEntregaProgramada(Boolean entregaProgramada) {
        this.entregaProgramada = entregaProgramada;
    }

    public Integer getDiasEntregaProgramada() {
        return diasEntregaProgramada;
    }

    public void setDiasEntregaProgramada(Integer diasEntregaProgramada) {
        this.diasEntregaProgramada = diasEntregaProgramada;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Ramo getRamo() {
        return ramo;
    }

    public void setRamo(Ramo ramo) {
        this.ramo = ramo;
    }
}
