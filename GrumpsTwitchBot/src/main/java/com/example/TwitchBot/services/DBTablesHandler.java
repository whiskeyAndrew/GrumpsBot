package com.example.TwitchBot.services;

import com.example.TwitchBot.TwitchModules.channelInfo.ChannelFollowers;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.entity.Karma;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DBTablesHandler {
    private final FollowerService followerService;
    private final KarmaService karmaService;
    private final ChannelFollowers channelFollowers;
    private final TwitchClientConfig twitchClientConfig;

    public void rebaseFollowersDatabase(){
        log.info("Started rebasing Followers DB");
        //Возможна жирная нагрузка, в идеале надо сделать форыч элемент в базе. а может и не надо, пока непонятно
        List<Follow> followers = channelFollowers.getAllFollowersFromTwitch();
        followers.forEach(e->followerService.saveFollower(new Follower(Long.parseLong(e.getFromId()),e.getFromLogin(),e.getFromName(),e.getFollowedAtInstant())));
        Follower myself = new Follower(Long.parseLong(twitchClientConfig.getChannelId()),"dieorpie","Dieorpie", Instant.EPOCH);
        followerService.saveFollower(myself);

        log.info("Followers DB rebased");
    }

    public void rebaseKarmaDatabase(){
        log.info("Started rebasing Karma DB");
        List<Follower> followers = followerService.findAll();
        for (Follower follower:followers) {
            Karma karma =  karmaService.findByFollower(follower);
            if(karma==null){
                karmaService.addNewKarmaEntity(follower);
            }
        }
        log.info("Karma DB rebased");
    }
}
