package com.flowixlab.dorabot.menu;

import com.flowixlab.dorabot.data.Option;
import com.flowixlab.dorabot.data.ResourceData;
import com.flowixlab.dorabot.option.PositionOption;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PositionSelectMenu implements SelectMenu {

    @Autowired
    private PositionOption positionOption;
    @Autowired
    private Map<String, Option> positionOptions;
    @Autowired
    private ResourceData resourceData;

    @Override
    public String name() {
        return "select-position";
    }

    @Override
    public Mono<Void> handle(SelectMenuInteractionEvent event) {
        LOG.info(event.getValues().getFirst());
        if (positionOptions.containsKey(event.getValues().getFirst())) {
            return positionOption.handle(event, positionOptions.get(event.getValues().getFirst()));
        }
        return event.reply(resourceData.message("option-else"))
                .withEphemeral(true);
    }
}
