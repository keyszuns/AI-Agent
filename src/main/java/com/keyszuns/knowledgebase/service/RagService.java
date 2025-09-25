package com.keyszuns.knowledgebase.service;

import com.keyszuns.knowledgebase.entity.document.KnowledgeDocument;
import com.keyszuns.knowledgebase.repository.KnowledgeRepository;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.Collections;

@Service
public class RagService {

    private final KnowledgeRepository knowledgeRepository;
    private final OpenAiChatModel chatModel;

    public RagService(KnowledgeRepository knowledgeRepository, OpenAiChatModel chatModel) {
        this.knowledgeRepository = knowledgeRepository;
        this.chatModel = chatModel;
    }

    public String generateAnswer(String question) {
        // 清理问题字符串中的特殊字符
        String cleanedQuestion = question.replaceAll("[\\r\\n\\t]", " ").trim();
        System.out.println("-------------------------" + cleanedQuestion + "-------------------------");

        // 1. 从Elasticsearch检索相关文档
        Page<KnowledgeDocument> results = knowledgeRepository.findByContent(cleanedQuestion, PageRequest.of(0, 3));
        System.out.println("-------------------------" + results + "-------------------------");

        // 2. 构建上下文
        StringBuilder contextBuilder = new StringBuilder();
        results.forEach(doc -> contextBuilder.append(doc.getContent()).append("\n\n"));
        String context = contextBuilder.toString();

        // 3. 构建提示词
        String prompt = String.format("""
        基于以下上下文信息回答问题,
        在知识库里面没有的知识请不要随意回答，就说不知道：
        %s
        问题：%s
        回答：
        """, context, cleanedQuestion);

        // 4. 调用DeepSeek模型生成答案
        return chatModel.call(prompt);
    }
    
    // 流式生成答案的方法
    public Flux<String> generateAnswerStream(String question) {
        // 清理问题字符串中的特殊字符
        String cleanedQuestion = question.replaceAll("[\\r\\n\\t]", " ").trim();
        System.out.println("-------------------------流式生成答案: " + cleanedQuestion + "-------------------------");

        // 1. 从Elasticsearch检索相关文档
        Page<KnowledgeDocument> results = knowledgeRepository.findByContent(cleanedQuestion, PageRequest.of(0, 3));
        System.out.println("-------------------------检索结果: " + results + "-------------------------");

        // 2. 构建上下文
        StringBuilder contextBuilder = new StringBuilder();
        results.forEach(doc -> contextBuilder.append(doc.getContent()).append("\n\n"));
        String context = contextBuilder.toString();

        // 3. 构建提示词
        String prompt = String.format("""
        基于以下上下文信息回答问题,
        在知识库里面没有的知识请不要随意回答，就说不知道：
        %s
        问题：%s
        回答：
        """, context, cleanedQuestion);
        
        // 创建UserMessage
        UserMessage userMessage = new UserMessage(prompt);
        Prompt promptObj = new Prompt(Collections.singletonList(userMessage));
        
        // 4. 调用DeepSeek模型流式生成答案
        return chatModel.stream(promptObj)
                .map(chatResponse -> {
                    return chatResponse.getResult().getOutput().toString();
                })
                .cast(String.class) // 确保类型正确
                .doOnComplete(() -> System.out.println("流式生成答案完成"));
    }
}