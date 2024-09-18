package com.image.processors.controllers;

import com.image.processors.models.CustomColorRGB;
import com.image.processors.processors.ImageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RestController
@CrossOrigin("*")
public class ImageReciverController {

    @Autowired
    ImageProcessor processor;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/processors/{processorType}", method = RequestMethod.POST)
    public ResponseEntity<byte[]> upload(
            @RequestParam("image") MultipartFile firstFile,
            @RequestParam(value = "second-image", required = false) MultipartFile secondFile,
            @RequestParam(value = "rgb-remove", required = false) CustomColorRGB rgbRemoveColor,
            @PathVariable("processorType") String processorType) {

            String fileType = (processorType.equals("color-remove")
                                || processorType.equals("replace-background") || processorType.equals("normalizer"))
                    ? "png" : "jpg";

            ArrayList<MultipartFile> files = new ArrayList<>();

        try {
            Collections.addAll(files, firstFile, secondFile);
            BufferedImage processingHelperResult =  new ImageProcessorTypeHelper(files, rgbRemoveColor, processorType).processFiles();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(processingHelperResult, fileType, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // ou use MediaType.IMAGE_PNG se for PNG
            return ResponseEntity.ok().headers(headers).body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
