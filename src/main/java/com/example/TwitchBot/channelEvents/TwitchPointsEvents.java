package com.example.TwitchBot.channelEvents;

import com.example.TwitchBot.channelChat.ChatEventHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.This;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TwitchPointsEvents extends Thread{

    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final ChatEventHandler chatEventHandler;

    @PostConstruct
    public void init(){
        this.start();
    }

    public void run(){
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(twitchClientConfig.getCredential(),twitchClientConfig.getChannelId());
        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class,event -> {
            if(event.getRedemption().getReward().getTitle().equals("Попробовать оффнуть компьютер")){
                tryToOffPC(event);
            }
            System.out.println(event);
        } );
    }

    public void tryToOffPC(RewardRedeemedEvent event){
        Integer chance = (int) Math.floor(Math.random() * (10000 + 1));
        chatEventHandler.sendMessageToChatIgnoreTimer("@"+event.getRedemption().getUser().getDisplayName()+ " выбил "+ chance);
        if(chance==10000){
            chatEventHandler.sendMessageToChatIgnoreTimer(" HAHAHA УХ БЛЯ  HAHAHA ");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Runtime runtime = Runtime.getRuntime();
            try {
                Process proc = runtime.exec("shutdown -s -t 0");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
        else if(chance==69){
            chatEventHandler.sendMessageToChatIgnoreTimer("@"+event.getRedemption().getUser().getDisplayName()+"  owoUwu  owoUwu  owoUwu ");
        }
        else if(chance==0){
            chatEventHandler.sendMessageToChatIgnoreTimer("@"+event.getRedemption().getUser().getDisplayName()+" , ого, первое место! С конца POGGERS ");
        }
    }
}
