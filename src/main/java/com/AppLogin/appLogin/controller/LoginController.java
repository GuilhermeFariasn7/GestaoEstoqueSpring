package com.AppLogin.appLogin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.AppLogin.appLogin.model.Usuario;
import com.AppLogin.appLogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository ur;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/login")
    public String login(HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado != null) {
            return "redirect:/"; // Já logado? Vai pra home
        }

        return "login";
    }

    @GetMapping("/CadastroUsuario")
    public String cadastro() {
        return "cadastro";
    }

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());

        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUsuario(@RequestParam String email,
                               @RequestParam String senha,
                               Model model,
                               HttpSession session) {

        Usuario usuario = ur.findByEmail(email);

        if (usuario != null && encoder.matches(senha, usuario.getSenha())) {
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:/";
        }

        model.addAttribute("erro", "E-mail ou senha inválidos!");
        model.addAttribute("email", email);
        model.addAttribute("senha", senha);
        return "login";
    }

    @PostMapping("/CadastroUsuario")
    public String CadastroUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmaSenha,
            Model model) {

        // Validação senhas iguais
        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            model.addAttribute("nome", nome);
            model.addAttribute("email", email);
            model.addAttribute("senha", senha);
            model.addAttribute("confirmaSenha", confirmaSenha);
            return "cadastro";
        }

        // Validação força senha
        if (!isSenhaForte(senha)) {
            model.addAttribute("erro", "A senha deve ter pelo menos 9 caracteres, uma letra maiúscula e um caractere especial.");
            model.addAttribute("nome", nome);
            model.addAttribute("email", email);
            model.addAttribute("senha", senha);
            model.addAttribute("confirmaSenha", confirmaSenha);
            return "cadastro";
        }

        // Validação e-mail único
        if (ur.findByEmail(email) != null) {
            model.addAttribute("erro", "Já existe um usuário com este e-mail.");
            model.addAttribute("nome", nome);
            model.addAttribute("email", email);
            model.addAttribute("senha", senha);
            model.addAttribute("confirmaSenha", confirmaSenha);
            return "cadastro";
        }

        // Tudo ok? Salva
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(senha));
        ur.save(usuario);

        // Mensagem de sucesso + rota de redirecionamento
        model.addAttribute("sucesso", "Usuário cadastrado com sucesso!");
        model.addAttribute("rotaRedirect", "/login");
        return "cadastro"; // volta pra mesma tela pra mostrar mensagem e js redirecionar
    }

    private boolean isSenhaForte(String senha) {
        if (senha.length() < 9) return false;
        if (!senha.matches(".*[A-Z].*")) return false; // Letra maiúscula?
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false; // Caractere especial?
        return true;
    }
}
