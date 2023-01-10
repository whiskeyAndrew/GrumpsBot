package com.example.TwitchBot;

import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Cucumber;
import com.example.TwitchBot.repository.CucumberRepository;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

@Component
@Setter
@Getter
@RequiredArgsConstructor
public class ChatEventHandler extends Thread {

    private final static int MAX_CUCUMBER_SIZE = 25;
    private final static int MIN_CUCUMBER_SIZE = 3;
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;

    private final CucumberRepository cucumberRepository;

    @PostConstruct
    private void initStart() {
        start();
    }

    @Override
    public void run() {
        System.out.println("STARTED LISTENING CHANNEL");
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] " + event.getUser().getName() + ": " + event.getMessage());

            if (event.getMessage().startsWith("SoCute")) {
                twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "SoCute SoCute SoCute SoCute SoCute SoCute");
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

                twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "Бросок кубика! Выпадает: " + size);
                return;
            }
        });
    }

    void handleCucumberMessage(ChannelMessageEvent event) {
        Cucumber cucumber = cucumberRepository.findByName(event.getUser().getName());

        if (cucumber == null) {
            cucumber = cucumberRepository.insertCucumber(new Cucumber(null, event.getUser().getName(),
                    Instant.now(), (int) Math.floor(Math.random() * (MAX_CUCUMBER_SIZE - 5 + 1) + 5)));
            twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                    "@"+event.getUser().getName() +" Вау! Линейка показывает " + cucumber.getSize() + " сантиметров! С первым замером! PepegaAim");
        } else {
            if (cucumber.getTime().plusSeconds(86400).isAfter(Instant.now())) {
                twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                        "@"+event.getUser().getName() +" Ежедневный замер уже был! NOPERS Линейка показывала " + cucumber.getSize() + " сантиметров!   ");
                return;
            } else {
                int size = cucumber.getSize();
                cucumber.setSize((int) Math.floor(Math.random() * (MAX_CUCUMBER_SIZE - MIN_CUCUMBER_SIZE + 1) + MIN_CUCUMBER_SIZE));
                cucumber = cucumberRepository.updateCucumber(cucumber);

                if (cucumber.getSize() == MAX_CUCUMBER_SIZE) {
                    twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                            "@"+event.getUser().getName() +" Нифига себе! Линейка показывает " + cucumber.getSize() + " сантиметров! Дальше расти некуда! peepoClap");
                    return;
                }

                if (size < cucumber.getSize()) {
                    twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                            "@"+event.getUser().getName() +" Вау! Линейка показывает " + cucumber.getSize() + " сантиметров! Ты подрос! widepeepoHappy ");
                } else if (size > cucumber.getSize()) {
                    twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                            "@"+event.getUser().getName() +" Мнда. Стало хуже. Линейка показывает " + cucumber.getSize() + " сантиметров!  А было "+size+" widepeepoSad");
                } else {
                    twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(),
                            "@"+event.getUser().getName() +" Линейка показывает " + cucumber.getSize() + " сантиметров. Ничего не поменялось !  peepoGiggles");
                }
            }
        }

    }
}
