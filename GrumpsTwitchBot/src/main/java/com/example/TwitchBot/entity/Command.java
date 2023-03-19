package com.example.TwitchBot.entity;

import com.github.twitch4j.common.enums.CommandPermission;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commands")
@ToString
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String commandName;
    private String commandAnswer;
    private CommandPermission permissionLevel;
    private Integer cooldown;

}
