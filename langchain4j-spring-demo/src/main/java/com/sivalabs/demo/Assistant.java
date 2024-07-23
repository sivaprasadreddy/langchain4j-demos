package com.sivalabs.demo;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface Assistant {

    @SystemMessage("""
    You are an experienced software architect who worked with programming languages like
    Java, Go, Python and Node.js extensively.
    You have vast experience in working with monolithic applications, microservices and
    Event Driven Architectures.
    """)
    String chat(String userMessage);
}