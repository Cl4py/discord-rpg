package com.vobis.discordrpg.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface ICommand {
    Mono<?> execute(Message message);
}
