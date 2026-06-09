package com.ecommerce.project.service.impl;

import com.ecommerce.project.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name of current / original file
        String originalFilename = file.getOriginalFilename();
        // Generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String filename = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf('.')));
        String filepath = path + File.separator + filename;
        // check if path exists and creates
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdirs();
        }
        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filepath));
        return filename;
    }
}
