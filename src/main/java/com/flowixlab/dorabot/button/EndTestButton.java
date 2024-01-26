package com.flowixlab.dorabot.button;

import com.flowixlab.dorabot.utils.ResourceData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.rest.util.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EndTestButton implements Button{

    @Autowired
    private ResourceData resourceData;

    @Override
    public String name() {
        return "end-test-button";
    }

    @Override
    public Mono<Void> handle(ButtonInteractionEvent event) {
        return event.getInteraction().getGuild()
                .flatMapMany(Guild::getMembers)
                .filter(member -> !member.isBot())
                .flatMap(member -> member.getBasePermissions()
                        .filter(permissions -> permissions.contains(Permission.ADMINISTRATOR)
                                || permissions.contains(Permission.MODERATE_MEMBERS))
                        .map(permissions -> member))
                .doOnNext(member -> LOG.info("nickname: " + member.getDisplayName()))
                .collectList()
                .flatMap(members -> {
                    var admins = members.stream()
                            .map(member -> "<@" + member.getId().asString() + ">")
                            .toList();
                    String content = String.join(" ", admins);
                    return event.reply(resourceData.message("end-test-reply") + " " + content);
                });
    }
}
