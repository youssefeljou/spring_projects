package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Team {


    private long id;
    private String fromTeam;
    private String toTeam;
    private int moneyTeam;
    private String note;
    private String numberTeam;

}
