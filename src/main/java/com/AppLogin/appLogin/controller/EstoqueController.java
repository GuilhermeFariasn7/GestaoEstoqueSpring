package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstoqueController {

    @GetMapping("/mainEstoque")
    public String estoque(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        return "mainEstoque";
    }
}
