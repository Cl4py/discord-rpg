package com.vobis.discordrpg.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public static final String HUMAN_DELIMETER = " ";
    public static final String EMPTY_STRING = "";
    public static final String PREFIX = System.getenv("COMMAND_PREFIX");

    private Map<String, ICommand> commandMap = Collections.synchronizedMap(new HashMap<>());

    public Mono<?> executeCommands(Message message) {
        if(message.getContent().isPresent()) {
            String content = message.getContent().get();

            if(content.startsWith(PREFIX)) {
                String[] commandTokens = content.split(HUMAN_DELIMETER);
                String command = commandTokens[0].replace(PREFIX, EMPTY_STRING);
                return runCommand(command, message);
            }
        }

        return Mono.empty();
    }

    public void registerCommand(String command, ICommand handler) {
        commandMap.put(command, handler);
    }

    private Mono<?> runCommand(String command, Message message){
        if(commandMap.containsKey(command)) {
            return commandMap.get(command).execute(message);
        }

        return Mono.empty();
    }
}
