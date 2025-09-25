package com.keyszuns.knowledgebase.controller;

import com.keyszuns.knowledgebase.dto.DocumentInfoDTO;
import com.keyszuns.knowledgebase.entity.document.KnowledgeDocument;
import com.keyszuns.knowledgebase.repository.KnowledgeRepository;
import java.util.Map;
import com.keyszuns.knowledgebase.service.FileParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final FileParserService fileParserService;
    private final KnowledgeRepository knowledgeRepository;

    public DocumentController(FileParserService fileParserService, KnowledgeRepository knowledgeRepository) {
        this.fileParserService = fileParserService;
        this.knowledgeRepository = knowledgeRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            KnowledgeDocument document = KnowledgeDocument.builder()
                    .fileName(file.getOriginalFilename())
                    .content(fileParserService.parseFile(file))
                    .uploadDate(new Date())
                    .fileSize(file.getSize())
                    .build();
            knowledgeRepository.save(document);
            System.out.println("文档上传成功");
            return ResponseEntity.ok("文档上传成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文档上传失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<DocumentInfoDTO>> listDocuments() {
        try {
            Iterable<KnowledgeDocument> documents = knowledgeRepository.findAll();
            List<DocumentInfoDTO> documentInfos = new ArrayList<>();
            documents.forEach(doc -> {
                DocumentInfoDTO infoDTO = DocumentInfoDTO.builder()
                        .fileName(doc.getFileName())
                        .uploadDate(doc.getUploadDate())
                        .fileSize(doc.getFileSize())
                        .build();
                documentInfos.add(infoDTO);
            });
            return ResponseEntity.ok(documentInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(null);
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDocument(@RequestBody Map<String, String> requestBody) {
        try {
            String fileName = requestBody.get("name");
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("文件名不能为空");
            }
            
            Iterable<KnowledgeDocument> documents = knowledgeRepository.findByFileName(fileName);
            boolean deleted = false;
            for (KnowledgeDocument doc : documents) {
                knowledgeRepository.delete(doc);
                deleted = true;
            }
            
            if (deleted) {
                System.out.println("文档删除成功: " + fileName);
                return ResponseEntity.ok("文档删除成功: " + fileName);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("未找到指定文档: " + fileName);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文档删除失败: " + e.getMessage());
        }
    }
}