package com.tpe.service;

import com.tpe.domain.ImageFile;
import com.tpe.repository.ImageFileRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageFileRepository imageFileRepository;

    public Optional<ImageFile> getImageById(Long id) {
        return imageFileRepository.findById(id);
    }

    public String encodeImageToBase64(String imageUrl) throws IOException {
        InputStream in = getClass().getResourceAsStream(imageUrl);
        byte[] imageBytes = IOUtils.toByteArray(in);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
