package com.flowixlab.dorabot.listener.message;

import com.flowixlab.dorabot.data.RoleData;
import com.flowixlab.dorabot.listener.EventListener;
import com.flowixlab.dorabot.menu.RolesSelectMenu;
import com.flowixlab.dorabot.menu.SelectMenu;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class SelectMenuInteractionListener implements EventListener<SelectMenuInteractionEvent> {

    @Autowired
    @Qualifier("selectMenuMap")
    private Map<String, SelectMenu> selectMenus;
    @Autowired
    private Map<String, Map<String, RoleData>> rolesMap;
    @Autowired
    private RolesSelectMenu rolesSelectMenu;

    @Override
    public Class<SelectMenuInteractionEvent> getEventType() {
        return SelectMenuInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        LOG.info("select-menu-id: "+event.getCustomId());
        if (selectMenus.containsKey(event.getCustomId())) {
            return selectMenus.get(event.getCustomId()).handle(event);
        }
        if (rolesMap.containsKey(event.getCustomId())) {
            return rolesSelectMenu.handle(event, rolesMap.get(event.getCustomId()));
        }

        return Mono.empty();
    }
}
