package com.flowixlab.dorabot.commands;

import com.flowixlab.dorabot.data.MenuData;
import com.flowixlab.dorabot.utils.ResourceData;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoleCommand implements SlashCommand{

    @Autowired
    private List<MenuData> menusData;
    @Autowired
    private ResourceData resourceData;

    @Override
    public String name() {
        return "role";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var embed = EmbedCreateSpec.builder()
                .color(Color.ORANGE)
                .title(resourceData.message("role-title"))
                .description(resourceData.message("role-description"))
                .thumbnail(resourceData.message("role-thumbnail"))
                .build();

        Map<String, EmbedCreateSpec> specMap = new HashMap<>();
        var selectMenus = menusData.stream()
                .peek(menuData -> LOG.info(menuData.placeholder()))
                .map(menuData -> {
                    AtomicInteger aI = new AtomicInteger();
                    var options = Arrays.stream(menuData.roles())
                            .peek(roleData -> LOG.info(roleData.name()))
                            .map(roleData -> {
                                String name = roleData.name();
                                if (menuData.embed()) {
                                    name = aI.incrementAndGet() + ") " + name;
                                }
                                var option = SelectMenu.Option.of(name, String.valueOf(roleData.id()));
                                if (roleData.emoji().isEmpty()) {
                                    return option;
                                }
                                return option.withEmoji(ReactionEmoji.unicode(roleData.emoji()));
                            })
                            .toList();
                    if (menuData.embed()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < menuData.roles().length; i++) {
                            sb.append(i + 1).append(") <@&").append(menuData.roles()[i].id()).append(">\n");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        specMap.put(String.valueOf(menuData.id()), EmbedCreateSpec.builder()
                                .title(menuData.placeholder())
                                .description(sb.toString())
                                .build());
                    }
                    return SelectMenu.of(String.valueOf(menuData.id()), options)
                            .withMinValues(0)
                            .withMaxValues(1)
                            .withPlaceholder(menuData.placeholder());
                }).toList();

        return event.deferReply()
                .then(event.createFollowup(InteractionFollowupCreateSpec.builder()
                                .addEmbed(embed).build()))
                .flatMapMany(v -> Flux.fromIterable(selectMenus))
                .flatMap(select -> {
                    var createSpec = InteractionFollowupCreateSpec.builder()
                            .components(List.of(ActionRow.of(select)));
                    if (specMap.containsKey(select.getCustomId())) {
                        createSpec.addEmbed(specMap.get(select.getCustomId()));
                    }
                    return event.createFollowup(createSpec.build());
                })
                .then();
    }
}
