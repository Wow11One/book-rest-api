package com.profitsoft.restapi.service.impl;

import com.cloudinary.Cloudinary;
import com.profitsoft.restapi.service.ImageService;
import jakarta.annotation.Resource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CloudinaryImageServiceImpl implements ImageService {

    @Resource
    Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            Map<String, String> options = new HashMap<>();
            options.put("folder", "books");
            Map uploadedImage = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedImage.get("public_id");

            return cloudinary.url().secure(true).generate(publicId);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}
