package com.AppLogin.appLogin.service;

import com.AppLogin.appLogin.dto.ProdutoDTO;
import com.AppLogin.appLogin.model.*;
import com.AppLogin.appLogin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final TipoProdutoRepository tipoProdutoRepository;
    private final GrupoProdutoRepository grupoProdutoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public ProdutoService(ProdutoRepository produtoRepository,
                          TipoProdutoRepository tipoProdutoRepository,
                          GrupoProdutoRepository grupoProdutoRepository,
                          UnidadeMedidaRepository unidadeMedidaRepository) {
        this.produtoRepository = produtoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.grupoProdutoRepository = grupoProdutoRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream().map(ProdutoDTO::new).toList();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto salvarProduto(ProdutoDTO dto, MultipartFile imagem) throws IOException {
        Produto produto = new Produto();

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setEstoqueAtual(dto.getEstoqueAtual());
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        produto.setEstoqueMaximo(dto.getEstoqueMaximo());
        produto.setDataCadastro(LocalDateTime.now());
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        if (dto.getTipoId() != null && dto.getTipoId() > 0) {
            TipoProduto tipo = tipoProdutoRepository.findById(dto.getTipoId())
                    .orElseThrow(() -> new RuntimeException("Tipo não encontrado"));
            produto.setTipo(tipo);
        }

        if (dto.getGrupoId() != null && dto.getGrupoId() > 0) {
            GrupoProduto grupo = grupoProdutoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
            produto.setGrupo(grupo);
        }

        if (dto.getUnidadeMedidaId() != null && !dto.getUnidadeMedidaId().isEmpty()) {
            UnidadeMedida unidade = unidadeMedidaRepository.findById(dto.getUnidadeMedidaId())
                    .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
            produto.setUnidadeMedida(unidade);
        }

        Produto produtoSalvo = produtoRepository.save(produto);

        if (imagem != null && !imagem.isEmpty()) {
            String url = cloudinaryService.uploadFoto(imagem, "produtos", "produto_" + produtoSalvo.getId());
            produtoSalvo.setFotoUrl(url);
            produtoRepository.save(produtoSalvo);
        }

        return produtoSalvo;
    }

    public Produto atualizarProduto(Long id, ProdutoDTO dto, MultipartFile imagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setEstoqueAtual(dto.getEstoqueAtual());
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        produto.setEstoqueMaximo(dto.getEstoqueMaximo());
        produto.setAtivo(dto.getAtivo());

        if (dto.getTipoId() != null && dto.getTipoId() > 0) {
            TipoProduto tipo = tipoProdutoRepository.findById(dto.getTipoId())
                    .orElseThrow(() -> new RuntimeException("Tipo não encontrado"));
            produto.setTipo(tipo);
        }

        if (dto.getGrupoId() != null && dto.getGrupoId() > 0) {
            GrupoProduto grupo = grupoProdutoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
            produto.setGrupo(grupo);
        }

        if (dto.getUnidadeMedidaId() != null && !dto.getUnidadeMedidaId().isEmpty()) {
            UnidadeMedida unidade = unidadeMedidaRepository.findById(dto.getUnidadeMedidaId())
                    .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
            produto.setUnidadeMedida(unidade);
        }

        if (imagem != null && !imagem.isEmpty()) {
            String url = cloudinaryService.uploadFoto(imagem, "produtos", "produto_" + id);
            produto.setFotoUrl(url);
        }

        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }
}