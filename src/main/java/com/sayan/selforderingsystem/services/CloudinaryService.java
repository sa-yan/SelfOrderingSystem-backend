package com.sayan.selforderingsystem.services;

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
    public Map upload(MultipartFile file) throws IOException {
        Map data = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "menu_items",  // Save in a specific folder
                "resource_type", "image",
                "quality", "auto:low",   // Auto compression
                "format", "webp",        // Convert to WebP (better compression)
                "width", 500,            // Resize to max 500px width
                "height", 500,           // Resize to max 500px height
                "crop", "limit"          // Keep aspect ratio
        ));
        System.out.println(data);
        return data;
    }
}
