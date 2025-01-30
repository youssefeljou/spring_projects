package com.example.demo.controller;

import com.example.demo.config.ApiCall;
import com.example.demo.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/team-players")
@RequiredArgsConstructor
public class TeamController {

    private final ApiCall apiCall;

    @GetMapping("/team/{from}/player/{to}")
    public Team teamWithPlayer(@PathVariable String from,@PathVariable String to)
    {
        Map<String,String> urlAttribute=new HashMap<>();
        urlAttribute.put("from",from);
        urlAttribute.put("to",to);
        ResponseEntity<Team> teamResponseEntity=new RestTemplate().getForEntity(
                "http://localhost:8888/football-player/buy/{from}/player/{to}",
                Team.class,
                urlAttribute
        );
        Team teamResponse=teamResponseEntity.getBody();
        Team team=new Team(teamResponse.getId(),teamResponse.getFromTeam(),teamResponse.getToTeam(),teamResponse.getMoneyTeam(),"null","40");
        return team;
    }
    @GetMapping("/team2/{from}/player/{to}")
    public Team teamWithPlayer2(@PathVariable String from,@PathVariable String to)
    {

        Team teamResponse=apiCall.getFootBallPlayer(from,to);
        Team team=new Team(teamResponse.getId(),teamResponse.getFromTeam(),teamResponse.getToTeam(),teamResponse.getMoneyTeam(),"null","40");
        return team;
    }
}
