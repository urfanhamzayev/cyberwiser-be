package com.phoenix_sat.phoenix_sat_backend.service.impl;


import com.phoenix_sat.phoenix_sat_backend.service.FileService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
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
        log.info("File saved successfully in {} resource path",RESOURCE_PATH);
    }

    @Override
    public File getFile(String fileName) {
        return new File(RESOURCE_PATH, fileName);
    }

    @SneakyThrows
    @Override
    public byte[] getFileAsBytes(String filename) {
        log.info("Converting file into byte array starting... filename : {}",filename);
        File file = new File(filename);

        if (!file.exists()) {
            log.warn("File not found with this filename: {}",filename);
            throw new IOException("File not found: " + filename);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            log.info("File converted byte array successfully filename : {}",filename);
            return fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading file: " + filename, e);
        }

    }
}
