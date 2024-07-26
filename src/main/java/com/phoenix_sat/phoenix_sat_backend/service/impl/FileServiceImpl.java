package com.phoenix_sat.phoenix_sat_backend.service.impl;


import com.phoenix_sat.phoenix_sat_backend.service.FileService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {
    public static final String RESOURCE_PATH = "C:\\Users\\HP\\OneDrive - The Academy of Public Administration under the President of the Republic of Azerbaijan\\Documents\\";

    @Override
    @SneakyThrows
    public void saveFile(byte[] bytes, String fileName) {
        File path = new File(RESOURCE_PATH + fileName);
        if (!path.createNewFile()) {
            return;
        }
        FileOutputStream output = new FileOutputStream(path);
        output.write(bytes);
        output.close();
    }

    @Override
    public File getFile(String fileName) {
        return new File(RESOURCE_PATH, fileName);
    }

    @SneakyThrows
    @Override
    public byte[] getFileAsBytes(String filename) {
        File file = new File(filename);

        if (!file.exists()) {
            throw new IOException("File not found: " + filename);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading file: " + filename, e);
        }
    }
}
