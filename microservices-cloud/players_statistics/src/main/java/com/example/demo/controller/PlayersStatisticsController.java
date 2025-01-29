package com.example.demo.controller;

import com.example.demo.config.PlayerStatisticsConfiguration;
import com.example.demo.model.PlayersStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players-management")
@RequiredArgsConstructor
public class PlayersStatisticsController {

    private final PlayerStatisticsConfiguration playerStatisticsConfiguration;

    @GetMapping("/statistics")
    public PlayersStatistics getPlayersStatistics()
    {
        return new PlayersStatistics(
                playerStatisticsConfiguration.getNumberTeams(),
                playerStatisticsConfiguration.getNumberPlayer(),
                playerStatisticsConfiguration.getCountry());
    }
}
