package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseCommandsRepo extends JpaRepository<Command,Long> {
    Command findFirstByCommandName(String commandName);
    Boolean existsCommandByCommandName(String commandName);
    void deleteByCommandName(String commandName);
}
