package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepo extends JpaRepository<Follower,Long> {

    Follower findFirstByUserId(Long userId);
    Follower findFirstByLogin(String login);
    Boolean existsFollowerByUserId(Long id);
}
