package com.flowixlab.dorabot.button;

import com.flowixlab.dorabot.data.IName;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface Button extends IName {
    Logger LOG = LoggerFactory.getLogger(Button.class);
    Mono<Void> handle(ButtonInteractionEvent event);
}
