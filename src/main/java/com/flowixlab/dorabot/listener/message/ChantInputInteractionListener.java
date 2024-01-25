package com.flowixlab.dorabot.listener.message;

import com.flowixlab.dorabot.commands.SlashCommand;
import com.flowixlab.dorabot.listener.EventListener;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ChantInputInteractionListener implements EventListener<ChatInputInteractionEvent> {

    @Autowired
    @Qualifier("slashCommandMap")
    Map<String, SlashCommand> commands;

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        var commandName = event.getCommandName();
        if (commands.containsKey(commandName)) {
            return commands.get(commandName).handle(event);
        }
        return Mono.empty();
    }
}
