package com.flowixlab.dorabot.commands;

import com.flowixlab.dorabot.data.IName;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface SlashCommand extends IName {

    Logger LOG = LoggerFactory.getLogger(SlashCommand.class);

    Mono<Void> handle(ChatInputInteractionEvent event);
}
