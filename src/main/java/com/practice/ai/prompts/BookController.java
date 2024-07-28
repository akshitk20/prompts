package com.practice.ai.prompts;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final ChatClient chatClient;

    public BookController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory())) // advisor -> intercepts the request and modifies it. this is used to preserve the state of request
                .build();
    }

    public record Author(String name, List<String> books) {}

    @GetMapping("/author")
    public Author getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen") String author) {
        String promptMessage = """
                Generate the list of books written by author {author}.\s
                {format}
                """;
        var outputParser = new BeanOutputConverter<>(Author.class);
        String format = outputParser.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author", author, "format", format));
        Prompt prompt = promptTemplate.create();

        return outputParser.convert(chatClient.prompt(prompt)
                .call()
                .content());
    }

    @GetMapping("/author/{author}")
    public Map<String, Object> byAuthor(@PathVariable(value = "author") String author) {
        String promptMessage = """
                Generate a list of links for author {author}. Include the authors name as key and any social network link
                as the object. {format} \s
                """;

        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        String format = mapOutputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author", author, "format", format));
        Prompt prompt = promptTemplate.create();
        return mapOutputConverter.convert(chatClient.prompt(prompt)
                .call()
                .content());
    }
}
