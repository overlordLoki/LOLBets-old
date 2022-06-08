package main;

import java.io.*;
import java.util.*;

public class Region implements Serializable {
	private static final long serialVersionUID = 1L;
	public String regionID;
	public Map<String,Tournament> Tournaments = new HashMap<>();
	public Map<String,Team> teams = new HashMap<>();//holds the teams for Serializable reasons
    public Region(String Name) {
    	regionID = Name;
    }
}
