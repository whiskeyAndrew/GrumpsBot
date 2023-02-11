package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepo extends JpaRepository<Follower,Long> {

    Follower findFirstById(Long id);
    Follower findFirstByDisplayName(String displayName);
    Boolean existsFollowerById(Long id);
}
