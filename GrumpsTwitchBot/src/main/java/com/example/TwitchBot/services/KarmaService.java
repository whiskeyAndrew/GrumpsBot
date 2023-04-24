package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.entity.Karma;
import com.example.TwitchBot.repository.KarmaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KarmaService {

    private final KarmaRepo jpa;

    public void addNewKarmaEntity(Follower follower){
        if (follower==null){
            return;
        }
        Karma karma = new Karma();
        karma.setFollower(follower);
        karma.setKarma(0L);
        karma.setChangedSomeoneKarmaLastTime(Instant.EPOCH);
        jpa.save(karma);

    }

    public Karma findByFollower(Follower follower){
        return jpa.findAllByFollower(follower);
    }
}
