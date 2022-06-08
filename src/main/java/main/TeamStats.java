package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamStats {
	public List<Team> teams = new ArrayList<>();
	public Tournament tournament;
	public Team team;
	public List<Game> games = new ArrayList<>();
	
	/*
	 * Create a Stats object about a team from a tournament. needs a tournament and team
	 */
	public TeamStats(Tournament t, Team Te) {
		tournament=t;
		teams.addAll(t.teams.values());
		team =Te;
		games = t.gamesTeamPlayed(Te);
	}
	
	public void printLastGame() {
		List<Game> last5Games = team.lastXGames(1);
		for(String s : last5Games.get(0).gameStats()) {
			System.out.println(s);
		}
	}
	
	public void printXGames(int x) {
		List<Game> lastXGames = team.lastXGames(x);
		for(int i =0; i<x; i++) {
			for(String s : lastXGames.get(i).gameStats()) {
				System.out.println(s);
			}
			System.out.println();
		}
	}
	
	public List<Game> GetXGames(int x) {
		List<Game> lastXGames = team.lastXGames(x);
		return lastXGames;
	}
	
	public double getAvgKills() {
		List<Integer> totals = new ArrayList<Integer>(); 
		for(Game g : games) {
			totals.add(g.tKills);
		}
		return (calculateAverage(totals));
	}
	
	public double getAvgDragons() {
		List<Integer> totals = new ArrayList<Integer>(); 
		for(Game g : games) {
			totals.add(g.tDragons);
		}
		return (calculateAverage(totals));
	}
	
	public void printAvgBarons() {
		List<Integer> totals = new ArrayList<Integer>(); 
		for(Game g : games) {
			totals.add(g.tBarons);
		}
		System.out.println(calculateAverage(totals));
	}
	
	
	private double calculateAverage(List <Integer> marks) {
	    return marks.stream()
	                .mapToDouble(d -> d)
	                .average()
	                .orElse(0.0);
	}
	
	public void printTotalKills(Team t) {
		for(Game g : games) {
			System.out.println(g.tKills);
		}
	}
	
	/*
	 * gets a list of total Kills from the games the team played
	 */
	public List<Integer> getTotalKillsList(){
		List<Integer> list = new ArrayList<>();
		for(Game g : tournament.gamesTeamPlayed(team)) {
			list.add(g.tKills);
		}
		return list;
	}
	
	/*
	 * gets a list of total Dragons from the games the team played
	 */
	public List<Integer> getTotalDragonsList(){
		List<Integer> list = new ArrayList<>();
		for(Game g : tournament.gamesTeamPlayed(team)) {
			list.add(g.tDragons);
		}
		return list;
	}
	
	
	/*
	 * prints the total kills from the last x games from the team
	 */
	public void printLastXTotalKills(int x) {
		List<Game> lastXGames = team.lastXGames(x);
		Collections.sort(lastXGames);
		Collections.reverse(lastXGames);
		for(Game g : lastXGames) {
			System.out.println(g.gameName);
			System.out.println(g.tKills);
			System.out.println();
		}
	}
	
	public void setTeam(Team t) {
		team = t;
	}
	
	public void setTournament(Tournament t) {
		tournament = t;
		teams.removeAll(teams);
		teams.addAll(t.teams.values());
		games.removeAll(games);
		games.addAll(t.getGames());
	}
}
