package com.flowixlab.dorabot.configuration;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GuildCommand implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestClient client;
    private final long guildId;
    private final String locationPattern;

    @Autowired
    public GuildCommand(RestClient restClient,
                        @Value("${application.guild-id}") long guildId,
                        @Value("${application.commands-path}") String locationPattern) {
        this.client = restClient;
        this.guildId = guildId;
        this.locationPattern = locationPattern;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var d4jMapper = JacksonResources.create();
        var matcher = new PathMatchingResourcePatternResolver();
        var applicationService = client.getApplicationService();
        var applicationId = client.getApplicationId().block();
        var discordCommands = applicationService.getGuildApplicationCommands(applicationId, guildId)
                .collectMap(ApplicationCommandData::name)
                .block();
        Map<String, ApplicationCommandRequest> commands = new HashMap<>();
        try {
            for (var resource : matcher.getResources(locationPattern)) {
                var request = d4jMapper.getObjectMapper()
                        .readValue(resource.getInputStream(), ApplicationCommandRequest.class);

                commands.put(request.name(), request);

                if (!discordCommands.containsKey(request.name())) {
                    applicationService.createGuildApplicationCommand(applicationId, guildId, request).block();
                    LOGGER.info("Created guild command: " + request.name());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var discordCommand : discordCommands.values()) {
            long commandId = discordCommand.id().asLong();

            var command = commands.get(discordCommand.name());
            if (command == null) {
                applicationService.deleteGuildApplicationCommand(applicationId, guildId, commandId).block();
                LOGGER.info("Deleted guild command: " + discordCommand.name());
                continue;
            }

            if (hasChanged(discordCommand, command)) {
                applicationService.modifyGuildApplicationCommand(applicationId, guildId, commandId, command).block();
                LOGGER.info("Updated guild command: " + discordCommand.name());
            }
        }
    }

    private boolean hasChanged(ApplicationCommandData discordCommand, ApplicationCommandRequest command) {
        // Compare types
        if (!discordCommand.type().toOptional().orElse(1)
                .equals(command.type().toOptional().orElse(1))) return true;

        //Check if description has changed.
        if (!discordCommand.description()
                .equals(command.description().toOptional().orElse(""))) return true;

        //Check and return if options have changed.
        return !discordCommand.options().equals(command.options());
    }
}
