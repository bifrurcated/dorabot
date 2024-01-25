package com.flowixlab.dorabot.commands;

import com.flowixlab.dorabot.data.ResourceData;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class RitualCommand implements SlashCommand{

    @Autowired
    private ResourceData resourceData;

    @Override
    public String name() {
        return "ritual";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Optional<Snowflake> guildId = event.getInteraction().getGuildId();
        if (guildId.isEmpty()) {
            return Mono.empty();
        }
        Button button = Button.success("ritual-button", resourceData.message("greeting-button-name"));
        return event.getClient().getGuildById(guildId.get())
                .flatMap(guild -> {
                    var embeds = EmbedCreateSpec.builder()
                            .title(resourceData.message("greeting-title"))
                            .description(resourceData.message("greeting-description"))
                            .footer(guild.getName(), guild.getIconUrl(Image.Format.JPEG).orElse(""))
                            .build();
                    return event.reply()
                            .withEmbeds(embeds)
                            .withComponents(ActionRow.of(button));
                });
    }
}
