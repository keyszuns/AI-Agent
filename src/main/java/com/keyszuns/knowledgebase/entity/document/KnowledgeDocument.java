package com.keyszuns.knowledgebase.entity.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "knowledge_documents")
public class KnowledgeDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Keyword)
    private String fileName;

    @Field(type = FieldType.Date)
    private Date uploadDate;
    
    @Field(type = FieldType.Long)
    private long fileSize;

}