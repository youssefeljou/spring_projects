package com.example.demo.dao;

import com.example.demo.model.PlayerTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballRepository extends JpaRepository<PlayerTeam,Long>
{
        PlayerTeam findByFromTeamAndToTeam(String fromTeam,String toTeam);
}
