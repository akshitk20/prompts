package com.practice.ai.prompts.gpt4o;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ImageModelController {

    private final ChatClient chatClient;

    public ImageModelController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
    }

    @GetMapping("/image-description")
    public String describeImage() throws IOException {
        byte[] imageData = new ClassPathResource("/images/sincerely-media-2UlZpdNzn2w-unsplash.jpg").getContentAsByteArray();
        UserMessage userMessage = new UserMessage("Can you please explain what you see in the following image", new Media(MimeTypeUtils.IMAGE_JPEG, imageData));
        return chatClient.prompt(new Prompt(userMessage))
                .call()
                .content();
    }
}
