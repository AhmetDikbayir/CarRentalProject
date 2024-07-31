package com.tpe.service;

import com.tpe.domain.ImageFile;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.ImageFileRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class ImageService {

    @Autowired
    private ImageFileRepository imageFileRepository;

    private final Path root = Paths.get("uploads");

    public ImageFile getImageById(Long id) {
        return imageFileRepository.findById(id).orElseThrow
                (()-> new ResourceNotFoundException(ErrorMessages.IMAGE_NOT_FOUND));
    }

    public String encodeImageToBase64(String imageUrl) throws IOException {
        InputStream in = getClass().getResourceAsStream(imageUrl);
        byte[] imageBytes = IOUtils.toByteArray(in);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public void saveImage (ImageFile imageFile){
        imageFileRepository.save(imageFile);
    }

    public String storeImage(MultipartFile file) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        String filename = file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(filename));
        return this.root.resolve(filename).toString();
    }
}
