package com.example.TwitchBot.config;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.service.IEventHandler;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Getter
@Slf4j
public class TwitchClientConfig {

    String identityProvider = "twitch";


    //запилить чтение из конфига
    @Value("${bot.token.access}")
    private String botTokenAccess;

    @Value("${bot.id}")
    private String botId;

    @Value("${bot.token.refresh}")
    private String botTokenRefresh;

    @Value("${channel.token.access}")
    private String channelTokenAccess;

    @Value("${channel.id}")
    private String channelId;

    private String botNameToId = "868301861";
    @Value("${channel.name}")
    private String channelName;
    OAuth2Credential credential;

    @Bean

    public TwitchClient twitchClient(){
        log.info("Initializing twitch client");

        credential = new OAuth2Credential(identityProvider, botTokenAccess);

        TwitchClient twitchClient;
        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnableHelix(true)
                .withEnablePubSub(true)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withChatAccount(credential)
                .build();

        twitchClient.getChat().joinChannel(channelName);
        return twitchClient;
    }


}
