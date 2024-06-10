package com.sivalabs.demo.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class OllamaChatDemo {

    public static void main(String[] args) {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .build();
        String answer = model.generate("List all the movies directed by Quentin Tarantino");
        System.out.println(answer);
    }
}

