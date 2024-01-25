package com.flowixlab.dorabot.button;

import com.flowixlab.dorabot.data.Option;
import com.flowixlab.dorabot.data.ResourceData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class RitualButton implements Button{

    @Autowired
    private Option[] options;
    @Autowired
    private ResourceData resourceData;

    @Override
    public String name() {
        return "ritual-button";
    }

    @Override
    public Mono<Void> handle(ButtonInteractionEvent event) {
        var embeds = EmbedCreateSpec.builder()
                .title(resourceData.message("ritual-title"))
                .description(resourceData.message("ritual-description"))
                .build();

        var selectOptions = Arrays.stream(options)
                .peek(option -> LOG.info(option.name()))
                .map(option -> SelectMenu.Option.of(option.name(), option.name().toLowerCase())
                        .withEmoji(ReactionEmoji.unicode(option.emoji()))
                        .withDescription(option.description()))
                .toList();
        SelectMenu selectMenu = SelectMenu.of("select-position", selectOptions)
                .withPlaceholder(resourceData.message("select-position-placeholder"));
        return event.reply()
                .withEmbeds(embeds)
                .withComponents(ActionRow.of(selectMenu))
                .withEphemeral(true);
    }
}
