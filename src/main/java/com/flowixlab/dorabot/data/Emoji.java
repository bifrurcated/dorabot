package com.flowixlab.dorabot.data;

import discord4j.core.object.reaction.ReactionEmoji;

public enum Emoji {
    EMPTY("", ""),
    MALE(":man_red_haired:", "\uD83D\uDC68\u200D\uD83E\uDDB0"),
    FEMALE(":woman_red_haired:", "\uD83D\uDC69\u200D\uD83E\uDDB0"),
    BABY(":baby:", "\uD83D\uDC76"),
    OLD(":older_man:", "\uD83D\uDC74");

    private final String discordNotation;
    private final ReactionEmoji reactionEmoji;

    Emoji(String discordNotation, String unicode) {
        this.discordNotation = discordNotation;
        this.reactionEmoji = ReactionEmoji.unicode(unicode);
    }

    public ReactionEmoji reactionEmoji() {
        return reactionEmoji;
    }

    @Override
    public String toString() {
        return discordNotation;
    }
}
