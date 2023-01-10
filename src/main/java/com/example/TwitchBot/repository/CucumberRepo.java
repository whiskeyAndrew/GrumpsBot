package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Cucumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CucumberRepo extends JpaRepository<Cucumber,Long> {
    Cucumber findFirstByChannelName(String channelName);
    Cucumber getCucumberByChannelName(String channelName);

}
