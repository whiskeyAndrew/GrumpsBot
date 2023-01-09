package com.example.TwitchBot.ChannelInfo;

import com.example.TwitchBot.Client;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.BannedUserList;
import com.github.twitch4j.helix.domain.ModeratorList;
import org.springframework.beans.factory.annotation.Autowired;

public class ChannelModerators {
    @Autowired
    public void setTwitchClient(Client client) {
        this.twitchClient = client.getTwitchClient();
    }

    TwitchClient twitchClient;

    ModeratorList getModeratorsList() {

        System.out.println("---------------MODERATORS---------------");
        ModeratorList moderatorList = twitchClient.getHelix().getModerators(Client.TWITCH_CHANNEL_ACCESS_TOKEN, Client.TWITCH_CHANNEL_ID, null, null).execute();
        moderatorList.getModerators().forEach(moderator -> {
            System.out.println(moderator);
        });
        System.out.println();
        return moderatorList;
    }
}
