package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.DuelistStats;
import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.repository.DuelistStatsRepo;
import com.example.TwitchBot.repository.FollowerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class DuelistStatService {

    private final DuelistStatsRepo jpa;
    private final FollowerRepo followerJpa;

    public DuelistStats getAllByUserId(Long userId){
        Follower follower = followerJpa.findFirstById(userId);
        Optional<DuelistStats> d =jpa.getDuelistStatsByFollower(follower);
        if(!d.isPresent()){
            DuelistStats newDuelist = new DuelistStats(null,follower,0,0,0,0);
            jpa.saveAndFlush(newDuelist);
            return newDuelist;
        }
        return d.get();
    }

    public void saveDuelistStats(DuelistStats duelistStats){
        jpa.saveAndFlush(duelistStats);
        return;
    }
}
