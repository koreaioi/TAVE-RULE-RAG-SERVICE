package com.taverule.rag.domain.rag.controller;

import com.taverule.rag.domain.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final RagService ragService;

    @GetMapping("/api/answer")
    public String simplify(String question) {

        String response = ragService.getAnswer(question);

        return response;
    }

}