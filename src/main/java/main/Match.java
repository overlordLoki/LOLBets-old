package main;

import java.io.*;
import java.util.*;

public class Match implements Serializable {
	private static final long serialVersionUID = 1L;
	public String matchID; //tournament name + current number of matchs played this tournament
	public String date;
	public String name; //blue team + " vs " + red team
	public List<Game> games = new ArrayList<>();
	public List<Team> teams = new ArrayList<>();
	public Match(String MatchID, String Name) {
        matchID = MatchID;
        name = Name;
	}
}
