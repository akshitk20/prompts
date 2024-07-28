package com.practice.ai.prompts.functions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityController {

    private final ChatClient chatClient;


    public CityController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
    }

    @GetMapping("/city")
    public String cities(@RequestParam(value = "message") String message) {
        SystemMessage systemMessage = new SystemMessage("You are a helpful AI assistant answering questions about cities");
        UserMessage userMessage = new UserMessage(message);
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .withFunction("currentWeatherFunction")
                .build();
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
