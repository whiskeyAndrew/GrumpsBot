package com.example.TwitchBot;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatEventHandler extends Thread{

    @Autowired
    public void setTwitchClient(Client client) {
        this.twitchClient = client.getTwitchClient();
        start();
    }

    TwitchClient twitchClient;

    @Override
    public void run() {
        System.out.println("STARTED LISTENING CHANNEL");
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println ("[" + event.getChannel().getName() + "]["+event.getPermissions().toString()+"] " + event.getUser().getName() + ": " + event.getMessage());

            if(event.getMessage().startsWith("SoCute")){
                twitchClient.getChat().sendMessage(Client.CHANNEL_NAME, "SoCute SoCute SoCute SoCute SoCute SoCute");
                return;
            }

            //Надеюсь это никто не увидит
            if(event.getMessage().startsWith("!биба")){
                int size = (int)Math.floor(Math.random() * (25 - 5 + 1) + 5);
                twitchClient.getChat().sendMessage(Client.CHANNEL_NAME, "Вау! Линейка показывает " + size + " сантиметров!");
                return;
            }

            if(event.getMessage().startsWith("!кубик")){
                int size = (int)Math.floor(Math.random() * (6  + 1));

                if(size ==0){
                    size = 1;
                }

                twitchClient.getChat().sendMessage(Client.CHANNEL_NAME, "Бросок кубика! Выпадает: " + size);
                return;
            }
        });
    }
}
