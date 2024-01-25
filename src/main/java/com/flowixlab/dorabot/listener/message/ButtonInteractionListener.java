package com.flowixlab.dorabot.listener.message;

import com.flowixlab.dorabot.button.Button;
import com.flowixlab.dorabot.listener.EventListener;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ButtonInteractionListener implements EventListener<ButtonInteractionEvent> {

    @Autowired
    @Qualifier("buttonMap")
    private Map<String, Button> buttons;

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        LOG.info("button-id: " + event.getCustomId());
        if (buttons.containsKey(event.getCustomId())) {
            return buttons.get(event.getCustomId()).handle(event);
        }
        return Mono.empty();
    }
}
