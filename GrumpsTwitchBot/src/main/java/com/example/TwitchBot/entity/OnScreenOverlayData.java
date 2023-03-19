package com.example.TwitchBot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//Переделать потом под энтити для хранения в бд
@AllArgsConstructor
@Getter
@Setter
public class OnScreenOverlayData {
    private String path;
    private String time;
    private String sound;
}
