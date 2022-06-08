package main;

import java.io.*;
import java.util.*;

public class Tournament implements Serializable {
	private static final long serialVersionUID = 1L;
	public String tournamentID;
	private List<Match> matches = new ArrayList<>();
	public int numOfGames= 0;
	public Map<String,Team> teams = new HashMap<>();
	public Tournament(String Name) {
		tournamentID = Name;
		
	}
	public void addMatch(Match m,int numOfGamesInMatch){
		matches.add(m);
		numOfGames += numOfGamesInMatch;
	}
	public List<Match> getMatchs() {
		return matches;
	}
	public List<Game> gamesTeamPlayed(Team t) {
		List<Game> games = new ArrayList<>();
		for(Match m : matches) {
			if(m.teams.contains(t)) {
				games.addAll(m.games);
			}
		}
		return games;
	}
	
	public List<Game> getGames(){
		List<Game> games = new ArrayList<>();
		for(Match m : matches) {
			games.addAll(m.games);
		}
		Collections.sort(games);
		return games;
	}
	
	public int[] getWinLossRate(Team team) {
		List<Game> games= gamesTeamPlayed(team);
		int wins =0;
		int loses =0;
		for(Game g : games) {
			if(g.winningTeam() == team) {
				wins++;
			}
			else {
				loses++;
			}
		}
		int[] winLose = new int[2];
		winLose[0]=wins;
		winLose[1]=loses;
		return winLose;
	}
	
}
