package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Utilities;


public class GUI extends JFrame{
	//components we need
	public JTextArea textArea;
	private JPanel topPanel;
	private JPanel westPanel;
	public DrawingPanel drawing;
	public JComboBox<String> combox;
	
	//data and Team stats object
	
	private Map<String,Region> regions = new HashMap<>();
	private Map<String,Tournament> Tournaments = new HashMap<>();
	private Map<String,Team> allTeams = new HashMap<>();
	public TeamStats teamStats;
	public Main M;
	
	public GUI(Main m) {
		//make the stats object and link to the LOL data
		M=m;
		regions = m.regions;
		Tournaments = m.Tournaments;
		allTeams = m.allTeams;
		teamStats = new TeamStats
				(Tournaments.get("LCK Spring 2022"), allTeams.get("T1"));
		
		//make the Frame and set its propertys
		this.setTitle("LOL Stats");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000,700);
		this.setResizable(false);
		makeTopPanel();
		makeWestPanel();
		makeDrawingPan();
		
		//set the icon
		ImageIcon icon = new ImageIcon("icon.png");
		this.setIconImage(icon.getImage());
		
		//last add everything to the frame and set Visible
		this.add(westPanel,BorderLayout.WEST);
		this.add(drawing,BorderLayout.CENTER);
		//this.pack();
		this.setVisible(true);
	}
	
	/*
	 * for testing reasons
	 */
	public void test() {
		M.makeDataFromText("newTeams.txt");
		printLine(M.Tournaments.size());
	}

	/*
	 * make the panel we post text onto
	 */
	private void makeWestPanel() {
		//first make it and set propertys
		westPanel = new JPanel();
		westPanel.setPreferredSize(new Dimension(300,700));
		westPanel.setBackground(Color.black);
		
		// make the text Area panel and set propertys
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true); // pretty line wrap.

		textArea.setBackground(Color.black);
		Font font = new Font("Verdana", Font.BOLD, 12);
		textArea.setFont(font);
		textArea.setForeground(Color.green);

		// make the scroll panel for the text Area to go in.
		JScrollPane scroll = new JScrollPane(textArea);
		
		// these two lines make the JScrollPane always scroll to the bottom when
		// text is appended to the JtextArea.
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scroll.setPreferredSize(new Dimension(300,500));
		
		
		//add Top panel
		westPanel.add(topPanel,BorderLayout.NORTH);
		westPanel.add(scroll,BorderLayout.CENTER);
	}

	/*
	 * make the panel we draw on
	 */
	private void makeDrawingPan() {
		drawing = new DrawingPanel();
		drawing.setPreferredSize(new Dimension(500,700));
		drawing.setBackground(Color.black);
		drawing.setVisible(true);
		drawing.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(drawing.nodesToPlot==null|| drawing.nodesToPlot.size()==0) {
					return;
				}
				for(Node n : drawing.nodesToPlot) {
					if(n.isOn(e.getX(), e.getY())) {
						n.drawRed(drawing.g2);
						if(drawing.redNode!=null) {
							drawing.redNode.draw(drawing.g2);
						}
						drawing.redNode = n;
						repaint();
						clearText();
						for(String s : n.game.gameStats()) {
							printLine(s);
						}
					}
				}
			}
		});
	}

	/*
	 * make the panel we put our buttons and drop down menu
	 */
	private void makeTopPanel() {
		//first make it and set propertys
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(4,2));
		topPanel.setBackground(Color.black);
		topPanel.setPreferredSize(new Dimension(300,200));
		
		//now make the panels to add to topPanel
		//text field for the user to write info into
		JTextField textField = new JTextField();
		Font font = new Font("Verdana", Font.BOLD, 12);
		textField.setFont(font);
		textField.setForeground(Color.green);
		textField.setBackground(Color.black);
		//make Button Panel and set propertys, it has 2 buttons
		JPanel Panel1 = new JPanel();
		Panel1.setLayout(new GridLayout(2,2));
		Panel1.setBackground(Color.black);
		
		//make Button for setting Tournament and set propertys
		JButton setTornButton = new JButton("Set Tournament");
		setTornButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tournament t = Tournaments.get(textField.getText());
				if(t == null) {
					String s = textField.getText()+ " is null";
					printLine(s);
				}else {
					teamStats.setTournament(t);
					String s = textField.getText()+" has been Set";
					printLine(s);
				}}});
		setTornButton.setBackground(Color.red);
		
		//make Button for setting Team and set propertys
		JButton setTeamButton = new JButton("Set Team");
		setTeamButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Team t = allTeams.get(textField.getText());
				if(t == null) {
					String s = textField.getText()+ " is null";
					printLine(s);
				}else {
					teamStats.setTeam(t);
					String s = textField.getText()+" has been Set";
					printLine(s);
				}}});
		setTeamButton.setBackground(Color.red);
		
		//make Button for Listing the teams and set propertys
		JButton teamNamButton = new JButton("List Teams");
		teamNamButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				for(Team t : teamStats.teams) {
					printLine(t.name);
				}
			}});
		teamNamButton.setBackground(Color.red);
		//make button for list all commands
		JButton RunComButton = new JButton("Run Command");
		RunComButton.setBackground(Color.red);
		RunComButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(combox ==null) {
					return;
				}else
				clearText();
				listCommands((String) combox.getSelectedItem());
			}});
		//drop down menu
		combox = new JComboBox<String>(comList);		
		//make Button for Listing the Tournaments and set propertys
		JButton ListTournamentsButton = new JButton("List Tournaments");
		ListTournamentsButton.setBackground(Color.red);
		ListTournamentsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				for(String s : Tournaments.keySet()) {
					printLine(s);
				}
			}});
		
		JButton clearButton = new JButton("Clear Graphics");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawing.clear();
			}});
		clearButton.setBackground(Color.red);
		

		//last add them and make some blank panels to make the shape we want
		topPanel.add(setTornButton);
		topPanel.add(textField);
		topPanel.add(setTeamButton);
		topPanel.add(teamNamButton);
		topPanel.add(RunComButton);
		topPanel.add(combox);
		topPanel.add(ListTournamentsButton);
		topPanel.add(clearButton);
	}
	
	//array of strings to rep what methods will be used. made it here so easy to add more
	private String[] comList = {
			"Graph Kills","Team Info","Graph dragons","print last 5 games",
			"print last game","print current team",
			"print current Tournament","Update Matchs","Print Avg Total Dragons",
			"Toggle Pen","Test"};
	
	/*
	 * this is were each command is proformed or calls the methad that does it.
	 * uses the string from the drop box to deside what command is used
	 */
	private void listCommands(String command) {
		switch (command) {
		  case "print last 5 games":
				List<Game> lastXGames = teamStats.team.lastXGames(5);
				Collections.reverse(lastXGames);
				for(int i =0; i<5; i++) {
					for(String s : lastXGames.get(i).gameStats()) {
						printLine(s);
					}
					printLine("");
				}
		    break;
		  case "print last game":
			  //print the last game they played
				List<Game> last5Games = teamStats.team.lastXGames(1);
				for(String s : last5Games.get(0).gameStats()) {
					printLine(s);
				}
		    break;
		  case "print current team":
			  printLine(teamStats.team.name);
			  break;
		  case "print current Tournament":
			  printLine(teamStats.tournament.tournamentID);
			  break;
		  case "Update Matchs":
			  //get and new games that have been played in lcs,lec,lck and lpl
			  M.updateMatchs();
			  break;
		  case "Test":
			  //for testing reasons
			  test();
			  break;
		  case "Team Info":
			  //print info about the team
			  printLine("Avg kills");
			  printLine(Double.toString(teamStats.getAvgKills()));
			  printLine("Avg Dragons");
			  printLine(Double.toString(teamStats.getAvgDragons()));
			  printLine("Wins/Loses");
			  printWinloseCount();
			  break;
		  case "Print Avg Total Dragons":
			  printLine(Double.toString(teamStats.getAvgDragons())); 
			  break;
		  case "Graph Kills":
			  drawing.drawGraf(teamStats.team.games,"kills");
			  drawing.redNode=null;
			  break;
		  case "Graph dragons":
			  drawing.drawGraf(teamStats.team.games,"dragons");
			  drawing.redNode=null;
			  break;
		  case "Toggle Pen":
			  if(drawing.penOn) {
				  drawing.penOn = false;
				  printLine("pen off");
			  }else {
				  drawing.penOn = true;
				  printLine("pen on");
			  }
			  break;
		  default:
		    break;
		}
	}
	
	//Convince methods
	/*
	 * prints wins and loses and total
	 */
	private void printWinloseCount() {
		//get win/loses for the team
		int[] wl = teamStats.tournament.getWinLossRate(teamStats.team);
		int wins = wl[0];
		int loses = wl[1];
		int totalGames = wins+loses;
		//make them a string for printing
		String w = Integer.toString(wins);
		String l = Integer.toString(loses);
		String t = Integer.toString(totalGames);
		//print them
		printLine("Wins: "+w+" Loses: "+l+" Total Games: "+t);
	}
	
	/*
	 * redraws everything
	 */
	public void redraw() {
		this.repaint();
	}
	
	/*
	 * appends String to textArea and adds \n to the string
	 */
	public void printLine(String s) {
		textArea.append(s+"\n");
	}
	public void printLine(int i) {
		String s = Integer.toString(i);
		textArea.append(s+"\n");
	}
	public void printLine(double d) {
		String s = Double.toString(d);
		textArea.append(s+"\n");
	}
	
	/*
	 * Clears all text
	 */
	public void clearText() {
		textArea.setText("");
	}
	
	/*
	 * gets the last line of text from textArea
	 */
	public String getLastLine() {
		//stackoverflow? like the run time error? never heard of that Website
		try {
			int end = textArea.getDocument().getLength();
			int start = Utilities.getRowStart(textArea, end);
		while (start == end){
		    end--;
		    start = Utilities.getRowStart(textArea, end);
		}
		return textArea.getText(start, end - start);
		} catch (BadLocationException e) {e.printStackTrace();}
		return "";
	}
	
}
