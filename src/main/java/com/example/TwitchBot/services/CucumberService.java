package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Cucumber;
import com.example.TwitchBot.repository.CucumberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Transactional
@RequiredArgsConstructor
@Service
public class CucumberService {
    private final CucumberRepo jpa;

    public Cucumber findByName(String channelName){
        return jpa.findFirstByChannelName(channelName);
    }

    public Cucumber insertCucumber(Cucumber cucumber){
        return jpa.saveAndFlush(cucumber);
    }

    public Cucumber updateCucumber(Cucumber cucumber){
        Cucumber cucumberToUpdate = jpa.getCucumberByChannelName(cucumber.getChannelName());
        cucumberToUpdate.setTime(Instant.now());
        cucumberToUpdate.setSize(cucumber.getSize());
        return jpa.save(cucumberToUpdate);
    }

}
