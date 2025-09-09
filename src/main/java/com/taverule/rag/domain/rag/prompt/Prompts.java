package com.taverule.rag.domain.rag.prompt;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Prompts {

    public static final String RAG_PROMPT = """
            You are an assistant for question-answering tasks.
            Use the following pieces of retrieved context to answer the question.
            If you don't know the answer, just say that you don.t know.
            Answer in Korean.
            
            #Question:
            {input}
            
            #Context :
            {documents}
            
            #Answer:
            """;
}