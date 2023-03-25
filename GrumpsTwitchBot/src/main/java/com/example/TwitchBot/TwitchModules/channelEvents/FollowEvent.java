package com.example.TwitchBot.TwitchModules.channelEvents;

//import com.example.TwitchBot.NotWorking.arduino.ArduinoHandler;
import com.example.TwitchBot.TwitchModules.channelChat.ChatEventHandler;
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
//    private final ArduinoHandler arduinoHandler;
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
                followerService.saveFollower(new Follower(Long.parseLong(followingEvent.getData().getUserId()),
                        followingEvent.getData().getUsername(),
                        followingEvent.getData().getDisplayName(),
                        Instant.now()));
            }
        });
    }

    private String chooseRandomQuoteToSayHelloToNewFollower(FollowingData followerData){
        String quote = "";
        if (followerService.isFollowerExistsById(Long.valueOf(followerData.getUserId()))){
            return "";
        }
        String followerName = followerData.getDisplayName();
        Integer quoteNumber = (int) Math.floor(1 + (int) (Math.random() * (HELLO_QUOTES_COUNT )));
        switch (quoteNumber){
            case 1:{
                    quote = "К нам залетает @" + followerName + "! Даровенечки! wideVIBE";
                break;
            }
            case 2:{
                quote = "Хоп, хоп, хоп, кто к нам присоединяется? Да это же  @" + followerName+ "! Приветики!  ratJAM ";
                break;
            }
            case 3:{
                quote = "Кто тут у нас впервые? Это же @" + followerName + "! Скажем привяо!  pepeJAM TeaTime ";
                break;
            }
            case 4:{
                quote = "Только пришел, @" + followerName + "? Отлично! Присаживайся к нам!   blanketJam ";
                break;
            }
            case 5:{
                quote = "В дверь с ноги влетает @" + followerName + "! kiryuArrive";
                break;
            }

        }
        return quote;
    }
}
