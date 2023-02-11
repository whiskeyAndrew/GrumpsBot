package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.repository.FollowerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class FollowerService {
    private final FollowerRepo jpa;

    public Follower findById(Long id){return jpa.findFirstById(id);};
    public Follower findByDisplayName(String displayName){return jpa.findFirstByDisplayName(displayName);}

    public Follower insertNewFollower(Follower follower){
        return jpa.saveAndFlush(follower);
    }

    public Follower saveFollower(Follower follower){
        return jpa.save(follower);
    }
    public Boolean isFollowerExistsById(Long id){return jpa.existsFollowerById(id);}
}
