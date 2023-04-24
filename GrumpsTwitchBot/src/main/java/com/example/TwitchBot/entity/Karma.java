package com.example.TwitchBot.entity;

import lombok.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Karma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    Follower follower;
    Long karma;
    @Column(name = "changed_someones_karma_last_time")
    Instant changedSomeoneKarmaLastTime;

}
