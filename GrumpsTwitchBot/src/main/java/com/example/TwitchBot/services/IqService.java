package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Iq;
import com.example.TwitchBot.repository.IqRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Transactional
@RequiredArgsConstructor
@Service
public class IqService {
    private final IqRepo jpa;
    private final FollowerService followerService;
    public Iq getIqEntityByUserId(Long id){
        return jpa.getByFollower(followerService.findById(id));
    }
    public Iq insertIqEntity(Iq iq){
        return jpa.saveAndFlush(iq);
    }

    public Iq save(Iq iq){
        return jpa.save(iq);
    }
    public Iq updateIqValue(Iq iq){
        Iq iqToUpdate = jpa.getByFollower(iq.getFollower());
        iqToUpdate.setTime(Instant.now());
        iqToUpdate.setIq(iq.getIq());
        return jpa.save(iqToUpdate);
    }


}
