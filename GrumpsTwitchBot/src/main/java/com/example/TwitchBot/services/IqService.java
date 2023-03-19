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

    public Iq findByName(String channelName){
        return jpa.findFirstByChannelName(channelName);
    }

    public Iq insertCucumber(Iq iq){
        return jpa.saveAndFlush(iq);
    }

    public Iq updateCucumber(Iq iq){
        Iq iqToUpdate = jpa.getCucumberByChannelName(iq.getChannelName());
        iqToUpdate.setTime(Instant.now());
        iqToUpdate.setSize(iq.getSize());
        return jpa.save(iqToUpdate);
    }

}
