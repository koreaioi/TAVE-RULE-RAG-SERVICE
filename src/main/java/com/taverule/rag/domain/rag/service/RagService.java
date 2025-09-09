package com.taverule.rag.domain.rag.service;

import com.taverule.rag.domain.rag.prompt.Prompts;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    public String getAnswer(String question) {
        // 유사도 높은 문서 검색
        String documents = findSimilarData(question);

        PromptTemplate template = new PromptTemplate(Prompts.RAG_PROMPT);
        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", documents);

        return chatModel
                .call(template.create(promptsParameters))
                .getResult()
                .getOutput()
                .getText();
    }

    private String findSimilarData(String question) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(2)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        return documents
                .stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}