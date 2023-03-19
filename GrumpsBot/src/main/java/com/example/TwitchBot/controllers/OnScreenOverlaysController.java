package com.example.TwitchBot.controllers;

import com.example.TwitchBot.channelEvents.TwitchPointsEvents;
import com.example.TwitchBot.entity.OnScreenOverlayData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayDeque;
import java.util.Queue;

@RestController
@RequestMapping(value = "/alerts")
@RequiredArgsConstructor
public class OnScreenOverlaysController {
    private final TwitchPointsEvents twitchPointsEvents;

    @GetMapping("/points")
    public ResponseEntity<OnScreenOverlayData> getLastQuery(){
       OnScreenOverlayData data = twitchPointsEvents.getLastToShowElement();
       if(data==null){
           return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
       }
       else{
           return new ResponseEntity<>(data,HttpStatus.OK);
       }
    }
}
