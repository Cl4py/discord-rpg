package com.vobis.discordrpg.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MessageUtils {

    /**
     * Sends a message to a channel that will automatically delete itself.
     *
     * @param message     The message to send
     * @param textChannel The channel to send the message to
     * @param duration    The amount of time before delete in ms
     */
    public static Mono<Void> createAutoDeleteMessage(String message, TextChannel textChannel, long duration) {
        return textChannel.createMessage(message).delayElement(Duration.ofMillis(duration)).flatMap(Message::delete);
    }
}
