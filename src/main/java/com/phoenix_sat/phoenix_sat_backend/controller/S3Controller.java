package com.phoenix_sat.phoenix_sat_backend.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.phoenix_sat.phoenix_sat_backend.constant.AwsConstants;
import com.phoenix_sat.phoenix_sat_backend.service.impl.S3ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3ServiceImpl s3ServiceImpl;
    private final AwsConstants awsConstants;
    private final AmazonS3 amazonS3;


    @GetMapping
    public String health() {
        return "UP";
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        s3ServiceImpl.uploadFile(file.getOriginalFilename(), file);
        return "File uploaded";
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(s3ServiceImpl.getFile(fileName).getObjectContent()));
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName) {
        var s3Object = s3ServiceImpl.getFile(fileName);
        var content = s3Object.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // This content type can change by your file :)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(content));
    }

    @PostMapping(path = "/upload-to-s3", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadToS3(@RequestParam("file") MultipartFile file) {
        try {
            // Generate a unique file name or use the original one
            String fileName = file.getOriginalFilename();

            // Create a PutObjectRequest
            PutObjectRequest request = new PutObjectRequest(awsConstants.getBucketName(), fileName, file.getInputStream(), null);

            // Upload the file
            amazonS3.putObject(request);

            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file";
        }
    }

    @GetMapping("/video")
    public ResponseEntity<Resource> getVideo(@RequestParam("video name") String videoName) {
        var content = s3ServiceImpl.getFile(videoName).getObjectContent();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .body(new InputStreamResource(content));
    }
}