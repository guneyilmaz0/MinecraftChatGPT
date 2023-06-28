package net.swade.chatgpt;

import cn.nukkit.utils.ConfigSection;
import com.theokanning.openai.completion.CompletionRequest;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import retrofit2.HttpException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class OpenAI {

    private static OpenAiService service;

    public static CompletableFuture<Void> init(String key) {
        return CompletableFuture.runAsync(() -> service = new OpenAiService(key, Duration.ofSeconds(5)));
    }

    public static CompletableFuture<String> getResponse(ConfigSection section, StringBuilder cached, String message) {
        cached.append("\nHuman:").append(message).append("\nAI:");

        return CompletableFuture.supplyAsync(() -> {
            String model = section.getString("model", "text-davinci-003");
            int maxTokens = section.getInt("max-tokens");
            double frequencyPenalty = section.getDouble("frequency-penalty");
            double presencePenalty = section.getDouble("presence-penalty");
            double topP = section.getDouble("top-p");
            double temperature = section.getDouble("temperature");

            if (model.startsWith("gpt-4") || model.startsWith("gpt-3.5")) {
                return service.createChatCompletion(ChatCompletionRequest.builder()
                                .model(model)
                                .temperature(temperature)
                                .maxTokens(maxTokens)
                                .topP(topP)
                                .frequencyPenalty(frequencyPenalty)
                                .presencePenalty(presencePenalty)
                                .stop(Arrays.asList("Human:", "AI:"))
                                .build())
                        .getChoices().get(0).getMessage().getContent();
            }

            return service.createCompletion(CompletionRequest.builder()
                            .prompt(cached.toString())
                            .model(model)
                            .temperature(temperature)
                            .maxTokens(maxTokens)
                            .topP(topP)
                            .frequencyPenalty(frequencyPenalty)
                            .presencePenalty(presencePenalty)
                            .stop(Arrays.asList("Human:", "AI:"))
                            .build())
                    .getChoices().get(0).getText();

        }).exceptionally(throwable -> {
            if (throwable.getCause() instanceof HttpException) {
                HttpException e = (HttpException) throwable.getCause();
                String reason;
                switch (Objects.requireNonNull(e.response()).code()){
                    case 401:
                        reason = "Invalid API key! Please check your configuration.";
                        break;
                    case 429:
                        reason = "Too many requests! Please wait a few seconds and try again.";
                        break;
                    case 500:
                        reason = "OpenAI service is currently unavailable. Please try again later.";
                        break;
                    default:
                        reason = "Unknown error! Please try again later. If this error persists, contact the plugin developer.";
                        break;
                }
                throw new RuntimeException(reason, throwable);
            }
            throw new RuntimeException(throwable);
        });
    }
}