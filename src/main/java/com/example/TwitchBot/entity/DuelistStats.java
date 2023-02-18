package com.example.TwitchBot.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "duelist_stat")
@NoArgsConstructor
@AllArgsConstructor

public class DuelistStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Follower follower;

    private Integer wins;
    private Integer loses;


}
