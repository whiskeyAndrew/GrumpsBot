package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.repository.FollowerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class FollowerService {
    private final FollowerRepo jpa;

    public Follower findById(Long id){return jpa.findFirstByUserId(id);};
    public Follower findByDisplayName(String login){return jpa.findFirstByLogin(login);}

    public Follower insertNewFollower(Follower follower){
        return jpa.saveAndFlush(follower);
    }
    public List<Follower> findAll(){
        return jpa.findAll();
    }
    public Follower saveFollower(Follower follower){
        return jpa.save(follower);
    }
    public Boolean isFollowerExistsById(Long id){return jpa.existsFollowerByUserId(id);}
}
