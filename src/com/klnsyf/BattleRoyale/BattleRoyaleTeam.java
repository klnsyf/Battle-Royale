package com.klnsyf.BattleRoyale;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class BattleRoyaleTeam extends BattleRoyaleOption {
	ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
	Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

	boolean createPlayerTeam(String player) {
		if (scoreboard.getEntryTeam(player) != null) {
			Team team = scoreboard.registerNewTeam(player);
			team.addEntry(player);
			return true;
		} else {
			return false;
		}
	}

	boolean joinPlayerTeam(String player, String team) {
		if (scoreboard.getEntryTeam(team) != null) {
			scoreboard.getEntryTeam(team).addEntry(player);
			return true;
		} else {
			return false;
		}
	}

	void randomTeam(ArrayList<Player> players) {
		for (Team team : scoreboard.getTeams()) {
			team.unregister();
		}
		Team red = scoreboard.registerNewTeam("Red");
		red.setPrefix("[Red]");
		Team blue = scoreboard.registerNewTeam("Blue");
		blue.setPrefix("[Blue]");
		ArrayList<Player> playerList = players;
		for (int index = 0; index < players.size(); index++) {
			switch (index % 2) {
			case 0:
				red.addEntry(playerList.get((new Random()).nextInt(playerList.size())).getName());
				break;
			case 1:
				blue.addEntry(playerList.get((new Random()).nextInt(playerList.size())).getName());
				break;
			}
		}
	}
}
