package com.flowixlab.dorabot.configuration;

import com.flowixlab.dorabot.listener.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BotConfig {
    @Value("${dorabot-token}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient discordClient(List<EventListener<T>> eventListeners) {
        return DiscordClientBuilder.create(token)
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
                .login()
                .doOnNext(client -> {
                    for (var listener : eventListeners) {
                        client.on(listener.getEventType())
                                .flatMap(listener::execute)
                                .onErrorResume(listener::handleError)
                                .subscribe();
                    }
                })
                .block();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create(token);
    }

}
