package com.vobis.discordrpg;

import com.vobis.discordrpg.commands.BootstrapCommand;
import com.vobis.discordrpg.commands.CommandHandler;
import com.vobis.discordrpg.zones.ZoneManager;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public final class DiscordRPG {

    public static DiscordRPG INSTANCE;

    public static final String TOKEN = System.getenv("DISCORD_TOKEN");

    private final DiscordClient client;
    private final CommandHandler commandHandler;
    private final ZoneManager zoneManager;

    public DiscordRPG() {
        DiscordRPG.INSTANCE = this;

        this.client = DiscordClientBuilder.create(TOKEN).build();
        this.commandHandler = new CommandHandler();
        this.zoneManager = new ZoneManager();

        commandHandler.registerCommand("bootstrap", new BootstrapCommand());

        init();
    }

    private void init() {
        Mono.when(client.getEventDispatcher().on(ReadyEvent.class)
                        .doOnNext(rdy -> System.out.println("Ready to go! (" + rdy.getSelf().getUsername() + ")")),

                client.getEventDispatcher().on(MessageCreateEvent.class)
                        .map(MessageCreateEvent::getMessage)
                        .flatMap(commandHandler::executeCommands),

                client.login()
        ).block();
    }

    public static void main(String[] args) {
        new DiscordRPG();
    }
}
