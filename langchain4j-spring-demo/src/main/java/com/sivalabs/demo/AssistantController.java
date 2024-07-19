package com.sivalabs.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AssistantController {

    Assistant assistant;

    AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message",
            defaultValue = "What is the time now?") String message) {
        return assistant.chat(message);
    }
}