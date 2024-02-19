package com.sivalabs.demo;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class LangChain4jOpenAIDemo {
    
    public static void main(String[] args) {
        String openAiKey = "demo";
        //String openAiKey = System.getenv("OPENAI_API_KEY");
        ChatLanguageModel model = OpenAiChatModel.withApiKey(openAiKey);
        String answer = model.generate("List all the movies directed by Quentin Tarantino");
        System.out.println(answer);
    }
}