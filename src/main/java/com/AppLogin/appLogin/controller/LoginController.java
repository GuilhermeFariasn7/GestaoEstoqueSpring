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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    // ================= CONSTANTES =================
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutos
    private static final int SENHA_MIN_LENGTH = 9;

    // ================= LOGIN =================

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

        // Sanitização
        login = sanitize(login);
        senha = sanitize(senha);

        // Validação campos obrigatórios
        if (login.isEmpty() || senha.isEmpty()) {
            return loginError(model, "Informe o login e a senha.", login);
        }

        // Busca usuário
        Usuario usuario = usuarioRepository.findByLogin(login);

        // Valida credenciais
        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            return loginError(model, "Login ou senha inválidos.", login);
        }

        // Valida se usuário está ativo
        if (usuario.getAtivo() != null && !usuario.getAtivo()) {
            return loginError(model, "Usuário inativo. Entre em contato com o administrador.", login);
        }

        // Login OK
        session.setAttribute("usuarioLogado", usuario);
        session.setMaxInactiveInterval(SESSION_TIMEOUT);

        return "redirect:/";
    }

    private String loginError(Model model, String mensagem, String login) {
        model.addAttribute("erro", mensagem);
        model.addAttribute("login", login);
        return "login";
    }

    // ================= HOME =================

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        model.addAttribute("usuario", usuarioLogado);

        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ================= AUX =================

    @GetMapping("/filiais/por-empresa/{idEmpresa}")
    @ResponseBody
    public List<Filial> filiaisPorEmpresa(@PathVariable Integer idEmpresa) {
        System.out.println("ID Empresa: " + idEmpresa);
        return filialRepository.findByEmpresa_Idempresa(idEmpresa);
    }

    // ================= CADASTRO =================

    @GetMapping("/CadastroUsuario")
    public String cadastro(Model model) {
        // Se não há dados de formulário, cria um DTO vazio
        if (!model.containsAttribute("usuarioDTO")) {
            model.addAttribute("usuarioDTO", new UsuarioCadastroDTO());
        }

        carregarDadosBasicos(model);
        return "cadastro";
    }

    @PostMapping("/CadastroUsuario")
    public String cadastroUsuario(@ModelAttribute UsuarioCadastroDTO dto,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // 1. Sanitização e limpeza
        sanitizarDTO(dto);

        // 2. Validações básicas
        String erroValidacao = validarDTO(dto);
        if (erroValidacao != null) {
            return erroCadastro(model, dto, erroValidacao);
        }

        // 3. Verificar duplicidade
        if (usuarioRepository.findByLogin(dto.getLogin()) != null) {
            return erroCadastro(model, dto, "Já existe um usuário com este login.");
        }

        if (usuarioRepository.findByEmail(dto.getEmail()) != null) {
            return erroCadastro(model, dto, "Já existe um usuário com este e-mail.");
        }

        // 4. Validar entidades relacionadas
        ValidacaoResult validacao = validarEntidadesRelacionadas(dto);
        if (validacao.temErro()) {
            return erroCadastro(model, dto, validacao.getMensagem());
        }

        // 5. Salvar usuário
        try {
            Usuario usuario = criarUsuario(dto, validacao);
            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
            redirectAttributes.addFlashAttribute("rotaRedirect", "/login");

            return "redirect:/CadastroUsuario";
        } catch (Exception e) {
            return erroCadastro(model, dto, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    // ================= CLASSES AUXILIARES =================

    /**
     * DTO para transferência de dados do formulário de cadastro
     */
    public static class UsuarioCadastroDTO {
        private String nome;
        private String email;
        private String login;
        private String senha;
        private String confirmaSenha;
        private String telefone;
        private String cep;
        private Integer empresa;
        private Integer filial;
        private String estado;
        private Integer cidade;
        private Integer tipoUsuarioId;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public String getConfirmaSenha() { return confirmaSenha; }
        public void setConfirmaSenha(String confirmaSenha) { this.confirmaSenha = confirmaSenha; }

        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }

        public String getCep() { return cep; }
        public void setCep(String cep) { this.cep = cep; }

        public Integer getEmpresa() { return empresa; }
        public void setEmpresa(Integer empresa) { this.empresa = empresa; }

        public Integer getFilial() { return filial; }
        public void setFilial(Integer filial) { this.filial = filial; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public Integer getCidade() { return cidade; }
        public void setCidade(Integer cidade) { this.cidade = cidade; }

        public Integer getTipoUsuarioId() { return tipoUsuarioId; }
        public void setTipoUsuarioId(Integer tipoUsuarioId) { this.tipoUsuarioId = tipoUsuarioId; }
    }

    /**
     * Resultado de validação de entidades
     */
    private static class ValidacaoResult {
        private Empresa empresa;
        private Filial filial;
        private Estado estado;
        private Cidade cidade;
        private TipoUsuario tipoUsuario;
        private String erro;

        public boolean temErro() {
            return erro != null && !erro.isEmpty();
        }

        public String getMensagem() {
            return erro;
        }

        public void setErro(String erro) {
            this.erro = erro;
        }

        // Getters e Setters
        public Empresa getEmpresa() { return empresa; }
        public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

        public Filial getFilial() { return filial; }
        public void setFilial(Filial filial) { this.filial = filial; }

        public Estado getEstado() { return estado; }
        public void setEstado(Estado estado) { this.estado = estado; }

        public Cidade getCidade() { return cidade; }
        public void setCidade(Cidade cidade) { this.cidade = cidade; }

        public TipoUsuario getTipoUsuario() { return tipoUsuario; }
        public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    }

    // ================= MÉTODOS UTILITÁRIOS =================

    private String sanitize(String valor) {
        return valor != null ? valor.trim() : "";
    }

    private void sanitizarDTO(UsuarioCadastroDTO dto) {
        dto.setNome(sanitize(dto.getNome()));
        dto.setEmail(sanitize(dto.getEmail()));
        dto.setLogin(sanitize(dto.getLogin()));

        // Limpeza de máscaras
        if (dto.getTelefone() != null) {
            dto.setTelefone(dto.getTelefone().replaceAll("\\D", ""));
        }
        if (dto.getCep() != null) {
            dto.setCep(dto.getCep().replaceAll("\\D", ""));
        }
    }

    private String validarDTO(UsuarioCadastroDTO dto) {
        // Campos obrigatórios
        if (isBlank(dto.getNome())) return "Nome é obrigatório.";
        if (isBlank(dto.getEmail())) return "E-mail é obrigatório.";
        if (isBlank(dto.getLogin())) return "Login é obrigatório.";
        if (isBlank(dto.getSenha())) return "Senha é obrigatória.";
        if (isBlank(dto.getConfirmaSenha())) return "Confirmação de senha é obrigatória.";

        if (dto.getEmpresa() == null) return "Empresa é obrigatória.";
        if (dto.getFilial() == null) return "Filial é obrigatória.";
        if (isBlank(dto.getEstado())) return "Estado é obrigatório.";
        if (dto.getCidade() == null) return "Cidade é obrigatória.";
        if (dto.getTipoUsuarioId() == null) return "Tipo de usuário é obrigatório.";

        // Validações de formato
        if (!dto.getSenha().equals(dto.getConfirmaSenha())) {
            return "As senhas não coincidem.";
        }

        if (!isSenhaForte(dto.getSenha())) {
            return "A senha deve ter pelo menos 9 caracteres, uma letra maiúscula e um caractere especial.";
        }

        if (!isEmailValido(dto.getEmail())) {
            return "E-mail inválido.";
        }

        return null;
    }

    private ValidacaoResult validarEntidadesRelacionadas(UsuarioCadastroDTO dto) {
        ValidacaoResult result = new ValidacaoResult();

        // Empresa
        Optional<Empresa> empresaOpt = empresaRepository.findById(dto.getEmpresa());
        if (empresaOpt.isEmpty()) {
            result.setErro("Empresa inválida.");
            return result;
        }
        result.setEmpresa(empresaOpt.get());

        // Filial
        Optional<Filial> filialOpt = filialRepository.findById(dto.getFilial());
        if (filialOpt.isEmpty() || !filialOpt.get().getEmpresa().getIdempresa().equals(dto.getEmpresa())) {
            result.setErro("Filial inválida para a empresa selecionada.");
            return result;
        }
        result.setFilial(filialOpt.get());

        // Estado
        Optional<Estado> estadoOpt = estadoRepository.findById(dto.getEstado());
        if (estadoOpt.isEmpty()) {
            result.setErro("Estado inválido.");
            return result;
        }
        result.setEstado(estadoOpt.get());

        // Cidade
        Optional<Cidade> cidadeOpt = cidadeRepository.findById(dto.getCidade());
        if (cidadeOpt.isEmpty() || !cidadeOpt.get().getEstado().getIdestado().equals(dto.getEstado())) {
            result.setErro("Cidade não pertence ao estado selecionado.");
            return result;
        }
        result.setCidade(cidadeOpt.get());

        // Tipo Usuário
        Optional<TipoUsuario> tipoOpt = tipoUsuarioRepository.findById(dto.getTipoUsuarioId());
        if (tipoOpt.isEmpty()) {
            result.setErro("Tipo de usuário inválido.");
            return result;
        }
        result.setTipoUsuario(tipoOpt.get());

        return result;
    }

    private Usuario criarUsuario(UsuarioCadastroDTO dto, ValidacaoResult validacao) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setLogin(dto.getLogin());
        usuario.setFone(dto.getTelefone());
        usuario.setCep(dto.getCep());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setEmpresa(validacao.getEmpresa());
        usuario.setFilial(validacao.getFilial());
        usuario.setCidade(validacao.getCidade());
        usuario.setTipoUsuario(validacao.getTipoUsuario());
        usuario.setAtivo(true);

        return usuario;
    }

    private String erroCadastro(Model model, UsuarioCadastroDTO dto, String mensagem) {
        model.addAttribute("erro", mensagem);
        model.addAttribute("usuarioDTO", dto); // PRESERVA TODOS OS CAMPOS!
        carregarDadosBasicos(model);
        return "cadastro";
    }

    private void carregarDadosBasicos(Model model) {
        model.addAttribute("tipoUsuarios", tipoUsuarioRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("estados", estadoRepository.findAll());
        model.addAttribute("cidades", cidadeRepository.findAll());
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isSenhaForte(String senha) {
        if (senha == null || senha.length() < SENHA_MIN_LENGTH) return false;
        if (!senha.matches(".*[A-Z].*")) return false;
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
}