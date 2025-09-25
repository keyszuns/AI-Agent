package com.keyszuns.knowledgebase.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FileParserService {

    public String parseFile(MultipartFile file) throws IOException, TikaException, SAXException {
        String contentType = file.getContentType();
        String content;

        content = switch (contentType) {
            case "text/plain" -> new String(file.getBytes(), StandardCharsets.UTF_8);
            case "application/pdf" -> parsePdf(file.getInputStream());
            case "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                    parseDoc(file.getInputStream());
            default -> throw new UnsupportedOperationException("不支持的文件类型: " + contentType);
        };

        return content;
    }

    private String parsePdf(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    private String parseDoc(InputStream inputStream) throws IOException, TikaException, SAXException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        parser.parse(inputStream, handler, metadata);
        return handler.toString();
    }
}