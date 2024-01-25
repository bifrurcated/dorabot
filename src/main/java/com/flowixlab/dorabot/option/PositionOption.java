package com.flowixlab.dorabot.option;

import com.flowixlab.dorabot.data.Option;
import com.flowixlab.dorabot.data.ResourceData;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.CategoryCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.function.Function3;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class PositionOption {

    private final Logger LOG = LoggerFactory.getLogger(PositionOption.class);

    @Autowired
    private ResourceData resourceData;

    private final Function<TextChannel, String> textAdder =
            (textChannel) ->  resourceData.message("link-to-channel") + " <#"+textChannel.getId().asString()+">";

    private final Function3<String, Snowflake, List<PermissionOverwrite>, TextChannelCreateSpec> specChannel =
            (channelName, categoryId, permissions) -> TextChannelCreateSpec.builder()
                    .name(channelName)
                    .permissionOverwrites(permissions)
                    .parentId(categoryId)
                    .build();

    public Mono<Void> handle(SelectMenuInteractionEvent event, Option option) {
        Optional<Snowflake> guildId = event.getInteraction().getGuildId();
        if (guildId.isEmpty()) {
            return Mono.empty();
        }
        var user = event.getInteraction().getUser();
        var username = user.getUsername();
        var channelName = option.name() + "-" + username;

        Button pingAdministratorBtn = Button.danger("end-test-button", resourceData.message("ping-administration-button"));
        var permissionsMono = event.getInteraction().getGuild()
                .flatMapMany(Guild::getRoles)
                .filter(role -> role.getPermissions().contains(Permission.MODERATE_MEMBERS)
                        || role.getPermissions().contains(Permission.ADMINISTRATOR))
                .map(role -> PermissionOverwrite.forRole(role.getId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.none()))
                .collectList()
                .doOnNext(permissionOverwrites -> permissionOverwrites.addAll(List.of(
                        PermissionOverwrite.forRole(guildId.get(), PermissionSet.none(), PermissionSet.of(Permission.VIEW_CHANNEL)),
                        PermissionOverwrite.forMember(user.getId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.none())
                )));
        return event.getClient().getGuildById(guildId.get())
                .flatMap(guild -> guild.getChannels()
                        .filter(channel -> channel.getType().equals(Channel.Type.GUILD_CATEGORY)
                                && channel.getName().equals(resourceData.message("category-name")))
                        .singleOrEmpty()
                        .switchIfEmpty(guild.createCategory(CategoryCreateSpec.builder()
                                        .position(0)
                                        .name(resourceData.message("category-name"))
                                        .build())
                                .flatMap(category -> guild.getChannels()
                                        .filter(channel -> channel.getName().equals(channelName))
                                        .singleOrEmpty()
                                        .switchIfEmpty(permissionsMono.flatMap(permissions ->
                                                        guild.createTextChannel(specChannel.apply(channelName, category.getId(), permissions))
                                                                .flatMap(textChannel -> event.reply(textAdder.apply(textChannel))
                                                                        .withEphemeral(true)
                                                                        .then(textChannel.createMessage(MessageCreateSpec.builder()
                                                                                .content(option.text())
                                                                                .addComponent(ActionRow.of(pingAdministratorBtn))
                                                                                .build())))
                                                                .then(Mono.empty()))
                                                )
                                        .cast(TextChannel.class)
                                        .flatMap(textChannel -> textChannel.edit().withParentIdOrNull(category.getId())
                                                .doOnSuccess(txtChannel -> event
                                                        .reply(textAdder.apply(textChannel))
                                                        .withEphemeral(true)))))
                        .flatMap(category -> guild.getChannels()
                                .filter(channel -> channel.getType().equals(Channel.Type.GUILD_TEXT)
                                        && channel.getName().equals(channelName))
                                .singleOrEmpty()
                                .switchIfEmpty(permissionsMono.flatMap(permissions ->
                                        guild.createTextChannel(specChannel.apply(channelName, category.getId(), permissions))
                                        .flatMap(textChannel -> event.reply(textAdder.apply(textChannel))
                                                .withEphemeral(true)
                                                .then(textChannel.createMessage(MessageCreateSpec.builder()
                                                        .content(option.text())
                                                        .addComponent(ActionRow.of(pingAdministratorBtn))
                                                        .build())))
                                        .then(Mono.empty())))
                                .cast(TextChannel.class)
                                .flatMap(textChannel -> event
                                        .reply(textAdder.apply(textChannel))
                                        .withEphemeral(true))));
    }
}
