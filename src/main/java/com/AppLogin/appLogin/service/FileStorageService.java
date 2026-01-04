package com.AppLogin.appLogin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String salvarImagem(MultipartFile imagem) throws IOException {
        if (imagem == null || imagem.isEmpty()) {
            return null;
        }

        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único para o arquivo
        String nomeOriginal = imagem.getOriginalFilename();
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }

        String nomeArquivo = UUID.randomUUID().toString() + extensao;
        Path caminhoArquivo = uploadPath.resolve(nomeArquivo);

        // Salvar arquivo
        Files.copy(imagem.getInputStream(), caminhoArquivo);

        return nomeArquivo;
    }

    public byte[] carregarImagem(String nomeArquivo) throws IOException {
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            return null;
        }

        Path caminhoArquivo = Paths.get(uploadDir).resolve(nomeArquivo);
        if (Files.exists(caminhoArquivo)) {
            return Files.readAllBytes(caminhoArquivo);
        }
        return null;
    }

    public void deletarImagem(String nomeArquivo) throws IOException {
        if (nomeArquivo != null && !nomeArquivo.isEmpty()) {
            Path caminhoArquivo = Paths.get(uploadDir).resolve(nomeArquivo);
            if (Files.exists(caminhoArquivo)) {
                Files.delete(caminhoArquivo);
            }
        }
    }
}