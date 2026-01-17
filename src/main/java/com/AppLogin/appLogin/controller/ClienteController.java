package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.model.*;
import com.AppLogin.appLogin.repository.*;
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

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private RamoRepository ramoRepository;

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
        model.addAttribute("empresaUsuario", usuarioLogado.getEmpresa().getNome());
        model.addAttribute("filialUsuario", usuarioLogado.getFilial().getNome());
        model.addAttribute("empresaId", usuarioLogado.getEmpresa().getIdempresa());
        model.addAttribute("filialId", usuarioLogado.getFilial().getIdfilial());

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
    public String novoCliente(Model model) {
        model.addAttribute("tipoUsuarios", tipoUsuarioRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());
        model.addAttribute("ramos", ramoRepository.findAll());
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

        // ============================
        // DADOS PRINCIPAIS
        // ============================
        model.addAttribute("cliente", cliente);

        // ============================
        // LISTAS PARA OS SELECTS
        // ============================
        model.addAttribute("ramos", ramoRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());

        return "editarCliente";
    }


    @PostMapping("/salvarCliente")
    public String salvarCliente(
            @RequestParam String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String descricao,
            @RequestParam Integer ramoId,
            @RequestParam String estado,
            @RequestParam Integer cidade,
            @RequestParam Integer entregaProgramada,
            @RequestParam(required = false) Integer diasEntregaProgramada,
            @RequestParam String status,
            Model model
    ) {

        /* ===== LIMPAR MÁSCARAS ===== */
        if (cpf != null) cpf = cpf.replaceAll("\\D", "");
        if (cnpj != null) cnpj = cnpj.replaceAll("\\D", "");
        if (telefone != null) telefone = telefone.replaceAll("\\D", "");

        String cpfFinal = null;
        String cnpjFinal = null;

        /* ===== VALIDAR DOCUMENTO ===== */
        if (cpf != null && !cpf.isBlank()) {
            if (cpf.length() != 11) {
                return erroCliente(model, "CPF inválido.",
                        nome, cpf, cnpj, telefone, email, tipo, descricao,
                        ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
            }

            cpfFinal = cpf;

            if (clienteRepository.findFirstByCpfAndStatus(cpfFinal, "ATIVO").isPresent()) {
                return erroCliente(model, "Já existe um cliente com este CPF!",
                        nome, cpf, cnpj, telefone, email, tipo, descricao,
                        ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
            }
        }

        if (cnpj != null && !cnpj.isBlank()) {
            if (cnpj.length() != 14) {
                return erroCliente(model, "CNPJ inválido.",
                        nome, cpf, cnpj, telefone, email, tipo, descricao,
                        ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
            }

            cnpjFinal = cnpj;

            if (clienteRepository.findFirstByCnpjAndStatus(cnpjFinal, "ATIVO").isPresent()) {
                return erroCliente(model, "Já existe um cliente com este CNPJ!",
                        nome, cpf, cnpj, telefone, email, tipo, descricao,
                        ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
            }
        }

        if ((cpfFinal == null || cpfFinal.isBlank()) && (cnpjFinal == null || cnpjFinal.isBlank())) {
            return erroCliente(model, "Informe CPF ou CNPJ.",
                    nome, cpf, cnpj, telefone, email, tipo, descricao,
                    ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
        }

        /* ===== RAMO ===== */
        Optional<Ramo> ramoOpt = ramoRepository.findById(ramoId);
        if (ramoOpt.isEmpty()) {
            return erroCliente(model, "Ramo inválido.",
                    nome, cpf, cnpj, telefone, email, tipo, descricao,
                    ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
        }

        /* ===== ESTADO ===== */
        Optional<Estado> estadoOpt = estadoRepository.findById(estado);
        if (estadoOpt.isEmpty()) {
            return erroCliente(model, "Estado inválido.",
                    nome, cpf, cnpj, telefone, email, tipo, descricao,
                    ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
        }

        /* ===== CIDADE ===== */
        Optional<Cidade> cidadeOpt = cidadeRepository.findById(cidade);
        if (cidadeOpt.isEmpty() || !cidadeOpt.get().getEstado().getIdestado().equals(estado)) {
            return erroCliente(model, "Cidade não pertence ao estado selecionado.",
                    nome, cpf, cnpj, telefone, email, tipo, descricao,
                    ramoId, estado, cidade, entregaProgramada, diasEntregaProgramada, status);
        }

        /* ===== SALVAR ===== */
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpfFinal);
        cliente.setCnpj(cnpjFinal);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setTipo(tipo);
        cliente.setDescricao(descricao);
        cliente.setRamo(ramoOpt.get());
        cliente.setCidade(cidadeOpt.get());
        cliente.setEntregaProgramada(entregaProgramada == 1);
        cliente.setDiasEntregaProgramada(diasEntregaProgramada);
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
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String descricao,
            @RequestParam Integer ramoId,
            @RequestParam String estado,
            @RequestParam Integer cidade,
            @RequestParam Integer entregaProgramada,
            @RequestParam(required = false) Integer diasEntregaProgramada,
            @RequestParam String status,
            Model model
    ) {

        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if (optionalCliente.isEmpty()) {
            model.addAttribute("erro", "Cliente não encontrado!");
            return "redirect:/mainCliente";
        }

        Cliente cliente = optionalCliente.get();

    /* ===============================
       LIMPAR MÁSCARAS
    ================================ */
        if (cpf != null) cpf = cpf.replaceAll("\\D", "");
        if (cnpj != null) cnpj = cnpj.replaceAll("\\D", "");
        if (telefone != null) telefone = telefone.replaceAll("\\D", "");

        String cpfFinal = null;
        String cnpjFinal = null;

    /* ===============================
       VALIDAR DOCUMENTO
    ================================ */
        if (cpf != null && !cpf.isBlank()) {
            if (cpf.length() != 11) {
                return erroEdicao(model, cliente, "CPF inválido.");
            }

            cpfFinal = cpf;

            Optional<Cliente> existente = clienteRepository.findFirstByCpfAndStatus(cpfFinal, "ATIVO");
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                return erroEdicao(model, cliente, "Já existe outro cliente com este CPF!");
            }
        }

        if (cnpj != null && !cnpj.isBlank()) {
            if (cnpj.length() != 14) {
                return erroEdicao(model, cliente, "CNPJ inválido.");
            }

            cnpjFinal = cnpj;

            Optional<Cliente> existente = clienteRepository.findFirstByCnpjAndStatus(cnpjFinal, "ATIVO");
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                return erroEdicao(model, cliente, "Já existe outro cliente com este CNPJ!");
            }
        }

        if ((cpfFinal == null || cpfFinal.isBlank()) && (cnpjFinal == null || cnpjFinal.isBlank())) {
            return erroEdicao(model, cliente, "Informe CPF ou CNPJ.");
        }

    /* ===============================
       RAMO
    ================================ */
        Optional<Ramo> ramoOpt = ramoRepository.findById(ramoId);
        if (ramoOpt.isEmpty()) {
            return erroEdicao(model, cliente, "Ramo inválido.");
        }

    /* ===============================
       ESTADO / CIDADE
    ================================ */
        Optional<Cidade> cidadeOpt = cidadeRepository.findById(cidade);
        if (cidadeOpt.isEmpty() || !cidadeOpt.get().getEstado().getIdestado().equals(estado)) {
            return erroEdicao(model, cliente, "Cidade não pertence ao estado selecionado.");
        }

    /* ===============================
       ATUALIZAR CAMPOS
    ================================ */
        cliente.setNome(nome);
        cliente.setCpf(cpfFinal);
        cliente.setCnpj(cnpjFinal);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setTipo(tipo);
        cliente.setDescricao(descricao);
        cliente.setRamo(ramoOpt.get());
        cliente.setCidade(cidadeOpt.get());
        cliente.setEntregaProgramada(entregaProgramada == 1);
        cliente.setDiasEntregaProgramada(diasEntregaProgramada);
        cliente.setStatus(status);

        clienteRepository.save(cliente);

        model.addAttribute("sucesso", "Cliente atualizado com sucesso!");
        model.addAttribute("rotaRedirect", "/mainCliente");
        model.addAttribute("cliente", cliente);

        // Recarregar listas
        model.addAttribute("ramos", ramoRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());

        return "editarCliente";
    }


    private String erroCliente(
            Model model,
            String mensagem,
            String nome,
            String cpf,
            String cnpj,
            String telefone,
            String email,
            String tipo,
            String descricao,
            Integer ramoId,
            String estado,
            Integer cidade,
            Integer entregaProgramada,
            Integer diasEntregaProgramada,
            String status
    ) {
        model.addAttribute("erro", mensagem);

        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("cnpj", cnpj);
        model.addAttribute("telefone", telefone);
        model.addAttribute("email", email);
        model.addAttribute("tipo", tipo);
        model.addAttribute("descricao", descricao);
        model.addAttribute("ramoId", ramoId);
        model.addAttribute("estado", estado);
        model.addAttribute("cidade", cidade);
        model.addAttribute("entregaProgramada", entregaProgramada);
        model.addAttribute("diasEntregaProgramada", diasEntregaProgramada);
        model.addAttribute("status", status);

        model.addAttribute("ramos", ramoRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());
        return "novoCliente";
    }

    private String erroEdicao(Model model, Cliente cliente, String mensagem) {

        model.addAttribute("erro", mensagem);
        model.addAttribute("cliente", cliente);

        model.addAttribute("ramos", ramoRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());

        return "editarCliente";
    }


    @GetMapping("/excluirCliente/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/mainCliente";
    }
}
