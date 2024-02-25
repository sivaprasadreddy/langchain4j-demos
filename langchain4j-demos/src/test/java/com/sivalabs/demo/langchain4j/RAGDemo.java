package com.sivalabs.demo.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.testcontainers.chromadb.ChromaDBContainer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.internal.Utils.randomUUID;

public class RAGDemo {

    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.withApiKey("demo");
        //EmbeddingModel embeddingModel = OpenAiEmbeddingModel.withApiKey("demo");
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore;

        embeddingStore = new InMemoryEmbeddingStore<>();
        //embeddingStore = chromaEmbeddingStore();

        DocumentSplitter splitter = DocumentSplitters.recursive(600, 0);

        Document document = loadDocument(toPath("/siva.txt"), new TextDocumentParser());

        /*
        URL url = new URL("https://www.sivalabs.in/about-me/");
        Document htmlDocument = UrlDocumentLoader.load(url, new TextDocumentParser());
        HtmlTextExtractor transformer = new HtmlTextExtractor(null, null, true);
        Document document = transformer.transform(htmlDocument);
        */

        System.out.println("Document loaded successfully");

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        Embedding queryEmbedding = embeddingModel.embed("Tell me about Siva?").content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.getFirst();

        String information = embeddingMatch.embedded().text();
        System.out.println("Relevant Information:\n"+information);

        Prompt prompt
                = PromptTemplate.from("""
                        Tell me about {{name}}?
                        
                        Use the information to answer the question:
                        {{information}}
                        """)
                .apply(Map.of("name", "Siva", "information", information));
        String answer = model.generate(prompt.toUserMessage()).content().text();
        System.out.println("Answer:\n"+answer);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                //.minScore(0.5)
                .build();

        PersonDataExtractor extractor =
                AiServices.builder(PersonDataExtractor.class)
                        .chatLanguageModel(model)
                        .contentRetriever(contentRetriever)
                        .build();
        Person person = extractor.getInfoAbout("Siva");
        System.out.println("Person:\n"+person);
    }

    private static EmbeddingStore<TextSegment> chromaEmbeddingStore() {
        var chromadb = new ChromaDBContainer("chromadb/chroma:0.4.22");
        chromadb.start();
        return ChromaEmbeddingStore.builder()
                .baseUrl(chromadb.getEndpoint())
                .collectionName(randomUUID())
                .build();
    }

    interface PersonDataExtractor {
        @UserMessage("Get information about {{it}} as of {{current_date}}")
        Person getInfoAbout(String name);
    }

    record Person(String name,
                  LocalDate dateOfBirth,
                  int experienceInYears,
                  List<String> books) {
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = RAGDemo.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
