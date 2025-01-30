package com.example.demo.config;

import com.example.demo.model.Team;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "football",url = "localhost:8888")
public interface ApiCall {

    @GetMapping("/football-player/buy/{from}/player/{to}")
    public Team getFootBallPlayer(@PathVariable String from, @PathVariable String to);
}
