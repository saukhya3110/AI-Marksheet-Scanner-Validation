
package com.marksheet;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/marksheets")
public class MarksheetController {

    @PostMapping(
        value = "/scan",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> scan(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new LinkedHashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("error", "Please upload a marksheet.");
            return ResponseEntity.badRequest().body(response);
        }

        String filename = file.getOriginalFilename() == null
                ? "" : file.getOriginalFilename().toLowerCase();

        boolean supported = filename.endsWith(".png")
                || filename.endsWith(".jpg")
                || filename.endsWith(".jpeg")
                || filename.endsWith(".tif")
                || filename.endsWith(".tiff");

        if (!supported) {
            response.put("error",
                    "Please upload a PNG, JPG, or TIFF image. PDF support is not enabled yet.");
            return ResponseEntity.badRequest().body(response);
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            response.put("error", "File size must not exceed 10 MB.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                response.put("error",
                        "The uploaded file is not a valid supported image.");
                return ResponseEntity.badRequest().body(response);
            }

            Tesseract ocr = new Tesseract();
            ocr.setDatapath("tessdata");
            ocr.setLanguage("eng");

            String extractedText = ocr.doOCR(image);

            response.put("filename", file.getOriginalFilename());
            response.put("extractedText", extractedText);
            response.put("message",
                    "Text extracted successfully. Please verify it against the original marksheet.");

            return ResponseEntity.ok(response);

        } catch (TesseractException | IOException
                 | IllegalArgumentException e) {
            response.put("error",
                    "Scanning failed. Please check the image and OCR setup.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(response);
        }
    }
}
