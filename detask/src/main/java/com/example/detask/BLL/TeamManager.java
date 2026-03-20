package com.example.detask.BLL;

import com.example.detask.BE.Team;
import com.example.detask.DLL.TeamDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamManager {

    private final TeamDAO repo;

    public TeamManager(TeamDAO repo) {
        this.repo = repo;
    }

    public List<Team> getAllTeams() {
        return repo.getAllTeams();
    }

    public Team getById(int id) {
        return repo.getById(id);
    }
}
