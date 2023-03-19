package com.example.TwitchBot.TwitchModules.channelChat.internalCommands;

import com.example.TwitchBot.entity.Command;
import com.example.TwitchBot.services.DatabaseCommandsService;
import com.github.twitch4j.common.enums.CommandPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseCommandsHandler {
    private final DatabaseCommandsService databaseCommandsService;
    public final static String ADD_NEW_COMMAND = "!addCommand ";
    public final static String DELETE_COMMAND = "!deleteCommand ";
    public final static String EDIT_COMMAND = "!editCommand ";


    public String addNewCommand(String message) {

        Command command = new Command();

        try {
            List<String> stringList = Arrays.asList(message.split(" "));
            List<String> params = new ArrayList<>();
            command.setCommandName(stringList.get(1));
            StringBuilder commandAnswer = new StringBuilder();

            command.setPermissionLevel(CommandPermission.EVERYONE);
            command.setCooldown(1000);

            if (!message.contains("\\%")) {
                for (int i = 2; i < stringList.size(); i++) {
                    commandAnswer.append(stringList.get(i)).append(" ");
                }
                command.setCommandAnswer(commandAnswer.toString());
            } else {
                int paramsPosition = -1;
                for (int i = 2; i < stringList.size(); i++) {
                    if (stringList.get(i).startsWith("\\%")) {
                        paramsPosition = i;
                        break;
                    }
                    commandAnswer.append(stringList.get(i)).append(" ");
                }
                command.setCommandAnswer(commandAnswer.toString());

                params.add(stringList.get(paramsPosition));
                if (paramsPosition != stringList.size() - 1) {
                    params.add(stringList.get(paramsPosition + 1));
                }

                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i).contains("\\%perm")) {
                        if (params.get(i).contains("MODERATOR")) {
                            command.setPermissionLevel(CommandPermission.MODERATOR);
                        } else if (params.get(i).contains("VIP")) {
                            command.setPermissionLevel(CommandPermission.VIP);
                        } else if (params.get(i).contains("SUBSCRIBER")) {
                            command.setPermissionLevel(CommandPermission.SUBSCRIBER);
                        } else {
                            command.setPermissionLevel(CommandPermission.values()[Integer.parseInt(params.get(i).replaceAll("\\D+", ""))]);
                        }
                    }
                    if (params.get(i).contains("\\%cd")) {
                        command.setCooldown(Integer.parseInt(params.get(i).replaceAll("\\D+", "")));
                    }
                }
            }

            if (command.getCommandAnswer().length() == 0) {
                return "Отсутствует ответ";
            }
            if (command.getPermissionLevel().ordinal() > CommandPermission.OWNER.ordinal()) {
                return "Неправильный уровень доступа";
            }
            if (command.getCooldown() < 0) {
                return "Ты че с кулдауном сделал?";
            }
            System.out.println(command);

            if (databaseCommandsService.addNewCommand(command) == null) {
                return "Команда не была добавлена";
            }

            return "Команда была добавлена!";
        } catch (Exception e) {
            return "Команда не была добавлена";
        }

    }

    public Boolean deleteCommand(String message) {
        try {
            String[] messageList = message.split(" ");
            databaseCommandsService.deleteCommandByName(messageList[1]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

//!commandAdd !дилдо Как же хочется тяночку белую не очень высокую \%perm=3
//!commandAdd !дилдо Как же хочется тяночку белую не очень высокую \%perm=3 \&cd=5
//!commandAdd !дилдо Как же хочется тяночку белую не очень высокую
//!commandAdd !дилдо Как же хочется тяночку белую не очень высокую \&cd=3
//!commandAdd !дилдо \&Как же хочется тяночку белую не очень высокую cd=3


