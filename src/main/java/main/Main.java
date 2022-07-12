package main;

import java.io.*;
import java.util.*;

public class Main {
	public Map<String,Region> regions = new HashMap<>();
	public Map<String,Tournament> Tournaments = new HashMap<>();
	public Map<String,Team> allTeams = new HashMap<>();
	public Set<String> oldLinks = new HashSet<>();
	public static GUI gui;
	
	public static void main(String[] args) {
		Main m = new Main();
		m.loadData();
		gui = new GUI(m);
		m.run();
	}
	
	public void run() {
		
	}

	//TODO: get 9 tournament working
	//TODO: get gold fixed, get id working.

	
	/*
	 * checks LPL, LEC , LCK and LCS for any new matchs and adds them.
	 */
	public void updateMatchs() {
		
		List<String> list = new ArrayList<String>();
		String LPL = "https://gol.gg/tournament/tournament-matchlist/LPL%20Spring%20Playoffs%202022/";
		list.add(LPL);
		String LEC = "https://gol.gg/tournament/tournament-matchlist/LEC%20Spring%20Playoffs%202022/";
		list.add(LEC);
		String LCK = "https://gol.gg/tournament/tournament-matchlist/LCK%20Spring%20Playoffs%202022/";
		list.add(LCK);
		String LCS = "https://gol.gg/tournament/tournament-matchlist/LCS%20Spring%20Playoffs%202022/";
		list.add(LCS);
		String msi = "https://gol.gg/tournament/tournament-ranking/MSI%202022/";
		list.add(msi);
		for(String reg : list) {
			Miner miner = new Miner(this);
			String s = miner.ScrapTournament(reg);
			gui.textArea.append(s+"\n");
			saveData();
		}
		gui.textArea.append("finsh Mining \n");
		saveData();
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		ArrayList<Object> regionObjects = new ArrayList<>();
		try{
			FileInputStream dataFile = new FileInputStream("data.ser"); //find the file
			ObjectInputStream dataFileAsObject = new ObjectInputStream(dataFile);
			regionObjects = (ArrayList<Object>) dataFileAsObject.readObject();
			dataFileAsObject.close();
			dataFile.close(); 
		}   catch(IOException e) {e.printStackTrace();}
		    catch(ClassNotFoundException e) {e.printStackTrace();}

		for(Object o: regionObjects) {
			Region r = (Region) o;
			regions.put(r.regionID,r);
		}
		//syn the lists up
		for(Region reg : regions.values()) {
			allTeams.putAll(reg.teams);
			Tournaments.putAll(reg.Tournaments);
		}
		//load old links set
		ArrayList<Object> data = new ArrayList<>();
		try{
			FileInputStream dataFile = new FileInputStream("linksdata.ser");
			ObjectInputStream dataFileAsObject = new ObjectInputStream(dataFile);
			data = (ArrayList<Object>) dataFileAsObject.readObject();
			dataFileAsObject.close();
			dataFile.close(); 
		}   catch(IOException e) {e.printStackTrace();}
		    catch(ClassNotFoundException e) {e.printStackTrace();}

		for(Object o: data) {
			String s = (String) o;
			oldLinks.add(s);
		}
		
	}

	public void saveData() {
		//all the data to be saved (missing oldlinks)
		ArrayList<Object> dataSet = new ArrayList<>();
		for(Region reg : regions.values()) {
			dataSet.add(reg);
		}
		try {
			FileOutputStream fileout = new FileOutputStream("data.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileout);
			out.writeObject(dataSet);
			out.close();
			fileout.close();
		}catch(IOException e) {e.printStackTrace();}
		//old links set to be saved
		ArrayList<Object> data = new ArrayList<>();
		for(String s : oldLinks) {
			data.add(s);
		}
		try {
			FileOutputStream fileout = new FileOutputStream("linksdata.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileout);
			out.writeObject(data);
			out.close();
			fileout.close();
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public void makeDataFromText(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String r = sc.nextLine(); //name of region
				Region reg = regions.get(r);
				if(reg == null) {
					reg = new Region(r);
					regions.put(r, reg);
				}
				Tournament tourn = new Tournament(sc.nextLine()); //region + spring tournament
				Tournaments.put(tourn.tournamentID, tourn);
				reg.Tournaments.put(tourn.tournamentID,tourn);
				int num = Integer.parseInt(sc.nextLine()); //num of Teams
				for(int i =0; i < num; i++) {
					String name = sc.nextLine(); //name of team
					Team t = new Team(name, reg);
					reg.teams.put(name,t);
				}
				tourn.teams = reg.teams;
			}
			sc.close();
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
	
	public void loadGamesForTeams() {
		List<Game> games = new ArrayList<>();
		for(Tournament t : Tournaments.values()) {
			for(Match m : t.getMatchs()) {
				games.addAll(m.games);
			}
		}
		for(Team t : allTeams.values()) {
			for(Game g : games) {
				if(g.teams.contains(t)) {
					t.games.add(g);
				}
			}
		}
		saveData();
	}

	//load all games into a csv file
	public void loadGames() {
		List<Game> games = new ArrayList<>();
		for(Tournament t : Tournaments.values()) {
			for(Match m : t.getMatchs()) {
				games.addAll(m.games);
			}
		}
		try {
			FileWriter writer = new FileWriter("games.csv");
			writer.append("Tournament,Match,gameID,week,bTeam,rTeam,date,link,Time,bKills,rKills,toalKills,bTowers,rTowers,"+
			"totalTowers,bDragons,rDragons,totalDragons,bBarons,rBarons,totalBarons,bGold,rGold,totalGold,winner\n");
			for(Game g : games) {
				writer.append(g.season.tournamentID+","+g.match.name+","+g.id+","+g.week+","+g.bTeam.name+","+g.rTeam.name+","+
				g.date+","+g.link+","+g.time+","+g.bKills+","+g.rKills+","+(g.bKills+g.rKills)+","+
				g.bTowers+","+g.rTowers+","+(g.bTowers+g.rTowers)+","+g.bDragons+","+g.rDragons+","+
				(g.bDragons+g.rDragons)+","+g.bBarons+","+g.rBarons+","+(g.bBarons+g.rBarons)+","+g.bGold+","+g.rGold+","+(g.bGold+g.rGold)+","+g.winningTeam().name+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	//add more tournaments
	public void addTournament(String tournName ) {
		Tournament t = new Tournament(tournName);
		Tournaments.put(t.tournamentID,t);
		saveData();
	}
	
}
