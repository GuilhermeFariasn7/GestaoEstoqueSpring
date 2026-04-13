package com.AppLogin.appLogin.controller;

import com.AppLogin.appLogin.dto.ProdutoDTO;
import com.AppLogin.appLogin.model.*;
import com.AppLogin.appLogin.repository.*;
import com.AppLogin.appLogin.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class ProdutoController {

    private final ProdutoService produtoService;

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    @Autowired
    private GrupoProdutoRepository grupoProdutoRepository;

    @Autowired
    private UnidadeMedidaRepository unidadeMedidaRepository;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/novoProduto")
    public String novoProduto(HttpSession session, Model model) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuario.getNome());
        model.addAttribute("produto", new ProdutoDTO());
        carregarDadosBasicos(model);

        return "novoProduto";
    }

    @GetMapping("/mainProduto")
    public String mainProduto(HttpSession session, Model model) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        model.addAttribute("empresaUsuario", usuarioLogado.getEmpresa().getNome());
        model.addAttribute("filialUsuario", usuarioLogado.getFilial().getNome());
        model.addAttribute("empresaId", usuarioLogado.getEmpresa().getIdempresa());
        model.addAttribute("filialId", usuarioLogado.getFilial().getIdfilial());
        model.addAttribute("produtos", produtoService.listarTodos());
        carregarDadosBasicos(model);

        return "mainProduto";
    }

    @GetMapping("/editarProduto/{id}")
    public String editarProduto(@PathVariable Long id, HttpSession session, Model model) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            model.addAttribute("nomeUsuario", usuario.getNome());
            carregarDadosBasicos(model);

            Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
            if (produtoOpt.isPresent()) {
                model.addAttribute("produto", new ProdutoDTO(produtoOpt.get()));
            } else {
                return "redirect:/mainProduto";
            }

        } catch (Exception e) {
            return "redirect:/mainProduto";
        }

        return "novoProduto";
    }

    @PostMapping("/salvarProduto")
    public String salvarProduto(
            HttpSession session,
            @Valid @ModelAttribute("produto") ProdutoDTO produtoDTO,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
            BindingResult result,
            Model model) {

        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("nomeUsuario", usuario.getNome());
        carregarDadosBasicos(model);

        if (temErrosValidacao(produtoDTO, result)) {
            model.addAttribute("erro", "Por favor, corrija os erros abaixo.");
            return "novoProduto";
        }

        try {
            if (produtoDTO.getId() != null && produtoDTO.getId() > 0) {
                produtoService.atualizarProduto(produtoDTO.getId(), produtoDTO, imagem);
                model.addAttribute("sucesso", "Produto atualizado com sucesso!");
            } else {
                produtoService.salvarProduto(produtoDTO, imagem);
                model.addAttribute("sucesso", "Produto salvo com sucesso!");
                model.addAttribute("produto", new ProdutoDTO());
            }
            model.addAttribute("rotaRedirect", "/mainProduto");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }

        return "novoProduto";
    }

    @PostMapping("/produto/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, HttpSession session) {
        if (!isUsuarioLogado(session)) {
            return "redirect:/login";
        }
        try {
            produtoService.deletarProduto(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/mainProduto";
    }

    private boolean isUsuarioLogado(HttpSession session) {
        return session != null && session.getAttribute("usuarioLogado") != null;
    }

    private boolean temErrosValidacao(ProdutoDTO produtoDTO, BindingResult result) {
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

    private void carregarDadosBasicos(Model model) {
        model.addAttribute("tipoProdutos", tipoProdutoRepository.findAll());
        model.addAttribute("grupoProdutos", grupoProdutoRepository.findAll());
        model.addAttribute("unidadeMedidas", unidadeMedidaRepository.findAll());
    }
}