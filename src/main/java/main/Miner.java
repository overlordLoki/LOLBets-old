package main;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Miner {
	private Set<String> oldLinks = new HashSet<>();
	public Map<String,Team> teams = new HashMap<>();
	public Map<String,Tournament> Tournaments = new HashMap<>();
	public Main main;
	public int count=0;

	public Miner(Main m) {
		main = m;
		oldLinks = m.oldLinks;
		teams = m.allTeams;
		Tournaments = m.Tournaments;
	}

	public String ScrapTournament(String URL) {
		List<String> matchsfound = new ArrayList<String>();
		matchsfound = getMatchList(URL);
		for(String link : matchsfound) {
			if(oldLinks.contains(link)) {
				continue;
			}
			makeMatch(link);
			oldLinks.add(link);
			main.oldLinks.add(link);
		}
		String s = "Total new games "+count;
		return s;
	}

	/*
	 * makes a Match from the given link
	 */
	public void makeMatch(String link) {
		try {
			//make the URl from the link
			String URL = "https://gol.gg"+link;
			//make a doc from the URL
			Document doc = null;
			try {doc = Jsoup.connect(URL).get();} catch (IOException e) {e.printStackTrace();}
			//Element e = doc.getElementsByClass("col-cadre").first();

			//tournament it belongs to
			String[] regionAndTourmament = doc.getElementsByClass("col-12 col-sm-7").first().text().split(" ");
			String stringtourmament = 
					regionAndTourmament[0]+" "+regionAndTourmament[1]+" "+regionAndTourmament[2];
			Tournament tourmament = Tournaments.get(stringtourmament);

			//make a list of links for each game in the match
			Elements links = doc.getElementsByClass("collapse navbar-collapse").last().
					getElementsByClass("navbar-nav mr-auto mt-2 mt-lg-0").first().getElementsByClass("nav-link");;
					List<String> linkList = new ArrayList<>();
					for(Element e : links) {
						String s = e.getElementsByAttribute("href").toString();
						s = s.split("\"")[3].substring(2);
						if(s.contains("page-game")) {
							linkList.add(s);
						}
					}

					int numOfGames = linkList.size();
					//get the teams playing in the match


					//make name and make match id from teams and tournament
					String name;
					if(numOfGames==1) {
						Element blueteambanner = doc.getElementsByClass("col-cadre").first()
								.getElementsByClass("col-12 blue-line-header").first();
						String blueTeamNameAndoutCome = blueteambanner.text();
						String blueTeamName =removeLastChar(blueTeamNameAndoutCome.split("-")[0].toString());
						Element redteambanner = doc.getElementsByClass("col-cadre").first()
								.getElementsByClass("col-12 red-line-header").first();
						String redTeamNameAndoutCome = redteambanner.text();
						String redTeamName =removeLastChar(redTeamNameAndoutCome.split("-")[0].toString());
						name = blueTeamName +" vs "+redTeamName;
					}else {
						Elements names = doc.getElementsByClass("row pb-3").first().
								getElementsByClass("col-4 col-sm-5 text-center");
						name = names.first().text()+" vs "+names.last().text();
					}
					String matchid = tourmament.tournamentID + (tourmament.getMatchs().size()+1);

					//make a match with current info
					Match match = new Match(matchid,name);

					//for each game in the match make a game object
					for(int i =0;i<numOfGames; i++) {
						Game game = makeGame(linkList.get(i), i+1, match);
						match.games.add(game);
						if(i==0) {
							match.teams.addAll(game.teams);
						}
					}
					tourmament.addMatch(match, numOfGames);
		}catch (Exception e) {System.out.println("https://gol.gg"+link);}
	}

	public Game makeGame(String link, int numInMatch, Match match) {
		//make a doc from the link
		String URL = "https://gol.gg"+link;
		Document doc = null;
		try {doc = Jsoup.connect(URL).get();} catch (IOException e) {e.printStackTrace();}
		Element e = doc.getElementsByClass("col-cadre").first(); //cuts most of the text to what we need

		//gametime
		String gametime = e.getElementsByClass("col-6 text-center").text().split(" ")[2];

		//blue team name and if won/loss
		Element blueteambanner = e.getElementsByClass("col-12 blue-line-header").first();
		String blueTeamNameAndoutCome = blueteambanner.text();
		String blueTeamName =removeLastChar(blueTeamNameAndoutCome.split("-")[0].toString());
		String outCome = blueTeamNameAndoutCome.split("-")[1];
		boolean blueWin =false;
		if(outCome.equals(" WIN")) {
			blueWin=true;
		}

		//red team name and if won/loss
		Element redteambanner = e.getElementsByClass("col-12 red-line-header").first();
		String redTeamNameAndoutCome = redteambanner.text();
		String redTeamName =removeLastChar(redTeamNameAndoutCome.split("-")[0].toString());
		boolean redWin =false;
		if(!blueWin) {redWin=true;}

		//date and week
		String[] dateAndWeek = doc.getElementsByClass("col-12 col-sm-5 text-right").first().text().split(" ");
		String date = dateAndWeek[0];
		StringBuilder sb = new StringBuilder(dateAndWeek[1]);
		int week = Integer.parseInt(removeLastChar(sb.delete(0, 5).toString()));

		//region and tourmament
		String[] regionAndTourmament = doc.getElementsByClass("col-12 col-sm-7").first().text().split(" ");
		String stringtourmament = 
				regionAndTourmament[0]+" "+regionAndTourmament[1]+" "+regionAndTourmament[2];
		Tournament tourmament = Tournaments.get(stringtourmament);

		//stats from both teams - kill,towers, dragons, barons, team gold in that order

		//blue team stats
		List<String> blueList = new ArrayList<>();
		for(Element ee : e.getElementsByClass("score-box blue_line")) {
			blueList.add(ee.text());
		}
		int blueKills = Integer.parseInt(blueList.get(0));
		int blueTowers = Integer.parseInt(blueList.get(1));
		int blueDragons = Integer.parseInt(blueList.get(2));
		int blueBarons = Integer.parseInt(blueList.get(3));
		double blueGold = Double.parseDouble(removeLastChar(blueList.get(4)));

		//red team stats
		List<String> redList = new ArrayList<>();
		for(Element ee : e.getElementsByClass("score-box red_line")) {
			redList.add(ee.text());
		}
		int redKills = Integer.parseInt(redList.get(0));
		int redTowers = Integer.parseInt(redList.get(1));
		int redDragons = Integer.parseInt(redList.get(2));
		int redBarons = Integer.parseInt(redList.get(3));
		double redGold = Double.parseDouble(removeLastChar(redList.get(4)));

		//make the game id. the number is the total games played in the tourmament so far+1
		int gameid = (tourmament.numOfGames+1);
		tourmament.numOfGames++;

		//make the game with with info we have gotten
		Game game = new Game
				(teams.get(blueTeamName),blueKills,blueTowers,blueDragons,blueBarons,
						teams.get(redTeamName),redKills,redTowers,redDragons,redBarons,
						blueGold,redGold,gametime,link,date,week,gameid,redWin,blueWin,
						numInMatch,tourmament,match);
		teams.get(blueTeamName).games.add(game);
		teams.get(redTeamName).games.add(game);
		count++;
		return game;
	}

	/*
	 * gets the links for each match. you need to give it the region
	 */
	public List<String> getMatchList(String URL) {
		List<String> list = new ArrayList<>();
		//make a text doc from the website
		Document doc = null;
		try {doc = Jsoup.connect(URL).get();} catch (IOException e) {e.printStackTrace();}
		//cuts the text down closer to what we need.
		//elements has a 1 or more element inside, that element has a Attribute that has a value
		Elements e = doc.getElementsByClass("text-left");
		for(Element el : e) {
			String s = el.getElementsByAttribute("href").toString();
			try {
				s = s.split("\"")[1].substring(2);
			} catch (Exception xxx) {
				System.out.println("missed "+s);
				continue;
			}
			if(!s.contains("preview")) {
				list.add(s);
			}
		}
		return list;
	}

	public String removeLastChar(String str) {
		return removeLastChars(str, 1);
	}

	public String removeLastChars(String str, int chars) {
		return str.substring(0, str.length() - chars);
	}

}
