package com.example.TwitchBot.ChannelInfo;

import com.example.TwitchBot.Client;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.SubscriptionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelSubscribers {

    @Autowired
    public void setTwitchClient(Client client) {
        this.twitchClient = client.getTwitchClient();
    }
    TwitchClient twitchClient;

    SubscriptionList getSubscriptionList(){
        System.out.println("---------------SUBSCRIBERS---------------");
        SubscriptionList resultList = twitchClient.getHelix().getSubscriptions( Client.TWITCH_CHANNEL_ACCESS_TOKEN, Client.TWITCH_CHANNEL_ID, null, null,null).execute();
        resultList.getSubscriptions().forEach(subscription -> {
            System.out.println("Subscriber: "+subscription.getUserName());
        });
        System.out.println();
        return  resultList;
    }
}
