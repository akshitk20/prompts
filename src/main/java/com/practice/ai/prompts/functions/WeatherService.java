package com.practice.ai.prompts.functions;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Service
public class WeatherService implements Function<WeatherService.Request, WeatherService.Response> {
    private final WeatherConfigProperties weatherConfigProperties;
    private final RestClient restClient;

    public WeatherService(WeatherConfigProperties weatherConfigProperties) {
        this.weatherConfigProperties = weatherConfigProperties;
        this.restClient = RestClient.create(weatherConfigProperties.apiUrl());
    }

    @Override
    public Response apply(Request request) {
        System.out.println("Weather Request " + request);
        Response response = restClient.get()
                .uri("/current.json?key={key}&q={q}", weatherConfigProperties.apiKey(), request.city())
                .retrieve()
                .body(Response.class);
        System.out.println("Weather Response" + response);
        return response;
    }


    public record Request(String city) {}
    public record Response(Location location,Current current) {}
    public record Location(String name, String region, String country, Long lat, Long lon){}
    public record Current(String temp_f, Condition condition, String wind_mph, String humidity) {}
    public record Condition(String text){}
}
