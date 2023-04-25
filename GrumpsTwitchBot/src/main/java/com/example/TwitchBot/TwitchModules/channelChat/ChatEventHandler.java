package com.example.TwitchBot.TwitchModules.channelChat;

import com.example.TwitchBot.TwitchModules.channelInfo.ChannelFollowers;
import com.example.TwitchBot.TwitchModules.channelChat.internalCommands.DatabaseCommandsHandler;
import com.example.TwitchBot.config.TwitchClientConfig;
import com.example.TwitchBot.entity.*;
import com.example.TwitchBot.repository.KarmaRepo;
import com.example.TwitchBot.services.*;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.helix.domain.BanUserInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

//В идеале бы бОльшую часть кода раскидать по сервисам отсюда, к примеру обработку IQ или Karma сообщений стоило бы перекинуть на сервисы
//Но я поздновато об этом подумал и времени пока нет рефакторить, оставлю возможно на потом
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventHandler extends Thread {
    private final KarmaRepo karmaRepo;
    private final ChannelFollowers channelFollowers;
    private final DBTablesHandler dbTablesHandler;

    //Logic Staff
    private final DatabaseCommandsHandler dataBaseCommandsHandler;
    private final DatabaseCommandsService databaseCommandsService;
    private final TwitchClient twitchClient;
    private final TwitchClientConfig twitchClientConfig;
    private final FollowerService followerService;
    private final DuelistStatService duelistStatService;
    private final KarmaService karmaService;
    //Timeout, need to prevent chat spam from bot (maybe not working right now)
    private final static int MESSAGE_TIMEOUT_EVERYONE = 1;

    Instant lastMessageTime;

    //Iq checker
    private final IqService iqService;
    private final static int MAX_IQ = 200;
    private final static int MIN_IQ = 1;

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

    //Пока не работает
    @PreDestroy
    private void appCloseMessage() {
        sendMessageToChatIgnoreTimer("Я УПАЛ");
    }

    @Override
    public void run() {
        log.info("Started listening for channel chat");
        twitchClient.getChat().sendMessage(twitchClientConfig.getChannelName(), "@dieorpie Я ПОДНЯЛСЯ!");
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] "
                    + event.getUser().getName() + ": " + event.getMessage());

            if (isHandlingDuel) {
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

            if (event.getMessage().startsWith("!ftm")) {
                Follower follower = followerService.findById(Long.parseLong(event.getUser().getId()));
                if (follower == null) {
                    return;
                }

                Instant userFollowed = follower.getFollowedAt();
                ZonedDateTime dateTime = userFollowed.atZone(ZoneId.systemDefault());
                long daysFollowed = userFollowed.until(Instant.now(), ChronoUnit.DAYS);
                String answer = "@" + event.getUser().getName() + " у нас с " + dateTime.getDayOfMonth() + "." + dateTime.getMonth().getValue() + "." + dateTime.getYear() + " (" + daysFollowed + " days) ";
                sendMessageToChannelChat(answer, CommandPermission.EVERYONE);
            }

            if (event.getMessage().startsWith("бан мне")) {
                sendMessageToChannelChat("ладно", CommandPermission.EVERYONE);
                banUserByBot(event.getUser().getId(), 60, "Попросил самобан");
                return;
            }

            if (event.getMessage().startsWith("!rebase")) {
                if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    handleRebaseMessage(event);
                }
                return;
            }
            if (event.getMessage().startsWith("SoCute")) {
                sendMessageToChannelChat("SoCute SoCute SoCute SoCute SoCute SoCute", CommandPermission.EVERYONE);
                return;
            }
            if (event.getMessage().contains("ГУС") || event.getMessage().contains("ГYС")) {
                deleteMessageFromChat(event);
                sendMessageToChannelChat("peepoMop УБИРАЮ peepoMop ЕБУЧИХ peepoMop ГУСЕЙ peepoMop", CommandPermission.EVERYONE);
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
                sendMessageToChannelChat("!iq lock блокирует или разблокирует нынешний твой IQ. Если он заблокирован, то команда !iq не будет обновлять твое IQ",CommandPermission.EVERYONE);
                return;
            }

            if (event.getMessage().startsWith("!iq") || event.getMessage().startsWith("!айкью")) {
                handleIqMessage(event);
                return;
            }

            if (event.getMessage().startsWith("!карма")) {
                handleKarmaMessage(event);
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

    void handleKarmaMessage(ChannelMessageEvent event) {
        List<String> message = new ArrayList<>(List.of(event.getMessage().split(" ")));

        if (message.size() == 1) {
            Follower follower = followerService.findById(Long.valueOf(event.getUser().getId()));
            if (follower == null) {
                sendMessageToChannelChat("@" + event.getUser().getName() + ", я тебя не знаю  PepeCringe ", CommandPermission.EVERYONE);
            }
            Karma karmaUser = karmaService.findByFollower(follower);
            if (karmaUser == null) {
                karmaService.addNewKarmaEntity(follower);
            } else {
                sendMessageToChannelChat("@" + karmaUser.getFollower().getUsername() + ", твоя карма сейчас: " + karmaUser.getKarma().toString(), CommandPermission.EVERYONE);
            }
        } else if (message.size() == 2) {
            String userToGetKarmaName = message.get(1);
            if (userToGetKarmaName.contains("@")) {
                userToGetKarmaName = message.get(1).replace("@", "");
            }
            Follower userToGetKarma = followerService.findByDisplayName(userToGetKarmaName);
            if (userToGetKarma == null) {
                sendMessageToChannelChat("Пользователь " + userToGetKarmaName + " не найден, упс!", CommandPermission.EVERYONE);
                return;
            }
            Karma usersKarma = karmaService.findByFollower(userToGetKarma);
            if (usersKarma == null) {
                sendMessageToChannelChat("Пользователь " + userToGetKarmaName + " не найден в базе кармы, упс!", CommandPermission.EVERYONE);
                return;
            }
            sendMessageToChannelChat("У " + userToGetKarmaName + " " + usersKarma.getKarma().toString() + " кармы!", CommandPermission.EVERYONE);


        } else if (message.size() == 3) {
            String action = message.get(2);
            if (!action.contains("+") && !action.contains("-")) {
                sendMessageToChannelChat("@" + event.getUser().getName() + " для изменения кармы используй + или - третьим параметром!", CommandPermission.EVERYONE);
                return;
            }

            Karma userToSetKarmaTimer = karmaService.findByFollower(followerService.findById(Long.parseLong(event.getUser().getId())));
            if (Instant.now().isBefore(userToSetKarmaTimer.getChangedSomeoneKarmaLastTime().plusSeconds(600))) {
                sendAdminMessage("@" + event.getUser().getName() + ", ты уже недавно ставил карму!");
                return;
            }

            String userToAddKarmaName = message.get(1);
            if (userToAddKarmaName.contains("@")) {
                userToAddKarmaName = message.get(1).replace("@", "");
            }

            if (userToAddKarmaName.equalsIgnoreCase(event.getUser().getName())) {
                sendMessageToChannelChat("@" + event.getUser().getName() + ", лезешь в свою карму?", CommandPermission.EVERYONE);
                return;
            }

            Follower userToAddKarma = followerService.findByDisplayName(userToAddKarmaName);
            if (userToAddKarma == null) {
                sendMessageToChannelChat("Пользователь " + userToAddKarmaName + " не найден, упс!", CommandPermission.EVERYONE);
                return;
            }

            Karma usersKarma = karmaService.findByFollower(userToAddKarma);
            if (usersKarma == null) {
                sendMessageToChannelChat("Пользователь " + userToAddKarmaName + " не найден в базе кармы, упс!", CommandPermission.EVERYONE);
                return;
            }

            if (action.contains("+")) {
                usersKarma.setKarma(usersKarma.getKarma() + 1);
                sendMessageToChannelChat(userToAddKarmaName + " получил плюсик к карме!", CommandPermission.EVERYONE);
            } else if (action.contains("-")) {
                usersKarma.setKarma(usersKarma.getKarma() - 1);
                sendMessageToChannelChat(userToAddKarmaName + " словил минус к карме", CommandPermission.EVERYONE);
            }
            userToSetKarmaTimer.setChangedSomeoneKarmaLastTime(Instant.now());
            karmaRepo.save(userToSetKarmaTimer);
            karmaRepo.save(usersKarma);
        }
    }

    void handleRebaseMessage(ChannelMessageEvent event) {
        List<String> message = List.of(event.getMessage().split(" "));
        if (message.size() == 1) {
            return;
        }

        if (message.size() > 2) {
            return;
        }

        String arg = message.get(1);
        switch (arg) {
            case "f": {
                dbTablesHandler.rebaseFollowersDatabase();
                break;
            }
            case "karma": {
                dbTablesHandler.rebaseKarmaDatabase();
                break;
            }
            case "all": {
                dbTablesHandler.rebaseFollowersDatabase();
                dbTablesHandler.rebaseKarmaDatabase();
                break;
            }
            default: {
                return;
            }
        }
    }

    void handleDuelMessageStat(ChannelMessageEvent event) {
        DuelistStats duelist = duelistStatService.getAllByUserId(Long.valueOf(event.getUser().getId()));
        sendMessageToChannelChat("@" + duelist.getFollower().getUsername() + " побед в дуэли: " +
                +duelist.getWins() + ", проигрышей: " + duelist.getLoses() + ", побед подряд: "
                + duelist.getWinstreak() + ", лучшая серия побед: " + duelist.getWinstreakMax(), CommandPermission.EVERYONE);
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
            sendMessageToChatIgnoreTimer("@" + duelistOne.getFollower().getUsername() + " хочет участвовать в дуэли! Ждем второго дуэлянта!");
            firstDuelistTriesToFindSomeoneTime = Instant.now();
            return;
        } else {

            duelistTwo = duelist;

            if (duelistTwo.getFollower().getUserId().equals(duelistOne.getFollower().getUserId())) {
                sendMessageToChannelChat("@" + duelistTwo.getFollower().getUsername() + " пытался " +
                        "подуэлиться с зеркалом! Итог: разбитое зеркало и лицо дуэлянта.", CommandPermission.EVERYONE);
                duelistOne = null;
                duelistTwo = null;
                firstDuelistTriesToFindSomeoneTime = Instant.EPOCH;
                return;
            }

            isHandlingDuel = true;
            sendMessageToChatIgnoreTimer("@" + duelistTwo.getFollower().getUsername() + " отвечает на вызов @"
                    + duelistOne.getFollower().getUsername() + "! Начинаем дуэль!");

            try {
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("3...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("2...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("1...");
                Thread.sleep(1000);
                sendMessageToChatIgnoreTimer("БАХ!");
                double result = Math.random();
                if (result < 0.5) {
                    sendMessageToChatIgnoreTimer("@" + duelistOne.getFollower().getUsername() + " выигрывает дуэль! " +
                            "@" + duelistTwo.getFollower().getUsername() + " падает замертво  NOOOO ");
                    banUserByBot(duelistTwo.getFollower().getUserId().toString(), 60, "Проиграл в дуэль");
                    duelistOne.setWins(duelistOne.getWins() + 1);
                    duelistTwo.setLoses(duelistTwo.getLoses() + 1);
                    duelistOne.setWinstreak(duelistOne.getWinstreak() + 1);
                    if (duelistOne.getWinstreakMax() < duelistOne.getWinstreak()) {
                        duelistOne.setWinstreakMax(duelistOne.getWinstreak());
                    }

                    duelistTwo.setWinstreak(0);
                } else {
                    sendMessageToChatIgnoreTimer("@" + duelistTwo.getFollower().getUsername() + " делает выстрел первым! " +
                            "@" + duelistOne.getFollower().getUsername() + " падает замертво  DIESOFCRINGE ");
                    banUserByBot(duelistOne.getFollower().getUserId().toString(), 60, "Проиграл в дуэль");
                    duelistTwo.setWins(duelistTwo.getWins() + 1);
                    duelistOne.setLoses(duelistOne.getLoses() + 1);
                    duelistTwo.setWinstreak(duelistTwo.getWinstreak() + 1);
                    if (duelistTwo.getWinstreakMax() < duelistTwo.getWinstreak()) {
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

    void handleIqMessage(ChannelMessageEvent event) {
        Iq iq = iqService.getIqEntityByUserId(Long.valueOf(event.getUser().getId()));

        if (iq == null) {
            Integer newIq = (int) Math.floor(MIN_IQ + (int) (Math.random() * (MAX_IQ)));
            Follower follower = followerService.findById(Long.valueOf(event.getUser().getId()));
            if (follower == null) {
                sendAdminMessage("Тебя нет в списке фолловеров! @" + event.getUser().getName());
                return;
            }

            iq = iqService.insertIqEntity(new Iq(null, followerService.findById(Long.valueOf(event.getUser().getId())),
                    newIq, Instant.now(), false));
            sendMessageToChannelChat("@" + event.getUser().getName() + " Вау! Твой айкью сегодня " + iq.getIq() + "! С первым замером! PepegaAim", CommandPermission.EVERYONE);
            return;
        }

        if (event.getMessage().contains("lock") || event.getMessage().contains("закрепить")) {
            if (iq.getIsLocked()) {
                iq.setIsLocked(false);
                sendMessageToChannelChat("@" + event.getUser().getName() + ", IQ открыт!", CommandPermission.EVERYONE);
            } else {
                iq.setIsLocked(true);
                sendMessageToChannelChat("@" + event.getUser().getName() + ", IQ закрыт!", CommandPermission.EVERYONE);
            }
            iqService.save(iq);
            return;
        }

        if (iq.getIsLocked()) {
            sendMessageToChannelChat("@" + event.getUser().getName() + ", твой последний замер показал " + iq.getIq() + " iq WHAT (IQ закреплен)", CommandPermission.EVERYONE);
            return;
        }

        if (iq.getTime().plusSeconds(48200).isAfter(Instant.now())) {
            sendMessageToChannelChat("@" + event.getUser().getName() + " Ежедневный замер айкью уже был! NOPERS Айкьюметр показывал " + iq.getIq() + "!", CommandPermission.EVERYONE);
            return;
        }

        int size = iq.getIq();
        iq.setIq((int) Math.floor(Math.random() * (MAX_IQ - MIN_IQ + 1) + MIN_IQ));
        iq = iqService.updateIqValue(iq);

        if (iq.getIq() == MAX_IQ) {
            sendMessageToChannelChat("@" + event.getUser().getName() + " Нифига себе! Айкьюметр показывает " + iq.getIq() + "! Дальше расти некуда! peepoClap", CommandPermission.EVERYONE);
            return;
        }

        if (size < iq.getIq()) {
            sendMessageToChannelChat("@" + event.getUser().getName() + " Вау! Айкьюметр показывает " + iq.getIq() + "! Ты стал умнее! widepeepoHappy До этого было " + size, CommandPermission.EVERYONE);
        } else if (size > iq.getIq()) {
            sendMessageToChannelChat("@" + event.getUser().getName() + " Мнда. Ты расслабился. Айкьюметр показывает " + iq.getIq() + "!  А было " + size + " widepeepoSad", CommandPermission.EVERYONE);
        } else {
            sendMessageToChannelChat("@" + event.getUser().getName() + " Айкьюметр показывает " + iq.getIq() + ". Ничего не поменялось !  peepoGiggles", CommandPermission.EVERYONE);
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

    public void deleteMessageFromChat(ChannelMessageEvent event) {
        try {
            twitchClient.getHelix().deleteChatMessages(twitchClientConfig.getBotTokenAccess(), event.getChannel().getId(), twitchClientConfig.getBotNameToId(), event.getMessageEvent().getMessageId().orElse(null)).execute();
        } catch (Exception e) {
            log.error("НЕУДАЧНАЯ ПОПЫТКА УДАЛИТЬ СООБЩЕНИЕ" + event.getUser().getName());
        }
    }

    public void banUserByBot(String id, Integer duration, String reason) {
        try {
            twitchClient.getHelix().banUser(twitchClientConfig.getBotTokenAccess(), twitchClientConfig.getChannelId(), twitchClientConfig.getBotNameToId(),
                    BanUserInput.builder().build().withUserId(id).withDuration(duration).withReason(reason)).execute();
        } catch (Exception e) {
            log.error("Tried to ban " + id + reason + " without success");
        }
    }
}
