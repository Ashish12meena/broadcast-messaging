package com.aigreentick.services.common.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aigreentick.services.common.service.impl.ImageUploadServiceImpl;


@RestController
@RequestMapping("/api/image")
public class ImageUploadController {

  @Autowired
  ImageUploadServiceImpl imageUploadService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
      return imageUploadService.uploadImage(file);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
    }

  }

  @GetMapping("/{filename:.+}")
  public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
    try {
      return imageUploadService.serveImage(filename);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(null);
    }
  }

}
