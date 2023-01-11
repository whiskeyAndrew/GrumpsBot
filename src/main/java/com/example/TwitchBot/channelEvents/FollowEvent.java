package com.example.TwitchBot.channelEvents;

import com.example.TwitchBot.arduino.ArduinoHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class FollowEvent extends Thread{
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final ArduinoHandler arduinoHandler;

    @PostConstruct
    public void init(){
        this.start();
    }

    public void run(){
        System.out.println("Started listening for new followers");

        twitchClient.getPubSub().listenForFollowingEvents(twitchClientConfig.getCredential(), twitchClientConfig.getChannelId());
        twitchClient.getEventManager().onEvent(FollowingEvent.class, followingEvent -> {
            System.out.println("new follower");


        });
    }
}
