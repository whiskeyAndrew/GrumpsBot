package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.entity.Karma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KarmaRepo extends JpaRepository<Karma,Long>{
    Karma findAllByFollower(Follower follower);
}
