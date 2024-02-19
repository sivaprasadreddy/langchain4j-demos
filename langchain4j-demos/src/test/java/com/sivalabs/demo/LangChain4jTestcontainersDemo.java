package com.sivalabs.demo;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.testcontainers.containers.GenericContainer;

public class LangChain4jTestcontainersDemo {
    static String MODEL_NAME = "llama2";
  
    public static void main(String[] args) {
      GenericContainer<?> ollama = new GenericContainer<>("langchain4j/ollama-" + MODEL_NAME + ":latest")
              .withExposedPorts(11434);
      ollama.start();
      
      String baseUrl = String.format("http://%s:%d", ollama.getHost(), ollama.getFirstMappedPort());
      ChatLanguageModel model = OllamaChatModel.builder()
              .baseUrl(baseUrl)
              .modelName(MODEL_NAME)
              .build();
      String answer = model.generate("List all the movies directed by Quentin Tarantino");
      System.out.println(answer);

      ollama.stop();
    }
}