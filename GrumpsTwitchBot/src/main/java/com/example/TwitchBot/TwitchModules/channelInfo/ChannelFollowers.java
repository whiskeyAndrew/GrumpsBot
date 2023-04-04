package com.example.TwitchBot.TwitchModules.channelInfo;

import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.services.FollowerService;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.Follow;
import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.domain.HelixPagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelFollowers {
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private List<Follow> followers;
    private  final FollowerService followerService;
    @PostConstruct
    public void init(){
        followers = new ArrayList<>();
        updateFollowersDB();
    }
    public void updateFollowersDB(){
        String pagination = null;
        while(true) {
            FollowList followersPage = twitchClient.getHelix().getFollowers(twitchClientConfig.getChannelTokenAccess(), null, twitchClientConfig.getChannelId(), pagination, 100).execute();
            followers.addAll(followersPage.getFollows());

            pagination = followersPage.getPagination().getCursor();
            if (pagination==null){
                break;
            }
        }

        followers.forEach(e->followerService.saveFollower(new Follower(Long.parseLong(e.getFromId()),e.getFromLogin(),e.getFromName(),e.getFollowedAtInstant())));
        followerService.saveFollower(new Follower(Long.parseLong(twitchClientConfig.getChannelId()),"dieorpie","Dieorpie", Instant.EPOCH));
        log.info("Updated Followers DB");
    }
}
