package com.keyszuns.knowledgebase.controller;

import com.keyszuns.knowledgebase.service.RagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.springframework.http.codec.ServerSentEvent;
import java.time.Duration;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/qa")
public class QAController {

    private final RagService ragService;

    public QAController(RagService ragService) {
        this.ragService = ragService;
    }

//    @PostMapping("/ask")
//    public ResponseEntity<String> askQuestion(@RequestBody String question) {
//        try {
//            String answer = ragService.generateAnswer(question);
//            return ResponseEntity.ok(answer);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("回答问题失败: " + e.getMessage());
//        }
//    }
    
    // 流式输出的API端点
    @PostMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> askQuestionStream(@RequestBody String question) {
        try {
            return ragService.generateAnswerStream(question)
                    .map(content -> ServerSentEvent.<String>builder()
                            .data(content)
                            .build())
                    .concatWith(Flux.just(ServerSentEvent.<String>builder()
                            .event("done")
                            .data("")
                            .build())
                            .delayElements(Duration.ofMillis(100)))
                    .onErrorResume(error -> {
                        System.err.println("流式输出错误: " + error.getMessage());
                        return Flux.just(
                                ServerSentEvent.<String>builder()
                                        .event("error")
                                        .data("回答问题失败: " + error.getMessage())
                                        .build()
                        );
                    });
        } catch (Exception e) {
            System.err.println("流式API初始化错误: " + e.getMessage());
            return Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("error")
                            .data("回答问题失败: " + e.getMessage())
                            .build()
            );
        }
    }
}