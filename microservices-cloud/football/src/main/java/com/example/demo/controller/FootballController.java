package com.example.demo.controller;

import com.example.demo.dao.FootballRepository;
import com.example.demo.model.PlayerTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/football-player")
@RequiredArgsConstructor
public class FootballController {


    private final FootballRepository footballRepository;
    @GetMapping("/buy/{from}/player/{to}")
    public PlayerTeam buyPlayer(@PathVariable String from,@PathVariable String to)
    {

        return footballRepository.findByFromTeamAndToTeam(from,to);
    }
}
