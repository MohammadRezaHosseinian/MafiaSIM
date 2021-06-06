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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mafiaserver.Constants;
import rolling.DoctorLecter;
import rolling.GodFather;
import rolling.Mafia;

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
	private boolean firstNight;
	private ArrayList<Player> killNight;
	private HashMap<Player, Integer> vottingSystem;

	public Room(String name, int playersCount) {
		this.name = name;
		this.playersCount = playersCount;
		this.players = new ArrayList<>(playersCount);
		this.gameIsStart = false;
		this.gameIsOver = false;
		this.firstNight = true;
		this.killNight = new ArrayList<>();
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
			this.firstNight = false;
			this.checkNightKills();
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
		this.broadcastMessage(Constants.MSG_BEGING_OF_NIGHT);
		this.mafiaNightPhase();
		this.doctorNightPhase();
		this.dieHardNighPhase();
		this.professionalNightPhase();
		this.detectiveNightPhase();
		this.psychologistNightPhase();
		this.broadcastMessage(Constants.MSG_END_OF_NIGHT);
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
		this.killByVoting();
		this.vottingSystem.clear();
		this.setPlayerCanVote(false);

	}

	private void setPlayerCanVote(boolean canVote) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				player.setCanVote(canVote);
			}
		}
	}

	private void killByVoting() {
		List<Integer> sortedVotes = new ArrayList<>(vottingSystem.values());
		Collections.sort(sortedVotes);
		int quorum = (int) 0.3 * this.alivePlayersCount();
		int maxVote = sortedVotes.get(sortedVotes.size() - 1);
		if (maxVote != sortedVotes.get(sortedVotes.size() - 2)) {
			if (maxVote >= quorum) {
				for (Player player : vottingSystem.keySet()) {
					if (vottingSystem.get(player) == maxVote) {
						player.kill();
						this.broadcastMessage("At this stage,"
								+ player.getUsername() + " leaves the game");
						return;
					}
				}
			}
		}
		this.broadcastMessage(Constants.MSG_WHITOUT_KILL_IN_VOTTING);
	}

	private int alivePlayersCount() {
		int count = 0;
		for (Player player : players) {
			if (player.getIsAlive()) {
				count++;
			}
		}
		return count;
	}

	private void mafiaNightPhase() {
		this.broadcastMessage(Constants.MSG_GOD_FATHER_WAKEUP);
		this.broadcastMessage(Constants.MSG_DOCTOR_LECTER_WAKEUP);
		this.broadcastMessage(Constants.MSG_SIMPLE_MAFIA_WAKEUP);
		this.setMafiaCanSpeek(true);
		if (this.firstNight) {
			this.introduceMafia();
			return;
		}
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				broadcastMessage(Constants.MSG_MAFIA_NIGHT_PHASE);
				setMafiaCanVote(true);
			}
		};
		Timer timer = new Timer("MafiaNight");
		timer.schedule(task, Constants.MAFIA_TURN_TIME * Constants.MIN_TO_MILISECOND);
		this.setMafiaCanVote(false);
		this.setMafiaCanSpeek(false);
		this.broadcastMessage(Constants.MSG_MAFIA_END_NIGHT);
	}

	private void doctorNightPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void dieHardNighPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void professionalNightPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void detectiveNightPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void psychologistNightPhase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void introduceMafia() {
		Player godFather = this.getGodFather();
		Player doctorLecter = this.getDoctorLecter();
		StringBuilder msg = new StringBuilder();
		if (godFather != null) {
			msg.append(String.format("the player with username: %s and chair number: %d is god father\n", godFather.getUsername(), godFather.getChairNumber()));
		}
		if (doctorLecter != null) {
			msg.append(String.format("the player with username: %s and chair number: %d is doctor lecter\n", doctorLecter.getUsername(), doctorLecter.getChairNumber()));
		}
		for (Player player : players) {
			if (player.getRoll() instanceof Mafia) {
				msg.append(String.format("the player with username: %s and chair number: %d is mafia", player.getUsername(), player.getChairNumber()));
			}
		}
		this.mafiaBroadcast(msg.toString());
	}

	private void setMafiaCanSpeek(boolean b) {
		for (Player player : players) {
			if (player.getRoll() instanceof Mafia) {
				player.setCanSpeak(b);
			}
		}
	}

	private void setMafiaCanVote(boolean b) {
		for (Player player : players) {
			if (player.getRoll() instanceof Mafia) {
				player.setCanVote(b);
			}
		}
	}

	private void checkNightKills() {

		this.killNight.clear();
	}

	private Player getGodFather() {
		for (Player player : players) {
			if (player.getRoll() instanceof GodFather) {
				return player;
			}
		}
		this.mafiaBroadcast(Constants.MSG_NO_GODFATHER_IN_GAME);
		return null;
	}

	private Player getDoctorLecter() {
		for (Player player : players) {
			if(player.getRoll() instanceof DoctorLecter){
				return player;
			}
		}
		this.mafiaBroadcast(Constants.MSG_NO_DOCTOR_LECTER_IN_GAME);
		return null;
	}

	private void mafiaBroadcast(String msg) {
		for (Player player : players) {
			if (player.getRoll() instanceof Mafia) {
				try {
					player.getStream().writeUTF(msg);
				} catch (IOException ex) {
					Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

}
