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
import java.util.Optional;

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

        Optional<Cliente> clienteCpfAtivo = clienteRepository.findFirstByCpfAndStatus(cpf, "ATIVO");
        Optional<Cliente> clienteNomeAtivo = clienteRepository.findFirstByNomeAndStatus(nome, "ATIVO");

        if (clienteCpfAtivo.isPresent()) {
            model.addAttribute("erro", "Já existe um cliente com este CPF!");
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
            return "novoCliente";
        }
        if (clienteNomeAtivo.isPresent()) {
            model.addAttribute("erro", "Já existe um cliente com este Nome!");
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
            return "novoCliente";
        }

        // Validação de telefone (ex: (99) 99999-9999)
        if (!telefone.matches("\\(\\d{2}\\)\\s9\\d{4}-\\d{4}")) {
            model.addAttribute("erro", "Telefone inválido. Use o formato (DD) 9XXXX-XXXX.");
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
            return "novoCliente";
        }

        // Validação simplificada de CPF ou CNPJ (apenas tamanho)
        String documento = cpf.replaceAll("[^\\d]", "");
        if (documento.length() != 11 && documento.length() != 14) {
            model.addAttribute("erro", "Documento deve ser CPF (11 dígitos) ou CNPJ (14 dígitos).");
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
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

        model.addAttribute("sucesso", "Cliente cadastrado com sucesso!");
        model.addAttribute("rotaRedirect", "/mainCliente");

        return "novoCliente";
    }

    @PostMapping("/atualizarCliente")
    public String atualizarCliente(
            @RequestParam Long id,
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String status,
            Model model) {

        Optional<Cliente> optionalCliente = clienteRepository.findById(id);

        if (optionalCliente.isPresent()) {
            Cliente cliente = optionalCliente.get();

            // Validação de telefone (ex: (99) 99999-9999)
            if (!telefone.matches("\\(\\d{2}\\)\\s9\\d{4}-\\d{4}")) {
                model.addAttribute("erro", "Telefone inválido. Use o formato (DD) 9XXXX-XXXX.");
                model.addAttribute("id", id);
                model.addAttribute("nome", nome);
                model.addAttribute("cpf", cpf);
                model.addAttribute("telefone", telefone);
                model.addAttribute("email", email);
                model.addAttribute("status", status);
                return "novoCliente";
            }

            // Validação simplificada de CPF ou CNPJ (apenas tamanho)
            String documento = cpf.replaceAll("[^\\d]", "");
            if (documento.length() != 11 && documento.length() != 14) {
                model.addAttribute("erro", "Documento deve ser CPF (11 dígitos) ou CNPJ (14 dígitos).");
                model.addAttribute("id", id);
                model.addAttribute("nome", nome);
                model.addAttribute("cpf", cpf);
                model.addAttribute("telefone", telefone);
                model.addAttribute("email", email);
                model.addAttribute("status", status);
                return "novoCliente";
            }

            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setTelefone(telefone);
            cliente.setEmail(email);
            cliente.setStatus(status);

            clienteRepository.save(cliente);

            model.addAttribute("sucesso", "Cliente atualizado com sucesso!");
            model.addAttribute("id", id);
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
            model.addAttribute("rotaRedirect", "/mainCliente");
            return "novoCliente";
        } else {
            model.addAttribute("erro", "Cliente não encontrado!");
            model.addAttribute("id", id);
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("status", status);
            return "erro";
        }
    }

    @GetMapping("/excluirCliente/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/mainCliente";
    }
}
