package com.AppLogin.appLogin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recupera_senha_token")
public class RecuperaSenhaToken {

    // Definindo token como PK, pois é gerado manualmente no código
    @Id
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    public RecuperaSenhaToken() {}

    public RecuperaSenhaToken(String token, Usuario usuario, LocalDateTime dataExpiracao) {
        this.token = token;
        this.usuario = usuario;
        this.dataExpiracao = dataExpiracao;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    // Método para verificar se o token está expirado
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }
}
