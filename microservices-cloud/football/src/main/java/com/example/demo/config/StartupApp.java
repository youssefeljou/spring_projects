package com.example.demo.config;

import com.example.demo.dao.FootballRepository;
import com.example.demo.model.PlayerTeam;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupApp implements CommandLineRunner {

    private final FootballRepository footballRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (footballRepository.findAll().isEmpty()) {
            PlayerTeam playerTeam = new PlayerTeam();
            playerTeam.setFromTeam("ZAMALEK");
            playerTeam.setMoneyPlayer(500);
            playerTeam.setToTeam("ALAHLY");
            playerTeam.setMoneyTeam(600);
            footballRepository.save(playerTeam);

        }
    }
}

