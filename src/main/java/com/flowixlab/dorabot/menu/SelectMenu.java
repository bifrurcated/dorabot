package com.flowixlab.dorabot.menu;

import com.flowixlab.dorabot.data.IName;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface SelectMenu extends IName {

    Logger LOG = LoggerFactory.getLogger(SelectMenu.class);

    Mono<Void> handle(SelectMenuInteractionEvent event);
}
