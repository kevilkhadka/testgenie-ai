package com.testgenie.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * OcrService
 *
 * Extracts text from uploaded images using Tesseract OCR.
 * When a user uploads a Jira ticket screenshot, this service
 * reads all the text out of it — ticket title, description,
 * acceptance criteria, labels, everything.
 *
 * 💡 LEARNING NOTE:
 *   OCR = Optical Character Recognition
 *   Tesseract is Google's open source OCR engine.
 *   Tess4j is the Java wrapper around it.
 */
@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService(
            @Value("${tesseract.data.path}") String dataPath,
            @Value("${tesseract.language:eng}") String language) {

        tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage(language);

        // Page segmentation mode 1 = automatic page segmentation with OSD
        // Works best for screenshots with mixed text layouts like Jira
        tesseract.setPageSegMode(1);

        // OCR engine mode 3 = default, based on what is available
        tesseract.setOcrEngineMode(3);
    }

    /**
     * Extracts all text from an uploaded image file.
     *
     * @param file - the uploaded screenshot (PNG, JPG, etc.)
     * @return extracted text as a plain string
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new RuntimeException("Could not read image — unsupported format");
            }

            String extractedText = tesseract.doOCR(image);
            return cleanText(extractedText);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded image: " + e.getMessage(), e);
        } catch (TesseractException e) {
            throw new RuntimeException("OCR processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans up the raw OCR output.
     * Tesseract sometimes adds extra whitespace or weird characters.
     */
    private String cleanText(String raw) {
        if (raw == null) return "";

        return raw
            .replaceAll("\\r\\n", "\n")       // normalize line endings
            .replaceAll("\\n{3,}", "\n\n")     // collapse 3+ blank lines into 2
            .replaceAll("[^\\x20-\\x7E\\n]", "") // remove non-printable characters
            .trim();
    }
}
