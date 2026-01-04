package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.model.*;
import com.AppLogin.appLogin.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository ur;

    @Autowired
    private BCryptPasswordEncoder encoder;

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

    /* ================= LOGIN ================= */

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUsuario(@RequestParam String login,
                               @RequestParam String senha,
                               Model model,
                               HttpSession session) {

        // Sanitização básica
        login = login != null ? login.trim() : "";
        senha = senha != null ? senha.trim() : "";

        // Validação campos obrigatórios
        if (login.isEmpty() || senha.isEmpty()) {
            model.addAttribute("erro", "Informe o login e a senha.");
            model.addAttribute("login", login);
            return "login";
        }

        // Busca usuário pelo LOGIN (não mais por e-mail)
        Usuario usuario = ur.findByLogin(login);

        // Login inválido ou senha incorreta
        if (usuario == null || !encoder.matches(senha, usuario.getSenha())) {
            model.addAttribute("erro", "Login ou senha inválidos.");
            model.addAttribute("login", login);
            return "login";
        }

        // (Opcional) usuário inativo
        if (usuario.getAtivo() != null && !usuario.getAtivo()) {
            model.addAttribute("erro", "Usuário inativo. Entre em contato com o administrador.");
            model.addAttribute("login", login);
            return "login";
        }

        // Login OK
        session.setAttribute("usuarioLogado", usuario);
        session.setMaxInactiveInterval(30 * 60); // 30 minutos

        return "redirect:/";
    }


    /* ================= HOME ================= */

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

    /* ================= AUX ================= */

    @GetMapping("/filiais/por-empresa/{idEmpresa}")
    @ResponseBody
    public List<Filial> filiaisPorEmpresa(@PathVariable Integer idEmpresa) {
        return filialRepository.findByEmpresa_Idempresa(idEmpresa);
    }

    /* ================= CADASTRO ================= */

    @GetMapping("/CadastroUsuario")
    public String cadastro(Model model) {
        model.addAttribute("tipoUsuarios", tipoUsuarioRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());
        return "cadastro";
    }

    @PostMapping("/CadastroUsuario")
    public String CadastroUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String login,
            @RequestParam String senha,
            @RequestParam String confirmaSenha,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String cep,
            @RequestParam Integer empresa,
            @RequestParam Integer filial,
            @RequestParam String estado,
            @RequestParam Integer cidade,
            @RequestParam Integer tipoUsuarioId,
            Model model) {

        /* ===== LIMPEZA DE MÁSCARAS ===== */
        if (telefone != null) {
            telefone = telefone.replaceAll("\\D", "");
        }

        if (cep != null) {
            cep = cep.replaceAll("\\D", "");
        }

        /* Validar usuário já cadastrado */

        if (ur.findByLogin(login) != null) {
            model.addAttribute("erro", "Já existe um usuário com este login.");
            return "cadastro";
        }

        /* ===== SENHAS ===== */
        if (!senha.equals(confirmaSenha)) {
            return erro(model, "As senhas não coincidem.", nome, email, login);
        }

        if (!isSenhaForte(senha)) {
            return erro(model,
                    "A senha deve ter pelo menos 9 caracteres, uma letra maiúscula e um caractere especial.",
                    nome, email, login);
        }

        /* ===== EMAIL ÚNICO ===== */
        if (ur.findByEmail(email) != null) {
            return erro(model, "Já existe um usuário com este e-mail.", nome, email, login);
        }

        /* ===== EMPRESA ===== */
        Optional<Empresa> empresaOpt = empresaRepository.findById(empresa);
        if (empresaOpt.isEmpty()) {
            return erro(model, "Empresa inválida.", nome, email, login);
        }

        /* ===== FILIAL ===== */
        Optional<Filial> filialOpt = filialRepository.findById(filial);
        if (filialOpt.isEmpty()
                || !filialOpt.get().getEmpresa().getIdempresa().equals(empresa)) {
            return erro(model, "Filial inválida para a empresa selecionada.", nome, email, login);
        }

        /* ===== ESTADO ===== */
        Optional<Estado> estadoOpt = estadoRepository.findById(estado);
        if (estadoOpt.isEmpty()) {
            return erro(model, "Estado inválido.", nome, email, login);
        }

        /* ===== CIDADE ===== */
        Optional<Cidade> cidadeOpt = cidadeRepository.findById(cidade);
        if (cidadeOpt.isEmpty()
                || !cidadeOpt.get().getEstado().getIdestado().equals(estado)) {
            return erro(model, "Cidade não pertence ao estado selecionado.", nome, email, login);
        }

        /* ===== TIPO USUÁRIO ===== */
        Optional<TipoUsuario> tipoOpt = tipoUsuarioRepository.findById(tipoUsuarioId);
        if (tipoOpt.isEmpty()) {
            return erro(model, "Tipo de usuário inválido.", nome, email, login);
        }

        /* ===== CEP x CIDADE ===== */
        if (cep != null && !cep.isBlank()) {
            if (cep.length() != 8) {
                return erro(model, "CEP inválido.", nome, email, login);
            }

            boolean cepCompatível = cidadeRepository.existsById(cidade);
            if (!cepCompatível) {
                return erro(model,
                        "O CEP informado não corresponde ao estado e cidade selecionados. " +
                                "Atualize os dados ou deixe o CEP em branco.",
                        nome, email, login);
            }
        }

        /* ===== SALVAR ===== */
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setFone(telefone);
        usuario.setSenha(encoder.encode(senha));
        usuario.setEmpresa(empresaOpt.get());
        usuario.setFilial(filialOpt.get());
        usuario.setCidade(cidadeOpt.get());
        usuario.setTipoUsuario(tipoOpt.get());
        usuario.setAtivo(true);

        ur.save(usuario);

        model.addAttribute("sucesso", "Usuário cadastrado com sucesso!");
        model.addAttribute("rotaRedirect", "/login");
        return "cadastro";
    }

    /* ================= UTIL ================= */

    private String erro(Model model, String mensagem, String nome, String email, String login) {
        model.addAttribute("erro", mensagem);
        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("login", login);
        return "cadastro";
    }

    private boolean isSenhaForte(String senha) {
        if (senha.length() < 9) return false;
        if (!senha.matches(".*[A-Z].*")) return false;
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
}
