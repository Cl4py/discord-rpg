package com.vobis.discordrpg.commands.handler;

import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.util.MessageUtils;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.*;

public class CommandHandler {

    public static final String MENTION = "@";
    public static final String HUMAN_DELIMETER = " ";
    public static final String EMPTY_STRING = "";
    public static final String PREFIX = System.getenv("COMMAND_PREFIX");

    private Map<String, ICommand> commandMap = Collections.synchronizedMap(new HashMap<>());

    public Mono<?> executeCommands(Message message) {
        if (message.getContent().isPresent()) {
            String content = message.getContent().get();

            if (content.startsWith(PREFIX)) {
                String[] commandTokens = content.split(HUMAN_DELIMETER);
                String command = commandTokens[0].replace(PREFIX, EMPTY_STRING);
                List<String> args = getArgs(commandTokens);

                return runCommand(command, args, message);
            }
        }

        return Mono.empty();
    }

    public void registerCommand(String command, ICommand handler) {
        commandMap.put(command, handler);
    }

    private Mono<?> runCommand(String command, List<String> args, Message message) {
        if (commandMap.containsKey(command)) {
            return Mono.zip(Mono.just(args), Mono.just(message), message.getAuthorAsMember(), message.getGuild(), message.getChannel().ofType(TextChannel.class))
                    .flatMap(TupleUtils.function(commandMap.get(command)::execute))
                    .and(message.delete().onErrorResume($ -> Mono.empty()));
        }

        //TODO sanitize command output
        return message.getChannel()
                .ofType(TextChannel.class)
                .flatMap(channel -> MessageUtils.createAutoDeleteMessage(Translations.templateFor("command.notFound", command.replaceAll(MENTION, EMPTY_STRING)), channel, 5000))
                .and(message.delete());
    }

    private List<String> getArgs(String[] tokens) {
        List<String> result = new ArrayList<>(Arrays.asList(tokens));
        result.remove(0);
        return result;
    }
}
