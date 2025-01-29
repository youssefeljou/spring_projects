package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("players.statistics")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatisticsConfiguration {

    private int numberTeams;
    private int numberPlayer;
    private String country;

}
