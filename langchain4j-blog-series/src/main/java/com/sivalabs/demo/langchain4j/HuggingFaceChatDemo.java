package com.sivalabs.demo.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;

import static java.time.Duration.ofSeconds;

public class HuggingFaceChatDemo {

    public static void main(String[] args) {
        String hfApiKey = System.getenv("HF_API_KEY");
        ChatLanguageModel model = HuggingFaceChatModel.builder()
                .accessToken(hfApiKey)
                .modelId("NousResearch/Nous-Hermes-2-Mixtral-8x7B-DPO")
                .timeout(ofSeconds(120))
                .temperature(1.0)
                .maxNewTokens(200)
                .waitForModel(true)
                .build();
        String answer = model.generate("List all the movies directed by Quentin Tarantino");
        System.out.println(answer);
    }
}

