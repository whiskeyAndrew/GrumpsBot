package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Iq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IqRepo extends JpaRepository<Iq,Long> {
    Iq findFirstByChannelName(String channelName);
    Iq getCucumberByChannelName(String channelName);

}
