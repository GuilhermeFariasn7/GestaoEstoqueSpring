package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.model.Usuario;
import com.AppLogin.appLogin.model.RecuperaSenhaToken;
import com.AppLogin.appLogin.repository.TokenRepository;
import com.AppLogin.appLogin.repository.UsuarioRepository;
import com.AppLogin.appLogin.service.EmailService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class RecuperaSenhaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/recuperarSenha")
    public String mostrarFormularioRecuperacao() {
        return "recuperarSenha";
    }

    @PostMapping("/recuperarSenha")
    @Transactional
    public String processarPedidoRecuperacao(@RequestParam String email, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null) {
            try {
                // Deleta token antigo (boa prática)
                tokenRepository.deleteByUsuario(usuario);

                // Gera novo token
                String token = UUID.randomUUID().toString();
                RecuperaSenhaToken novoToken = new RecuperaSenhaToken(token, usuario, LocalDateTime.now().plusHours(1));
                tokenRepository.save(novoToken);

                // Monta link de reset
                String linkReset = "http://localhost:8080/alterarSenha?token=" + token;

                String corpoEmail = "Olá,\n\n"
                        + "Recebemos uma solicitação para redefinir sua senha.\n"
                        + "Para redefinir, clique no link abaixo:\n"
                        + linkReset + "\n\n"
                        + "Se não foi você, ignore este e-mail.\n\n"
                        + "Atenciosamente,\nEquipe de Suporte";

                emailService.enviarEmail(usuario.getEmail(), "Recuperação de Senha", corpoEmail);

            } catch (Exception e) {
                System.err.println("Erro ao processar pedido de recuperação de senha: " + e.getMessage());
                // Se der erro no envio, pode ignorar para não expor falha para o atacante
            }
        }

        // Sempre mostra mensagem genérica + rotaRedirect para login
        model.addAttribute("sucesso", "Se existir, enviaremos instruções para seu e-mail.");
        model.addAttribute("rotaRedirect", "/login");
        return "recuperarSenha";
    }

    @GetMapping("/alterarSenha")
    public String mostrarFormularioAlterarSenha(@RequestParam String token, Model model) {
        RecuperaSenhaToken tokenEntity = tokenRepository.findByToken(token);

        if (tokenEntity == null || tokenEntity.isExpirado()) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "erroToken";
        }

        model.addAttribute("token", token);
        return "alterarSenha";
    }

    @PostMapping("/alterarSenha")
    @Transactional
    public String processarNovaSenha(
            @RequestParam String token,
            @RequestParam String senha,
            @RequestParam String confirmaSenha,
            Model model) {

        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            model.addAttribute("senha", senha);
            model.addAttribute("confirmaSenha", confirmaSenha);
            model.addAttribute("token", token);
            return "alterarSenha";
        }

        RecuperaSenhaToken tokenEntity = tokenRepository.findByToken(token);

        if (tokenEntity == null || tokenEntity.isExpirado()) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "erroToken";
        }

        if (!isSenhaForte(senha)) {
            model.addAttribute("erro", "A senha deve ter pelo menos 9 caracteres, uma letra maiúscula e um caractere especial.");
            model.addAttribute("senha", senha);
            model.addAttribute("confirmaSenha", confirmaSenha);
            model.addAttribute("token", token);
            return "alterarSenha";
        }

        Usuario usuario = tokenEntity.getUsuario();
        usuario.setSenha(encoder.encode(senha));
        usuarioRepository.save(usuario);

        tokenRepository.delete(tokenEntity);

        // Sucesso + rotaRedirect para login
        model.addAttribute("sucesso", "Senha alterada com sucesso!");
        model.addAttribute("rotaRedirect", "/login");
        return "alterarSenha";
    }

    private boolean isSenhaForte(String senha) {
        if (senha.length() < 9) return false;
        if (!senha.matches(".*[A-Z].*")) return false;
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
}
