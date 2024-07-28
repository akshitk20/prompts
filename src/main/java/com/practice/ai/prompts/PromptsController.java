package com.practice.ai.prompts;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptsController {

    private final ChatClient chatClient;

    public PromptsController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("")
    public String simple() {
        return chatClient.prompt
                (new Prompt("Tell me a bad joke"))
                .call()
                .content();
    }
}
