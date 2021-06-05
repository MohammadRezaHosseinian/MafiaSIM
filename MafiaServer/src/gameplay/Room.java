/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import Player.Player;
import java.util.ArrayList;

/**
 *
 * @author mohammadreza
 */
public class Room {

	private final String name;
	private final int playersCount;
	private final ArrayList<Player> players;

	public Room(String name, int playersCount) {
		this.name = name;
		this.playersCount = playersCount;
		this.players = new ArrayList<Player>(playersCount);
	}

	public String getName() {
		return name;
	}

	public int getPlayersCount() {
		return playersCount;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public boolean addPlayer(String username) {
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				System.out.println("This username used Please enter another name");
				return false;
			}
		}
		Player p = new Player(username,this.players.size());
		this.players.add(p);
		return true;
	}

	public boolean isRoomFull() {
		return this.players.size() == this.playersCount;
	}
	public void shuffliseRoll(){
		
	}
	
}
