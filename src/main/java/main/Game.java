package main;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Game implements Serializable, Comparable<Game>{
	private static final long serialVersionUID = 1L;
	public Team     bTeam, rTeam;
	public List<Team> teams = new ArrayList<>();
	public int      bKills, bTowers, bDragons, bBarons,
	                rKills, rTowers, rDragons, rBarons,
	                tKills, tTowers, tDragons, tBarons, week, id, numInMatch, time;
	public Tournament   season;
	public double   bGold, rGold, tGold;
	public String   link, date, gameName;
	public boolean  rWin, bWin;
	public Match match;
	public Game(Team BTeam, int BKills, int BTowers, int BDragons, int BBarons,
	            Team RTeam, int RKills, int RTowers, int RDragons, int RBarons,
	            double BGold, double RGold, String Time, String Link, String Date,
	            int Week, int ID, boolean Rwin, boolean Bwin, int NumInMatch, Tournament t, Match m){

		bTeam = BTeam;
		bKills = BKills;
		bTowers = BTowers;
		bDragons = BDragons;
		bBarons = BBarons;

		rTeam = RTeam;
		rKills = RKills;
		rTowers = RTowers;
		rDragons = RDragons;
		rBarons = RBarons;

		tKills = BKills + RKills;
        tTowers = BTowers + RTowers;
        tDragons = BDragons + RDragons;
        tBarons = BBarons + RBarons;

        tGold = BGold + RGold;
        
		time = Integer.parseInt(Time.split(":")[0])*60+Integer.parseInt(Time.split(":")[1]);
        link = Link;
        
        date = Date;
        week = Week;
        numInMatch = NumInMatch;
        season = t;
        match = m;
        gameName= bTeam.name+" vs "+rTeam.name+ " "+date;
        teams.add(BTeam);
        teams.add(RTeam);
        id = ID;
	}
	

	public String timeToString() {
		return time/60+":"+time%60;
	}
	
	public List<String> gameStats(){
		List<String> list = new ArrayList<>();
		list.add(gameName);
		list.add("Date: "+date);
		list.add("Winner: "+winningTeam().name);
		list.add("Total Game Time: "+ timeToString());
		list.add("Total Kills: "+ tKills);
		list.add("Total Towers: "+ tTowers);
		list.add("Total Dragons: "+ tDragons);
		list.add("Total Barons: "+ tBarons);
		return list;
	}
	
	@Override
	public String toString() {
		return "Game [gameName=" + gameName + "]";
	}


	public Team winningTeam() {
		if(bWin) {
			return bTeam;
		}
		else return rTeam;
	}


	@Override
	public int compareTo(Game o) {
		if(this.id<o.id) {return -1;}
		if(this.id>o.id) {return 1;}
		return 0;
	}
}
