package com.sivalabs.demo.langchain4j;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.LocalDate;
import java.util.Map;

public class OpenAIChatMemoryDemo {
    
    public static void main(String[] args) {
        String openAiKey = "demo";
        //String openAiKey = System.getenv("OPENAI_API_KEY");
        ChatLanguageModel model = OpenAiChatModel.withApiKey(openAiKey);

        //chatModelDoesntHaveMemory(model);
        chatWithMemory(model);

    }

    private static void chatWithMemory(ChatLanguageModel model) {

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(2);
        //ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO));

        ConversationalChain chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();
        ask(chain, "What are all the movies directed by Quentin Tarantino?");
        ask(chain, "How old is he?");
        // Answer: Quentin Tarantino was born on March 27, 1963, so as of 2021, he is 58 years old.

        ask(chain, "How old is he as of "+ LocalDate.now() + "?");

        Prompt prompt = PromptTemplate
                .from("How old is he as of {{current_date}}?")
                .apply(Map.of());
        ask(chain, prompt.text());
        //Question: How old is he as of 2024-02-20?
        //Answer: As of February 20, 2024, Quentin Tarantino would be 60 years old.
    }

    private static void chatModelDoesntHaveMemory(ChatLanguageModel model) {
        ask(model, "What are all the movies directed by Quentin Tarantino");
        ask(model, "How old is he?");
        // Answer: I'm sorry, without more context I am unable to determine who "he" is or his age. Can you please provide more information?
    }

    private static void ask(ConversationalChain chain, String question) {
        String answer = chain.execute(question);
        System.out.println("====================================");
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
        System.out.println("====================================");
    }

    private static void ask(ChatLanguageModel model, String question) {
        String answer = model.generate(question);
        System.out.println("====================================");
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
        System.out.println("====================================");
    }
}