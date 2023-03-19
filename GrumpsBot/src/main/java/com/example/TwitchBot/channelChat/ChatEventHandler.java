package com.example.TwitchBot.channelChat;

import com.example.TwitchBot.channelChat.internalCommands.DatabaseCommandsHandler;
import com.example.TwitchBot.channelInfo.ChannelFollowers;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.Command;
import com.example.TwitchBot.entity.DuelistStats;
import com.example.TwitchBot.entity.Follower;
import com.example.TwitchBot.entity.Iq;
import com.example.TwitchBot.services.DuelistStatService;
import com.example.TwitchBot.services.FollowerService;
import com.example.TwitchBot.services.IqService;
import com.example.TwitchBot.services.DatabaseCommandsService;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.UserList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component
@Setter
@Getter
@RequiredArgsConstructor
public class ChatEventHandler extends Thread {
    private final ChannelFollowers channelFollowers;

    //Logic Staff
    private final DatabaseCommandsHandler dataBaseCommandsHandler;
    private final DatabaseCommandsService databaseCommandsService;
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final FollowerService followerService;
    private final DuelistStatService duelistStatService;
    private final static int MESSAGE_TIMEOUT_EVERYONE = 1;
    //Timeout, need to prevent chat spam from bot (maybe not working right now)
    Instant lastMessageTime;

    //Iq checker
    private final IqService iqService;
    private final static int MAX_IQ = 200;
    private final static int MIN_IQ = 3;

    //Duels
    Instant firstDuelistTriesToFindSomeoneTime;
    private DuelistStats duelistOne;
    private DuelistStats duelistTwo;
    boolean isHandlingDuel = false;

    @PostConstruct
    private void initStart() throws MalformedURLException, UnsupportedEncodingException {
        firstDuelistTriesToFindSomeoneTime = Instant.EPOCH;
        start();
        lastMessageTime = Instant.now();
    }

    @PreDestroy
    private void appCloseMessage() {
        sendMessageToChatIgnoreTimer("Я УПАЛ");
    }

