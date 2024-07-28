package com.practice.ai.prompts;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/olympics")
public class OlympicController {
    private final ChatClient chatClient;
    @Value("classpath:/prompts/olympic-sports.st") // taking prompt from external resource
    private Resource resource;

    @Value("classpath:/docs/olympic-sports.st") // taking prompt from external resource
    private Resource docsToStuffResource;

    public OlympicController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
    }

    @GetMapping("/2024")
    public String get2024OlympicSport(@RequestParam(value = "message", defaultValue = "What sports are being included in the 2024 summer olympics? ") String message,
                                      @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffIt) {

        PromptTemplate promptTemplate = new PromptTemplate(resource);
        Map<String, Object> map = new HashMap<>();
        map.put("question", message);
        if (stuffIt) {
            map.put("context", docsToStuffResource);
        } else {
            map.put("context", "");
        }

        Prompt prompt = promptTemplate.create();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
