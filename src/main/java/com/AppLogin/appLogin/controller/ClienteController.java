package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.model.Cliente;
import com.AppLogin.appLogin.model.Usuario;
import com.AppLogin.appLogin.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/mainCliente")
    public String mainCliente(
            HttpServletRequest request,
            Model model,
            @RequestParam(required = false) Long codigo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf
    ) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());

        List<Cliente> clientes;

        if (codigo != null) {
            clientes = clienteRepository.findById(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } else if (nome != null && !nome.isEmpty()) {
            clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        } else if (cpf != null && !cpf.isEmpty()) {
            clientes = clienteRepository.findByCpfContaining(cpf);
        } else {
            clientes = clienteRepository.findAll();
        }

        model.addAttribute("clientes", clientes);

        return "mainCliente";
    }
    @GetMapping("/novoCliente")
    public String novoCliente() {


        return "novoCliente";
    }
    @GetMapping("/editarCliente/{id}")
    public String editarCliente(@PathVariable Long id, HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            // Se cliente não existe, pode redirecionar para a lista
            return "redirect:/mainCliente";
        }

        model.addAttribute("cliente", cliente);
        return "editarCliente";
    }
    @PostMapping("/salvarCliente")
    public String salvarCliente(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String status,
            Model model) {

        // Verifica se já existe um cliente com o mesmo CPF OU nome
        boolean existeCpf = clienteRepository.findByCpfContaining(cpf).size() > 0;
        boolean existeNome = clienteRepository.findByNomeContainingIgnoreCase(nome).size() > 0;

        if (existeCpf) {
            model.addAttribute("erro", "Já existe um cliente com este CPF!");
            return "novoCliente";
        }
        if (existeNome) {
            model.addAttribute("erro", "Já existe um cliente com este Nome!");
            return "novoCliente";
        }


        // Tudo ok? Salva
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setStatus(status);

        clienteRepository.save(cliente);

        // Mensagem de sucesso + redirecionamento
        model.addAttribute("sucesso", "Cliente cadastrado com sucesso!");
        model.addAttribute("rotaRedirect", "/mainCliente");

        return "novoCliente";
    }



}
