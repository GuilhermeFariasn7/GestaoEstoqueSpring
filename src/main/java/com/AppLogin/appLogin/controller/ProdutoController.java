package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.dto.ProdutoDTO;
import com.AppLogin.appLogin.model.Usuario;
import com.AppLogin.appLogin.repository.*;
import com.AppLogin.appLogin.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class ProdutoController {

    private final ProdutoService produtoService;

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

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/novoProduto")
    public String novoProduto(HttpSession session, Model model) {
        model.addAttribute("tipoUsuarios", tipoUsuarioRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuario.getNome());
        model.addAttribute("produto", new ProdutoDTO());

        return "novoProduto";
    }

    @GetMapping("/mainProduto")
    public String mainProduto(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Long codigo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String grupo
    ) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // ==========================
        // DADOS DO HEADER (IGUAL CLIENTE)
        // ==========================
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        model.addAttribute("empresaUsuario", usuarioLogado.getEmpresa().getNome());
        model.addAttribute("filialUsuario", usuarioLogado.getFilial().getNome());
        model.addAttribute("empresaId", usuarioLogado.getEmpresa().getIdempresa());
        model.addAttribute("filialId", usuarioLogado.getFilial().getIdfilial());

        // ==========================
        // FILTRO
        // ==========================
        List<ProdutoDTO> produtos;

        /*if (codigo != null) {
            produtos = produtoService.buscarPorIdLista(codigo); // você cria isso
        } else if (nome != null && !nome.isBlank()) {
            produtos = produtoService.buscarPorNome(nome);
        } else if (tipo != null && !tipo.isBlank()) {
            produtos = produtoService.buscarPorTipo(tipo);
        } else if (grupo != null && !grupo.isBlank()) {
            produtos = produtoService.buscarPorGrupo(grupo);
        } else {
            produtos = produtoService.listarTodosProdutos();
        }*/

        /*model.addAttribute("produtos", produtos);*/

        return "mainProduto";
    }


    @PostMapping("/salvarProduto")
    public String salvarProduto(
            HttpSession session,
            @Valid @ModelAttribute("produto") ProdutoDTO produtoDTO,
            BindingResult result,
            Model model)  {

        if (model == null) {
            return "redirect:/novoProduto";
        }

        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuario.getNome());

        try {
            // Validações
            if (temErrosValidacao(produtoDTO, result)) {
                model.addAttribute("erro", "Por favor, corrija os erros abaixo.");
                return "novoProduto";
            }

            if (produtoDTO.getId() != null) {
                produtoService.atualizarProduto(produtoDTO.getId(), produtoDTO, null);
            } else {
                produtoService.salvarProduto(produtoDTO, null);
            }

            model.addAttribute("sucesso", "Produto salvo com sucesso!");
            model.addAttribute("produto", new ProdutoDTO());
            model.addAttribute("rotaRedirect", "/mainProduto");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }

        return "mainProduto";
    }

    @GetMapping("/produto/imagem/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> exibirImagemProduto(@PathVariable Long id) {
        try {

            byte[] imagemBytes = produtoService.obterImagemProduto(id);

            if (imagemBytes != null) {
                InputStream inputStream = new ByteArrayInputStream(imagemBytes);
                InputStreamResource resource = new InputStreamResource(inputStream);

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                // Imagem padrão se não tiver
                InputStream imagemPadrao = getClass().getResourceAsStream("/static/img/SEMIMAGEM.jpg");
                if (imagemPadrao != null) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_PNG)
                            .body(new InputStreamResource(imagemPadrao));
                }
            }
        } catch (Exception e) {
            // Log do erro
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/produto/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, HttpSession session) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        try {
            produtoService.deletarProduto(id);
        } catch (Exception e) {
            // Log do erro
            e.printStackTrace();
        }

        return "redirect:/mainProduto";
    }


    @GetMapping("/editarProduto/{id}")
    public String editarProduto(@PathVariable Long id, HttpSession session, Model model) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            model.addAttribute("nomeUsuario", usuario.getNome());

            var produtoOpt = produtoService.buscarPorId(id);
            if (produtoOpt.isPresent()) {
                ProdutoDTO produtoDTO = new ProdutoDTO(produtoOpt.get());
                model.addAttribute("produto", produtoDTO);
            } else {
                model.addAttribute("erro", "Produto não encontrado");
                return "redirect:/mainProduto";
            }

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produto: " + e.getMessage());
            return "redirect:/mainProduto";
        }

        return "novoProduto"; // Reutiliza a mesma página de cadastro
    }

    private boolean isUsuarioLogado(HttpSession session) {
        return session != null && session.getAttribute("usuarioLogado") != null;
    }

    private boolean temErrosValidacao(ProdutoDTO produtoDTO, BindingResult result) {
        // Validações de negócio
        if (produtoDTO.getPrecoCusto() != null && produtoDTO.getPrecoCusto().compareTo(BigDecimal.ZERO) < 0) {
            result.rejectValue("precoCusto", "error.precoCusto", "Preço de custo não pode ser negativo");
        }
        if (produtoDTO.getPrecoVenda() != null && produtoDTO.getPrecoVenda().compareTo(BigDecimal.ZERO) < 0) {
            result.rejectValue("precoVenda", "error.precoVenda", "Preço de venda não pode ser negativo");
        }
        if (produtoDTO.getEstoqueAtual() != null && produtoDTO.getEstoqueAtual().compareTo(BigDecimal.ZERO) < 0) {
            result.rejectValue("estoqueAtual", "error.estoqueAtual", "Estoque atual não pode ser negativo");
        }

        return result.hasErrors();
    }
}