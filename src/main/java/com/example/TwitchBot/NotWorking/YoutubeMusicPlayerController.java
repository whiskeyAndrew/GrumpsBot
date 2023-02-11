package com.example.TwitchBot.NotWorking;

import com.example.TwitchBot.services.YoutubePlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//Not working

@RestController
@RequiredArgsConstructor
public class YoutubeMusicPlayerController {
        private final YoutubePlayerService youtubePlayer;

        @GetMapping("/music")
        public ResponseEntity getRequestedMusic(){
                System.out.println("request");
                youtubePlayer.addMusic("l8W98L94gw8");
                youtubePlayer.addMusic("a6QSpLZ-fT4");
                if(youtubePlayer.getMusicRequests().isEmpty()){
                        return ResponseEntity.badRequest().body("500");
                }

                return ResponseEntity.ok(youtubePlayer.getMusicRequests());
        }
}
