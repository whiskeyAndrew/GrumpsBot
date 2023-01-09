package com.example.TwitchBot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IDisposable;
import com.github.philippheuer.events4j.api.service.IEventHandler;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.BannedUserList;
import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.domain.ModeratorList;
import com.github.twitch4j.helix.domain.SubscriptionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;


import javax.annotation.PostConstruct;

@Component
public class Client {
    public TwitchClient getTwitchClient() {
        return twitchClient;
    }

    TwitchClient twitchClient;
    String identityProvider = "twitch";
    public static final String ACCESS_TOKEN;
    public static final String BOT_ID ;
    public static final String REFRESH_TOKEN ;
    public static final String TWITCH_CHANNEL_ACCESS_TOKEN ;
    public static final String TWITCH_CHANNEL_ID ;
    public static final String CHANNEL_NAME ;

    IEventHandler eventManager;


    @PostConstruct
    void init(){
        System.out.println("Init");
        OAuth2Credential credential = new OAuth2Credential(identityProvider, ACCESS_TOKEN);
        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnableHelix(true)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withChatAccount(credential)
                .build();
        twitchClient.getChat().joinChannel(CHANNEL_NAME);
        twitchClient.getChat().sendMessage(CHANNEL_NAME, "Я ПОДНЯЛСЯ!");





    }


}
