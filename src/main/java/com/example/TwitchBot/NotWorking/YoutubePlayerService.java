package com.example.TwitchBot.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.http.HttpHeaders;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//НЕ РАБОТАЕТ
@Service
@Getter
public class YoutubePlayerService {

    ArrayList<String> musicRequests;
    HttpHeaders headers;
    ScriptEngineManager manager;
    ScriptEngine engine;

    @Value("web.ip")
    String url;

    @PostConstruct
    private void init() {
        musicRequests = new ArrayList<>();
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
    }

    public void addMusic(String musicURL) {
        musicRequests.add(musicURL);
    }

}
