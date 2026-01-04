package com.AppLogin.appLogin.service;

import com.AppLogin.appLogin.dto.ProdutoDTO;
import com.AppLogin.appLogin.model.Produto;
import com.AppLogin.appLogin.repository.ProdutoRepository;
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
    private final FileStorageService fileStorageService;

    public ProdutoService(ProdutoRepository produtoRepository, FileStorageService fileStorageService) {
        this.produtoRepository = produtoRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<ProdutoDTO> listarTodosProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(ProdutoDTO::new)
                .toList();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto salvarProduto(ProdutoDTO produtoDTO, MultipartFile imagem) throws IOException {
        Produto produto = new Produto();

        // Mapear campos
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setTipo(produtoDTO.getTipo());
        produto.setGrupo(produtoDTO.getGrupo());
        produto.setCodigoBarras(produtoDTO.getCodigoBarras());
        produto.setUnidadeMedida(produtoDTO.getUnidadeMedida());
        produto.setPrecoCusto(produtoDTO.getPrecoCusto());
        produto.setPrecoVenda(produtoDTO.getPrecoVenda());
        produto.setEstoqueAtual(produtoDTO.getEstoqueAtual());
        produto.setEstoqueMinimo(produtoDTO.getEstoqueMinimo());
        produto.setEstoqueMaximo(produtoDTO.getEstoqueMaximo());
        produto.setDataCadastro(LocalDateTime.now());
        produto.setAtivo(true);

        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = fileStorageService.salvarImagem(imagem);
            produto.setNomeImagem(nomeArquivo);
        }

        return produtoRepository.save(produto);
    }

    public Produto atualizarProduto(Long id, ProdutoDTO produtoDTO, MultipartFile imagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Atualizar campos
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setTipo(produtoDTO.getTipo());
        produto.setGrupo(produtoDTO.getGrupo());
        produto.setCodigoBarras(produtoDTO.getCodigoBarras());
        produto.setUnidadeMedida(produtoDTO.getUnidadeMedida());
        produto.setPrecoCusto(produtoDTO.getPrecoCusto());
        produto.setPrecoVenda(produtoDTO.getPrecoVenda());
        produto.setEstoqueAtual(produtoDTO.getEstoqueAtual());
        produto.setEstoqueMinimo(produtoDTO.getEstoqueMinimo());
        produto.setEstoqueMaximo(produtoDTO.getEstoqueMaximo());

        /*if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = fileStorageService.salvarImagem(imagem);
            produto.setNomeImagem(nomeArquivo);
        }*/


        return produtoRepository.save(produto);
    }

    public byte[] obterImagemProduto(Long id) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getNomeImagem() != null) {
            return fileStorageService.carregarImagem(produto.getNomeImagem());
        }
        return null;
    }


    public void deletarProduto(Long id) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Deletar imagem do sistema de arquivos
        if (produto.getNomeImagem() != null) {
            fileStorageService.deletarImagem(produto.getNomeImagem());
        }

        // Deletar produto do banco
        produtoRepository.delete(produto);
    }
}