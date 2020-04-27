package com.vobis.discordrpg;

import com.vobis.discordrpg.player.Players;
import com.vobis.discordrpg.commands.impl.*;
import com.vobis.discordrpg.commands.handler.CommandHandler;
import com.vobis.discordrpg.mob.MobDefs;
import com.vobis.discordrpg.zones.ZoneManager;
import com.vobis.discordrpg.zones.ZoneMap;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Getter
public final class DiscordRPG {

    public static DiscordRPG INSTANCE;

    public static final String TOKEN = System.getenv("DISCORD_TOKEN");

    private final DiscordClient client;
    private final CommandHandler commandHandler;
    private final MobDefs mobs;
    private final ZoneManager zoneManager;
    private final ZoneMap zoneMap;
    private final Players players;

    public DiscordRPG() {
        DiscordRPG.INSTANCE = this;

        this.client = DiscordClientBuilder.create(TOKEN).build();
        this.commandHandler = new CommandHandler();
        this.mobs = new MobDefs();
        this.zoneManager = new ZoneManager();
        this.zoneMap = new ZoneMap();
        this.players = new Players();

        commandHandler.registerCommand("bootstrap", new BootstrapCommand());
        commandHandler.registerCommand("look", new LookCommand());
        commandHandler.registerCommand("travel", new TravelCommand());
        commandHandler.registerCommand("attack", new AttackCommand());
        commandHandler.registerCommand("join", new JoinCommand());

        init();
    }

    private void init() {
        Mono.when(client.getEventDispatcher().on(ReadyEvent.class)
                        .doOnNext(rdy -> System.out.println("Ready to go! (" + rdy.getSelf().getUsername() + ")")),

                client.getEventDispatcher().on(MessageCreateEvent.class)
                        .map(MessageCreateEvent::getMessage)
                        .flatMap(msg -> Mono.zip(Mono.just(msg), msg.getAuthorAsMember()))
                        .filter(TupleUtils.predicate((msg, author) -> !author.isBot()))
                        .flatMap(tuple -> commandHandler.executeCommands(tuple.getT1())),

                client.login()
        ).block();
    }

    public static void main(String[] args) {
        new DiscordRPG();
    }
}
