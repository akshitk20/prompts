package com.practice.ai.prompts.faq.configuration;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfiguration {

    @Value("classpath:/docs/olympic-faq.txt")
    private Resource resource;

    @Value("{vectorstore.json}")
    private String vectorStoreName;
    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embedding) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embedding);

        File vectorStoreFile = getVectorStoreFile();
        if (vectorStoreFile.exists()) {
            System.out.println("Vector store file exists");
            simpleVectorStore.load(vectorStoreFile);
        } else {
            System.out.println("Vector store file does not exist");
            TextReader textReader = new TextReader(resource);
            textReader.getCustomMetadata().put("filename", "olympic-faq.txt");
            List<Document> documents = textReader.get();
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = tokenTextSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }

    private File getVectorStoreFile() {
        Path path = Paths.get("src/main/resources/data");
        String absolutePath = path.toFile().getAbsoluteFile() + "/" + vectorStoreName;
        return new File(absolutePath);
    }
}
