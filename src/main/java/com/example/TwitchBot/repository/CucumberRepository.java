package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Cucumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Instant;

@Repository
@Transactional
@RequiredArgsConstructor
public class CucumberRepository{
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
