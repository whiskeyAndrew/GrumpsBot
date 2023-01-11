package com.example.TwitchBot.services;

import com.example.TwitchBot.entity.Command;
import com.example.TwitchBot.repository.DatabaseCommandsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class DatabaseCommandsService {
    private final DatabaseCommandsRepo jpa;

    public Boolean isCommandExists(String channelName){
        return jpa.existsCommandByCommandName(channelName);
    }
    public List<Command> getAllCommands(){
        return jpa.findAll();
    }

    public Command getCommandByName(String commandName){
        return jpa.findFirstByCommandName(commandName);
    }

    public Command addNewCommand(Command command){
        if(jpa.existsCommandByCommandName(command.getCommandName())){
            return null;
        }
        return jpa.saveAndFlush(command);
    }
    public void deleteCommandByName(String commandName){
        jpa.deleteByCommandName(commandName);
    }

}