    @Override
    public void run() {
        System.out.println("STARTED LISTENING CHANNEL");
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] "
                    + event.getUser().getName() + ": " + event.getMessage());

            //Подержать какое-то время, проверяет есть ли в бд фолловерах человек
            if (!followerService.isFollowerExistsById(Long.parseLong(event.getUser().getId()))) {
                UserList resultList = twitchClient.getHelix().getUsers(twitchClientConfig.getChannelTokenAccess(), Collections.singletonList(event.getUser().getId()), null).execute();
                resultList.getUsers().forEach(user -> {
                    Follower follower = new Follower(Long.parseLong(user.getId()), user.getDisplayName(), user.getDisplayName(), true, Instant.EPOCH, 0, Instant.EPOCH);
                    followerService.insertNewFollower(follower);
                });
            }

            if(isHandlingDuel){
                return;
            }
            //Команда в БД
            String[] words = event.getMessage().split(" ");
            if (databaseCommandsService.isCommandExists(words[0])) {
                Command command = databaseCommandsService.getCommandByName(words[0]);
                if (event.getPermissions().contains(command.getPermissionLevel())) {
                    sendMessageToChannelChat(command.getCommandAnswer(), CommandPermission.EVERYONE);
                } else {
                    System.out.println("No access to command");
                }

                return;
            }
            if (event.getMessage().startsWith("!дебаг")) {
                channelFollowers.updateFollowersDB();
                return;
            }

            /*
            if(event.getMessage().contains("вебк") || event.getMessage().contains("камер"))
            {
                sendMessageToChannelChat("Стримлер отдыхает", CommandPermission.EVERYONE);
                return;
            }
*/

            if (event.getMessage().startsWith("SoCute")) {
                sendMessageToChannelChat("SoCute SoCute SoCute SoCute SoCute SoCute", CommandPermission.EVERYONE);
                return;
            }

            if (event.getMessage().startsWith(DatabaseCommandsHandler.ADD_NEW_COMMAND)) {
                if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    sendMessageToChannelChat(dataBaseCommandsHandler.addNewCommand(event.getMessage()), CommandPermission.EVERYONE);
                }
                return;
            }

            if (event.getMessage().startsWith(DatabaseCommandsHandler.DELETE_COMMAND)) {
                if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    if (dataBaseCommandsHandler.deleteCommand(event.getMessage())) {
                        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "Команда удалена!");
                    } else {
                        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "Ошибка! Команда не была удалена");
                    }
                }
                return;
            }

            //е или ё скипаем
            if (event.getMessage().startsWith("!iq мо") || event.getMessage().startsWith("!айкью мо")) {
                handleIqMessageNow(event);
                return;
            }
            if (event.getMessage().startsWith("!iq") || event.getMessage().startsWith("!айкью")) {
                handleIqMessage(event);
                return;
            }

            if (event.getMessage().startsWith("!карма")) {
                handleKarmaMessage(event.getMessage(), event.getUser());
                return;
            }

            if (event.getMessage().startsWith("!кубик")) {
                int size = (int) Math.floor(Math.random() * (6 + 1));

                if (size == 0) {
                    size = 1;
                }
                sendMessageToChannelChat("Бросок кубика! Выпадает: " + size, CommandPermission.EVERYONE);
                return;
            }

            if (event.getMessage().startsWith("!дуэли")) {
                handleDuelMessageStat(event);
                return;
            }
            if (event.getMessage().startsWith("!дуэль")) {
                handleDuelMessage(event);
                return;
            }
        });
    }

    void handleKarmaMessage(String message, EventUser user) {

        List<String> strings = List.of(message.split(" "));
        Follower follower = followerService.findById(Long.valueOf(user.getId()));
        if (strings.size() == 1) {
            sendMessageToChannelChat("@" + user.getName() + ", твоя карма сейчас: " + follower.getKarma(), CommandPermission.EVERYONE);
            return;
        } else if (strings.size() != 3) {
            return;
        }

        if (follower.getChangedSomeonesKarmaLastTime().plusSeconds(1800).isAfter(Instant.now())) {
            sendMessageToChannelChat("Карму можно давать только раз в полчаса!", CommandPermission.EVERYONE);
            return;
        }
        if (follower.getDisplayName().equalsIgnoreCase(strings.get(1).replaceAll("@", ""))) {
            sendMessageToChannelChat("Ты что, пытался изменить себе карму???  Susge", CommandPermission.EVERYONE);
            return;
        }
        Follower followerToChangeKarma = followerService.findByDisplayName(strings.get(1).replaceAll("@", ""));
        if (followerToChangeKarma == null) {
            sendMessageToChannelChat("Таких людей в фолловерах у нас нет!", CommandPermission.EVERYONE);
            return;
        }

        follower.setChangedSomeonesKarmaLastTime(Instant.now());
        followerService.saveFollower(follower);
        if (strings.get(2).contains("+")) {
            followerToChangeKarma.setKarma(followerToChangeKarma.getKarma() + 1);
        } else if (strings.get(2).contains("-")) {
            followerToChangeKarma.setKarma(followerToChangeKarma.getKarma() - 1);
        }
        followerService.saveFollower(followerToChangeKarma);
        sendMessageToChannelChat("Карма " + followerToChangeKarma.getDisplayName() + " изменена!", CommandPermission.EVERYONE);

    }

    void handleDuelMessageStat(ChannelMessageEvent event) {
        DuelistStats duelist = duelistStatService.getAllByUserId(Long.valueOf(event.getUser().getId()));
        sendMessageToChannelChat("@" + duelist.getFollower().getDisplayName() + " побед в дуэли: " +
                +duelist.getWins() + ", проигрышей: " + duelist.getLoses() + ", побед подряд: "
                +duelist.getWinstreak()+", лучшая серия побед: " + duelist.getWinstreakMax(), CommandPermission.EVERYONE);
    }

    void handleDuelMessage(ChannelMessageEvent event) {
        DuelistStats duelist = duelistStatService.getAllByUserId(Long.valueOf(event.getUser().getId()));
        //Если прошло полчаса, начинаем дуэль по новой
        if (firstDuelistTriesToFindSomeoneTime.plusSeconds(1800).isBefore(Instant.now())) {
            firstDuelistTriesToFindSomeoneTime = Instant.now();
            duelistOne = null;
            System.out.println("creating new duel");
        }
        if (duelistOne == null) {
            duelistOne = duelist;
            sendMessageToChatIgnoreTimer("@" + duelistOne.getFollower().getDisplayName() + " хочет участвовать в дуэли! Ждем второго дуэлянта!");
            firstDuelistTriesToFindSomeoneTime = Instant.now();
            return;
        } else {

            duelistTwo = duelist;

            if(duelistTwo.getFollower().getId().equals(duelistOne.getFollower().getId())){
                sendMessageToChannelChat("@"+duelistTwo.getFollower().getDisplayName()+" пытался " +
                        "подуэлиться с зеркалом! Итог: разбитое зеркало и лицо дуэлянта.",CommandPermission.EVERYONE);
                duelistOne = null;
                duelistTwo = null;
                firstDuelistTriesToFindSomeoneTime = Instant.EPOCH;
                return;
            }

            isHandlingDuel = true;
            sendMessageToChatIgnoreTimer("@" + duelistTwo.getFollower().getDisplayName() + " отвечает на вызов @"
                    + duelistOne.getFollower().getDisplayName() + "! Начинаем дуэль!");

            try {
                sendMessageToChatIgnoreTimer("3...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("2...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("1...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("БАХ!");
                double result = Math.random();
                if (result < 0.5) {
                    sendMessageToChatIgnoreTimer("@" + duelistOne.getFollower().getDisplayName() + " выигрывает дуэль! " +
                            "@" + duelistTwo.getFollower().getDisplayName() + " падает замертво  NOOOO ");
                    sendAdminMessage("/timeout " + duelistTwo.getFollower().getDisplayName() + " 60 ПРОИГРАЛ В ДУЭЛЬ");
                    duelistOne.setWins(duelistOne.getWins() + 1);
                    duelistTwo.setLoses(duelistTwo.getLoses() + 1);
                    duelistOne.setWinstreak(duelistOne.getWinstreak()+1);
                    if(duelistOne.getWinstreakMax()<duelistOne.getWinstreak()){
                        duelistOne.setWinstreakMax(duelistOne.getWinstreak());
                    }

                    duelistTwo.setWinstreak(0);
                } else {
                    sendMessageToChatIgnoreTimer("@" + duelistTwo.getFollower().getDisplayName() + " делает выстрел первым! " +
                            "@" + duelistOne.getFollower().getDisplayName() + " падает замертво  DIESOFCRINGE ");
                    sendAdminMessage("/timeout " + duelistOne.getFollower().getDisplayName() + " 60 ПРОИГРАЛ В ДУЭЛЬ");
                    duelistTwo.setWins(duelistTwo.getWins() + 1);
                    duelistOne.setLoses(duelistOne.getLoses() + 1);
                    duelistTwo.setWinstreak(duelistTwo.getWinstreak()+1);
                    if(duelistTwo.getWinstreakMax()<duelistTwo.getWinstreak()){
                        duelistTwo.setWinstreakMax(duelistTwo.getWinstreak());
                    }

                    duelistOne.setWinstreak(0);
                }
                firstDuelistTriesToFindSomeoneTime = Instant.EPOCH;
                duelistStatService.saveDuelistStats(duelistOne);
                duelistStatService.saveDuelistStats(duelistTwo);
                duelistOne = null;
                duelistTwo = null;
                isHandlingDuel = false;
            } catch (InterruptedException e) {
                isHandlingDuel = false;
                e.printStackTrace();
                sendMessageToChatIgnoreTimer("СМОТРИТЕЛЬ ДУЭЛИ СЛОМАЛСЯ, ДУЭЛЯНТЫ ОСТАЮТСЯ ЖИВЫ!");
            }
        }
    }

    void handleIqMessageNow(ChannelMessageEvent event){
        Iq iq = iqService.findByName(event.getUser().getName());
        if (iq == null){
            handleIqMessage(event);
        } else{
            sendMessageToChannelChat("@" + event.getUser().getName() + ", твой последний замер показал " + iq.getSize() + " iq WHAT ", CommandPermission.EVERYONE);
        }
    }
    void handleIqMessage(@NotNull ChannelMessageEvent event) {
        Iq iq = iqService.findByName(event.getUser().getName());

        if (iq == null) {
            iq = iqService.insertCucumber(new Iq(null, event.getUser().getName(),
                    Instant.now(), (int) Math.floor(MIN_IQ + (int) (Math.random() * (MAX_IQ)))));
            sendMessageToChannelChat("@" + event.getUser().getName() + " Вау! Твой айкью сегодня " + iq.getSize() + "! С первым замером! PepegaAim", CommandPermission.EVERYONE);
            return;
        } else {
            if (iq.getTime().plusSeconds(86400).isAfter(Instant.now())) {
                sendMessageToChannelChat("@" + event.getUser().getName() + " Ежедневный замер айкью уже был! NOPERS Айкьюметр показывал " + iq.getSize() + "!", CommandPermission.EVERYONE);
                return;
            } else {
                int size = iq.getSize();
                iq.setSize((int) Math.floor(Math.random() * (MAX_IQ - MIN_IQ + 1) + MIN_IQ));
                iq = iqService.updateCucumber(iq);

                if (iq.getSize() == MAX_IQ) {
                    sendMessageToChannelChat("@" + event.getUser().getName() + " Нифига себе! Айкьюметр показывает " + iq.getSize() + "! Дальше расти некуда! peepoClap", CommandPermission.EVERYONE);
                    return;
                }

                if (size < iq.getSize()) {
                    sendMessageToChannelChat("@" + event.getUser().getName() + " Вау! Айкьюметр показывает " + iq.getSize() + "! Ты стал умнее! widepeepoHappy До этого было " + size, CommandPermission.EVERYONE);
                } else if (size > iq.getSize()) {
                    sendMessageToChannelChat("@" + event.getUser().getName() + " Мнда. Ты расслабился. Айкьюметр показывает " + iq.getSize() + "!  А было " + size + " widepeepoSad", CommandPermission.EVERYONE);
                } else {
                    sendMessageToChannelChat("@" + event.getUser().getName() + " Айкьюметр показывает " + iq.getSize() + ". Ничего не поменялось !  peepoGiggles", CommandPermission.EVERYONE);
                }
            }
        }

    }

    public void sendAdminMessage(String message) {
        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), message);
    }

    public void sendMessageToChannelChat(String message, CommandPermission messageSentBy) {
        if (message.startsWith("/")) {
            return;
        }
        if (lastMessageTime.plusSeconds(MESSAGE_TIMEOUT_EVERYONE).isAfter(Instant.now())) {
            if (messageSentBy.ordinal() < CommandPermission.VIP.ordinal()) {
                return;
            }
        }
        lastMessageTime = Instant.now();
        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), message);
    }

    public void sendMessageToChatIgnoreTimer(String message) {
        if (message.startsWith("/")) {
            return;
        }
        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), message);
    }
}
