package com.example.TwitchBot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "followers")
public class Follower {
    @Column(name = "user_id")
    @Id
    private Long id;
    @Column(name = "display_name")
    private String displayName;
    private String username;
    @Column(name = "is_followed")
    private Boolean isFollowed;
    private Instant followedFirstTime;
    private Integer karma;
    @Column(name = "changed_someones_karma_last_time")
    private Instant changedSomeonesKarmaLastTime;
}
