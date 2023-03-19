package com.example.TwitchBot.TwitchModules.channelInfo;

import com.example.TwitchBot.config.TwitchClientConfig;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.FollowList;
import com.netflix.hystrix.HystrixCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class ChannelFollowers {
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;

    public void updateFollowersDB(){
        FollowList followers =  twitchClient.getHelix().getFollowers(twitchClientConfig.getChannelTokenAccess(),twitchClientConfig.getChannelId(),null,null,100).execute();
        followers.getFollows().forEach(follow -> {
            System.out.println(follow.getFromName() + " is following " + follow.getToName());
        });

    }
}
