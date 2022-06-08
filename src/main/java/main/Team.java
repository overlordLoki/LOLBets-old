package main;

import java.util.*;
import java.io.Serializable;
public class Team implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public Region reg;
	public String name;
	public String shortName;
	public List<Game> games = new ArrayList<>();
	
	public Team(String Name, Region Reg) {
		reg = Reg;
		name = Name;
	}
	
	public boolean HasWonGame(Game game) {
		if(game.bTeam==this) {
			return game.bWin;
		}else {
			return game.rWin;
		}
	}
	
	/*
	 * gets a list of the last x games played by the team
	 * 1est in the list is the lastest game played
	 */
	public List<Game> lastXGames(int x){
		List<Game> list = new ArrayList<>();
		if(games.size()==0) {return null;}
		int z = games.size()-1;
		if(games.size()<x) {
			x=games.size();
		}
		for(int i = 0; i<x;i++) {
			list.add(games.get(z));
			z--;
		}
		Collections.reverse(list);
		return list;
	}
	
}
