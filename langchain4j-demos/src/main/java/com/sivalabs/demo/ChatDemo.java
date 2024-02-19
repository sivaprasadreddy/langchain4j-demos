package com.sivalabs.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChatDemo {
    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    public final static String MODEL = "gpt-3.5-turbo";
    public final static double TEMPERATURE = 0.7;

    static HttpClient client = HttpClient.newHttpClient();
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        ChatRequest chatRequest = new ChatRequest(
                MODEL, List.of(new Message("user", "List all the movies directed by Quentin Tarantino")),
                TEMPERATURE);
        String requestPayload = mapper.writeValueAsString(chatRequest);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_URL))
                .header("Authorization", "Bearer %s".formatted(OPENAI_API_KEY))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestPayload))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        String body = response.body();
        ChatResponse chatResponse = mapper.readValue(body, ChatResponse.class);
        System.out.println(chatResponse.choices().getFirst().message().content());
    }
}

record Message(String role, String content) {}

record ChatRequest( String model, List<Message> messages, double temperature) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record ChatUsage(
        @JsonProperty("prompt_tokens")
        int promptTokens,
        @JsonProperty("completion_tokens")
        int completionTokens,
        @JsonProperty("total_tokens")
        int totalTokens
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record ChatResponseChoice(
        int index,
        Message message,
        @JsonProperty("finish_reason")
        String finishReason
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record ChatResponse(
        String id, String object, long created,
        String model, ChatUsage usage,
        List<ChatResponseChoice> choices
) {}