package com.phoenix_sat.phoenix_sat_backend.service;

import java.io.File;

public interface FileService {
    void saveFile(byte[] bytes, String fileName);
    File getFile(String fileName);
    byte[] getFileAsBytes(String filename);

}