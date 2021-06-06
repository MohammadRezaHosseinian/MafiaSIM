/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import Player.Player;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mafiaserver.Constants;

/**
 *
 * @author mohammadreza
 */
public class Room implements Runnable {
	
	private final String name;
	private final int playersCount;
	private final ArrayList<Player> players;
	private boolean gameIsStart;
	private boolean gameIsOver;
	private HashMap<Player,Integer> vottingSystem;
	
	public Room(String name, int playersCount) {
		this.name = name;
		this.playersCount = playersCount;
		this.players = new ArrayList<>(playersCount);
		this.gameIsStart = false;
		this.gameIsOver = false;
		this.vottingSystem = new HashMap<>();
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
	
	public boolean addPlayer(String username, DataOutputStream dos) {
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				System.out.println("This username used Please enter another name");
				return false;
			}
		}
		Player p = new Player(username, this.players.size(), dos);
		this.players.add(p);
		return true;
	}
	
	private Player getPlayer(String username) {
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		System.out.println("[-] invalid username");
		return null;
	}
	
	public boolean isRoomFull() {
		return this.players.size() == this.playersCount;
	}
	
	public void shuffliseRoll() {
		
	}
	
	public void handleReq(DataOutputStream dos, String cmd, String arg) {
		switch (cmd) {
			case Constants.ROUTE_JOIN_ROOM:
				this.addPlayer(arg, dos);
				break;
			case Constants.ROUTE_READY_PALYER:
				Player p = getPlayer(arg);
				p.setIsReady(true);
				this.allPlayersReady();
				break;
		}
	}
	
	public void handleReq(String cmd, String arg1, String arg2) {
		
	}
	
	private boolean allPlayersReady() {
		for (Player player : players) {
			if (!player.getIsReady()) {
				return false;
			}
		}
		this.gameIsStart = true;
		return true;
	}
	
	private void broadcastMessage(String msg) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				try {
					player.getStream().writeUTF(msg);
				} catch (IOException ex) {
					Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}
	
	@Override
	public void run() {
		while (!this.gameIsStart || players.size() != playersCount) {
			this.broadcastMessage("[-] waiting to all users be ready!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		while (!this.gameIsOver) {
			this.dayPhase();
			this.votePhase();
			this.nightPhase();
		}
		this.checkWinner();
	}
	
	private void dayPhase() {
		Timer timer;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				broadcastMessage(Constants.MSG_BEGINING_OF_DAY);
				setCanSpeekPlayers(true);
			}
		};
		timer = new Timer("Timer");
		timer.schedule(task, Constants.DAY_TIME * Constants.MIN_TO_MILISECOND);
		setCanSpeekPlayers(false);
		this.broadcastMessage(Constants.MSG_END_OF_DAY);
	}
	
	
	
	private void nightPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	private void checkWinner() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	private void setCanSpeekPlayers(boolean canSpeek) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				player.setCanSpeak(canSpeek);
			}
		}
	}
	
	private void votePhase() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				broadcastMessage(Constants.MSG_BEGINING_OF_VOTING);
				setPlayerCanVote(true);
			}
		};
		Timer timer = new Timer("Timer Voting");
		timer.schedule(task, Constants.VOTING_TIME * Constants.MIN_TO_MILISECOND);
		this.broadcastMessage(Constants.MSG_END_OF_VOTING);
		
	}
	
	private void setPlayerCanVote(boolean canVote) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				player.setCanVote(canVote);
			}
		}
	}
}
