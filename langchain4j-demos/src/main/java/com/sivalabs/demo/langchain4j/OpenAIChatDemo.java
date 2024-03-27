package com.sivalabs.demo.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAIChatDemo {
    
    public static void main(String[] args) {
        //String openAiKey = System.getenv("OPENAI_API_KEY");
        String openAiKey = "demo";
        ChatLanguageModel model = OpenAiChatModel.withApiKey(openAiKey);

        String answer = model.generate("List all the movies directed by Quentin Tarantino");
        System.out.println(answer);
    }
}