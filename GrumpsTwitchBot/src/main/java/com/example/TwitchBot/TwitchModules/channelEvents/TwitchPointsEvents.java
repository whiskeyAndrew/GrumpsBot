package com.example.TwitchBot.TwitchModules.channelEvents;

import com.example.TwitchBot.TwitchModules.channelChat.ChatEventHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.OnScreenOverlayData;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
@RequiredArgsConstructor
public class TwitchPointsEvents extends Thread{

    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final ChatEventHandler chatEventHandler;

    private Deque<OnScreenOverlayData> toShowQueue;

    @PostConstruct
    public void init(){
        toShowQueue = new ArrayDeque<>();
        this.start();
    }

    public OnScreenOverlayData getLastToShowElement(){
        assert toShowQueue.peek() != null;
        System.out.println("ahaha");
        if(toShowQueue.size()!=0) {
            return toShowQueue.pop();
        }
        else return null;
    }

    public void run(){
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(twitchClientConfig.getCredential(),twitchClientConfig.getChannelId());
        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class,event -> {
            if(event.getRedemption().getReward().getTitle().equals("Попробовать оффнуть компьютер")){
                tryToOffPC(event);
            }
            else             if(event.getRedemption().getReward().getTitle().equals("Боньк")){
                toShowQueue.add(new OnScreenOverlayData("https://i.redd.it/wyptzansk7m61.png","5",""));
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
