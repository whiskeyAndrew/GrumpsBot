package com.example.TwitchBot.ChannelInfo;

import com.example.TwitchBot.Client;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.BannedUserList;
import com.github.twitch4j.helix.domain.SubscriptionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelBans {

    @Autowired
    public void setTwitchClient(Client client) {
        this.twitchClient = client.getTwitchClient();
    }

    TwitchClient twitchClient;

        BannedUserList getBannedList() {

        System.out.println("---------------BANS---------------");
        BannedUserList bannedUserList = twitchClient.getHelix().getBannedUsers(Client.TWITCH_CHANNEL_ACCESS_TOKEN, Client.TWITCH_CHANNEL_ID, null, null, null).execute();
        bannedUserList.getResults().forEach(bannedUser -> {
            System.out.println(bannedUser);
        });
        System.out.println();
        return bannedUserList;
    }
}
