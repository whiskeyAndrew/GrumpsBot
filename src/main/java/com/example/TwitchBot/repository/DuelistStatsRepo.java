package com.example.TwitchBot.repository;

import com.example.TwitchBot.entity.DuelistStats;
import com.example.TwitchBot.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DuelistStatsRepo extends JpaRepository<DuelistStats, Long> {
     Optional<DuelistStats> getDuelistStatsByFollower(Follower follower);

}
