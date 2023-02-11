package com.example.TwitchBot.channelEvents;

import com.example.TwitchBot.NotWorking.arduino.ArduinoHandler;
import com.example.TwitchBot.channelChat.ChatEventHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.services.FollowerService;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.domain.FollowingData;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class FollowEvent extends Thread{
    private final FollowerService followerService;
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final ArduinoHandler arduinoHandler;
    private final ChatEventHandler chatEventHandler;
    private final static Integer HELLO_QUOTES_COUNT = 5;
    @PostConstruct
    public void init(){
        this.start();
    }

    public void run(){
        System.out.println("Started listening for new followers");

        twitchClient.getPubSub().listenForFollowingEvents(twitchClientConfig.getCredential(), twitchClientConfig.getChannelId());
        twitchClient.getEventManager().onEvent(FollowingEvent.class, followingEvent -> {
            System.out.println("new follower");
            String response = chooseRandomQuoteToSayHelloToNewFollower(followingEvent.getData());
            if(response!="") {
                chatEventHandler.sendMessageToChatIgnoreTimer(response);
            }
        });
    }

    private String chooseRandomQuoteToSayHelloToNewFollower(FollowingData followerData){
        String quote = "";
        Follower follower = followerService.findById(Long.parseLong(followerData.getUserId()));
        if(follower!=null){
            return quote;
        } else {
            follower = new Follower(Long.parseLong(followerData.getUserId()),
                    followerData.getDisplayName(),
                    followerData.getUsername(),
                    true,
                    Instant.now(),
                    0,
            Instant.EPOCH);
        }
        Integer quoteNumber = (int) Math.floor(1 + (int) (Math.random() * (HELLO_QUOTES_COUNT )));
        switch (quoteNumber){
            case 1:{
                    quote = "К нам залетает @" + follower.getDisplayName() + "! Даровенечки! wideVIBE";
                break;
            }
            case 2:{
                quote = "Хоп, хоп, хоп, кто к нам присоединяется? Да это же  @" + follower.getDisplayName() + "! Приветики!  ratJAM ";
                break;
            }
            case 3:{
                quote = "Кто тут у нас впервые? Это же @" + follower.getDisplayName() + "! Скажем привяо!  pepeJAM TeaTime ";
                break;
            }
            case 4:{
                quote = "Только пришел, @" + follower.getDisplayName() + "? Отлично! Присаживайся к нам!   blanketJam ";
                break;
            }
            case 5:{
                quote = "В дверь с ноги влетает @" + follower.getDisplayName() + "! kiryuArrive";
                break;
            }

        }
        followerService.insertNewFollower(follower);
        return quote;
    }
}
