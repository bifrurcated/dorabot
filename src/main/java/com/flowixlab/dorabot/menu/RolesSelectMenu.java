package com.flowixlab.dorabot.menu;

import com.flowixlab.dorabot.utils.ResourceData;
import com.flowixlab.dorabot.data.RoleData;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.entity.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RolesSelectMenu {

    private final Logger LOG = LoggerFactory.getLogger(RolesSelectMenu.class);

    @Autowired
    private ResourceData resourceData;

    public Mono<Void> handle(SelectMenuInteractionEvent event, Map<String, RoleData> roleDataMap) {
        var userId = event.getInteraction().getUser().getId();
        if (event.getValues().isEmpty()) {
            return event.getInteraction().getGuild()
                    .flatMapMany(Guild::getMembers)
                    .filter(member -> member.getId().equals(userId))
                    .singleOrEmpty()
                    .flatMap(member -> {
                        for (var roleId : member.getRoleIds()){
                            if (roleDataMap.containsKey(roleId.asString())) {
                                return member.removeRole(roleId)
                                        .then(event.reply(resourceData.message("role-remove", roleId.asString()))
                                                .withEphemeral(true));
                            }
                        }
                        return Mono.empty();
                    });
        }
        if (roleDataMap.containsKey(event.getValues().getFirst())) {
            var role = roleDataMap.get(event.getValues().getFirst());
            return event.getInteraction().getGuild()
                    .flatMapMany(Guild::getMembers)
                    .filter(member -> member.getId().equals(userId))
                    .singleOrEmpty()
                    .flatMap(member -> {
                        var roleId = Snowflake.of(role.id());
                        for (var el : member.getRoleIds()){
                            if (roleDataMap.containsKey(el.asString())) {
                                return member.removeRole(el)
                                        .then(member.addRole(roleId));
                            }
                        }
                        return member.addRole(roleId);
                    })
                    .then(event.reply(resourceData.message("role-add", String.valueOf(role.id())))
                            .withEphemeral(true));
        }
        return event.reply(resourceData.message("option-else"))
                .withEphemeral(true);
    }
}
