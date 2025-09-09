package com.taverule.rag.loader;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PDFLoader {

    private  final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    @Value("classpath:/tave-rule.pdf")
    private Resource pdfResource;

    @PostConstruct
    public void init(){
        Integer count = jdbcClient.sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();


        if (count == 0) {
            // PDF Reader
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPageTopMargin(0)
                    .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                            .withNumberOfTopTextLinesToDelete(0)
                            .build())
                    .withPagesPerDocument(1)
                    .build();

            // 문서 로드
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource, config);
            List<Document> documents = pdfReader.get();

            // 문서 분할
            TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
            List<Document> splitDocuments = splitter.apply(documents);

            // 배치로 나누어 임베딩 및 저장
            int batchSize = 10;

            for (int i = 0; i < splitDocuments.size(); i += batchSize) {
                int end = Math.min(i + batchSize, splitDocuments.size());
                List<Document> batch = splitDocuments.subList(i, end);

                // OpenAi 임베딩
                vectorStore.accept(batch);

                // 분당 토큰수 제한으로 1초 간격으로 전송
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}