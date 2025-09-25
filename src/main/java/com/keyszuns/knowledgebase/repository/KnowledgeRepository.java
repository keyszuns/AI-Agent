package com.keyszuns.knowledgebase.repository;

import com.keyszuns.knowledgebase.entity.document.KnowledgeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface KnowledgeRepository extends ElasticsearchRepository<KnowledgeDocument, String> {
/**
 * Custom query method to search for KnowledgeDocuments by content using Elasticsearch match query
 *
 * @param content The search term to match against document content
 * @param pageable Pagination information (page size, page number, sorting)
 * @return Page of KnowledgeDocuments that match the search criteria
 */
    @Query("{\"match\": {\"content\": {\"query\": \"?0\"}}}")
    Page<KnowledgeDocument> findByContent(String content, Pageable pageable);
    
    /**
     * Find KnowledgeDocuments by file name
     * 
     * @param fileName The file name to search for
     * @return Iterable of KnowledgeDocuments with matching file name
     */
    Iterable<KnowledgeDocument> findByFileName(String fileName);
}
