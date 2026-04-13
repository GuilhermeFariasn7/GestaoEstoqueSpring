package com.AppLogin.appLogin.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFoto(MultipartFile foto, String pasta, String nomeArquivo) throws IOException {
        Map resultado = cloudinary.uploader().upload(foto.getBytes(),
                ObjectUtils.asMap(
                        "folder", pasta,
                        "public_id", nomeArquivo,
                        "overwrite", true,
                        "resource_type", "image"
                ));

        return resultado.get("secure_url").toString();
    }

    public void deletarFoto(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}