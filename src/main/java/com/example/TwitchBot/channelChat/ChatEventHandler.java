package com.example.TwitchBot.channelChat;

import com.example.TwitchBot.channelChat.internalCommands.DatabaseCommandsHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Command;
import com.example.TwitchBot.entity.Cucumber;
import com.example.TwitchBot.services.CucumberService;
import com.example.TwitchBot.services.DatabaseCommandsService;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
@Setter
@Getter
@RequiredArgsConstructor
public class ChatEventHandler extends Thread {

    private final static int MAX_CUCUMBER_SIZE = 25;
    private final static int MIN_CUCUMBER_SIZE = 3;

    private final DatabaseCommandsHandler dataBaseCommandsHandler;
    private final DatabaseCommandsService databaseCommandsService;
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;

    private final CucumberService cucumberService;

    @PostConstruct
    private void initStart() {
        start();
    }

    @Override
    public void run() {
        System.out.println("STARTED LISTENING CHANNEL");
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] " + event.getUser().getName() + ": " + event.getMessage());

            //Команда в БД
            String[] words = event.getMessage().split(" ");
            if(databaseCommandsService.isCommandExists(words[0])){
                Command command = databaseCommandsService.getCommandByName(words[0]);
                sendMessageToChannelChat(command.getCommandAnswer());
                return;
            }

            if (event.getMessage().startsWith("SoCute")) {
                sendMessageToChannelChat("SoCute SoCute SoCute SoCute SoCute SoCute");
                return;
            }

            if(event.getMessage().startsWith(DatabaseCommandsHandler.ADD_NEW_COMMAND)){
                if(event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    sendMessageToChannelChat(dataBaseCommandsHandler.addNewCommand(event.getMessage()));
                }
                return;
            }

            //Пофиксить, сделать метод возвращаемый с ответом
            if(event.getMessage().startsWith(DatabaseCommandsHandler.DELETE_COMMAND)){
                if(event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    if (dataBaseCommandsHandler.deleteCommand(event.getMessage())) {
                        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "Команда удалена!");
                    } else {
                        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "Ошибка! Команда не была удалена");
                    }
                }
                return;
            }

            //Надеюсь это никто не увидит
            if (event.getMessage().startsWith("!биба")) {
                handleCucumberMessage(event);
                return;
            }

            if (event.getMessage().startsWith("!кубик")) {
                int size = (int) Math.floor(Math.random() * (6 + 1));

                if (size == 0) {
                    size = 1;
                }
                sendMessageToChannelChat("Бросок кубика! Выпадает: " + size);
                return;
            }
        });
    }

    void handleCucumberMessage(ChannelMessageEvent event) {
        Cucumber cucumber = cucumberService.findByName(event.getUser().getName());

        if (cucumber == null) {
            cucumber = cucumberService.insertCucumber(new Cucumber(null, event.getUser().getName(),
                    Instant.now(), (int) Math.floor(Math.random() * (MAX_CUCUMBER_SIZE - 5 + 1) + 5)));
            sendMessageToChannelChat("@"+event.getUser().getName() +" Вау! Линейка показывает " + cucumber.getSize() + " сантиметров! С первым замером! PepegaAim");
            return;
        } else {
            if (cucumber.getTime().plusSeconds(86400).isAfter(Instant.now())) {
                sendMessageToChannelChat("@"+event.getUser().getName() +" Ежедневный замер уже был! NOPERS Линейка показывала " + cucumber.getSize() + " сантиметров!");
                return;
            } else {
                int size = cucumber.getSize();
                cucumber.setSize((int) Math.floor(Math.random() * (MAX_CUCUMBER_SIZE - MIN_CUCUMBER_SIZE + 1) + MIN_CUCUMBER_SIZE));
                cucumber = cucumberService.updateCucumber(cucumber);

                if (cucumber.getSize() == MAX_CUCUMBER_SIZE) {
                    sendMessageToChannelChat("@"+event.getUser().getName() +" Нифига себе! Линейка показывает " + cucumber.getSize() + " сантиметров! Дальше расти некуда! peepoClap");
                    return;
                }

                if (size < cucumber.getSize()) {
                    sendMessageToChannelChat("@"+event.getUser().getName() +" Вау! Линейка показывает " + cucumber.getSize() + " сантиметров! Ты подрос! widepeepoHappy ");
                } else if (size > cucumber.getSize()) {
                    sendMessageToChannelChat("@"+event.getUser().getName() +" Мнда. Стало хуже. Линейка показывает " + cucumber.getSize() + " сантиметров!  А было "+size+" widepeepoSad");
                } else {
                    sendMessageToChannelChat("@"+event.getUser().getName() +" Линейка показывает " + cucumber.getSize() + " сантиметров. Ничего не поменялось !  peepoGiggles");
                }
            }
        }

    }

    void sendMessageToChannelChat(String message){
        if(message.startsWith("/")){
            return;
        }
        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), message);
    }
}
