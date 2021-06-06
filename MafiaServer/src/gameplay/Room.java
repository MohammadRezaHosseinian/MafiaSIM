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
import rolling.Citizen;
import rolling.Detective;
import rolling.DieHard;
import rolling.Doctor;
import rolling.DoctorLecter;
import rolling.GodFather;
import rolling.Mafia;
import rolling.Professional;
import rolling.Psychologist;

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
	private Player mutPlayer;
	private boolean isDay;
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
		try {
			p.getStream().writeUTF(String.format(Constants.MSG_JOINED_SUCCESSFULLY, this.name));
		} catch (IOException ex) {
			Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
		}
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

	private Player getPlayer(int chairNumber) {
		for (Player player : players) {
			if (player.getChairNumber() == chairNumber) {
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
				this.checkAllPlayersReady();
				break;

		}
	}

	public void handleReq(String cmd, String arg1, String arg2) {

		switch (cmd) {
			case Constants.ROUTE_CHAT:
				Player p = this.getPlayer(arg1);
				if (p == null) {
					break;
				}
				if (this.isDay && p.isCanSpeak()) {

					this.broadcastMessage(arg1 + " : " + arg2 + "\n");
				} else if (p.isCanSpeak()) {
					this.mafiaBroadcast(arg1 + " : " + arg2 + "\n");
				}
				break;
			case Constants.ROUTE_VOTE:
				Player player = this.getPlayer(arg1);
				if (player == null) {
					break;
				}
				if (!player.getCanVote()) {
					try {
						player.getStream().writeUTF(Constants.MSG_CANT_VOTE);
					} catch (IOException ex) {
						Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				int chairNumber = Integer.parseInt(arg2);
				Player votedPlayer = this.getPlayer(chairNumber);
				if (!votedPlayer.getIsAlive()) {
					break;
				}
				if (this.isDay) {
					if (votedPlayer != null) {
						int oldVoteCount = this.vottingSystem.get(votedPlayer);
						this.vottingSystem.put(votedPlayer, oldVoteCount + 1);
					}
					break;
				}
				if (player.getRoll() instanceof Doctor) {
					this.doctorVoteNight(votedPlayer);
					break;
				}
				if (player.getRoll() instanceof Detective) {
					this.detectiveVoteNight(votedPlayer);
					break;
				}
				if (player.getRoll() instanceof Psychologist) {
					this.psychologistVoteNight(votedPlayer);
					break;
				}
				if (player.getRoll() instanceof Professional) {
					this.professionalVoteNight(votedPlayer);
					break;
				}
				if (player.getRoll() instanceof Mafia) {
					this.mafiaVoteNight(player, votedPlayer);
					break;
				}
				break;
		}
	}

	private boolean checkAllPlayersReady() {
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
			this.mutPlayer = null;
			this.nightPhase();
			this.firstNight = false;
			this.checkNightKills();
			this.checkGameIsOver();
		}

	}

	private void dayPhase() {
		this.isDay = true;
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
		this.isDay = false;
		this.broadcastMessage(Constants.MSG_BEGING_OF_NIGHT);
		this.mafiaNightPhase();
		this.doctorNightPhase();
		this.dieHardNighPhase();
		this.professionalNightPhase();
		this.detectiveNightPhase();
		this.psychologistNightPhase();
		this.broadcastMessage(Constants.MSG_END_OF_NIGHT);
	}

	private void setCanSpeekPlayers(boolean canSpeek) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				player.setCanSpeak(canSpeek);
			}
		}
		if (this.mutPlayer != null && this.mutPlayer.getIsAlive()) {
			mutPlayer.setCanSpeak(false);
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
		this.broadcastMessage(Constants.MSG_DOCTOR_WAKEUP);
		Player doctor = this.getDoctor();
		if (doctor != null) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (doctor.getIsAlive()) {
						doctor.setCanVote(true);
					}
				}

			};
			Timer timer = new Timer("Doctor");
			timer.schedule(task, Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			doctor.setCanVote(false);
			this.broadcastMessage(Constants.MSG_DOCTOR_SLEEP);
		}
	}

	private void dieHardNighPhase() {
		this.broadcastMessage(Constants.MSG_DIEHARD_WAKEUP);
		Player dieHard = this.getDieHard();
		if (dieHard != null) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (dieHard.getIsAlive()) {
						dieHard.setCanVote(true);
					}
				}

			};
			Timer timer = new Timer("DieHard");
			timer.schedule(task, Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			dieHard.setCanVote(false);
			this.broadcastMessage(Constants.MSG_DIEHARD_SLEEP);
		}
	}

	private void professionalNightPhase() {
		this.broadcastMessage(Constants.MSG_PROFESSIONAL_WAKEUP);
		Player professional = this.getProfessional();
		if (professional != null) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (professional.getIsAlive()) {
						professional.setCanVote(true);
					}
				}
			};
			Timer timer = new Timer("peofessional");
			timer.schedule(task, Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			professional.setCanVote(false);
		}
		this.broadcastMessage(Constants.MSG_PROFESSIONAL_SLEEP);
	}

	private void detectiveNightPhase() {
		this.broadcastMessage(Constants.MSG_DETECTIVE_WAKEUP);
		Player detective = this.getDetective();
		if (detective != null) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (detective.getIsAlive()) {
						detective.setCanVote(true);
					}
				}
			};
			Timer timer = new Timer("Detective");
			timer.schedule(task, Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			detective.setCanVote(false);
		}
		this.broadcastMessage(Constants.MSG_DETECTIVE_SLEEP);
	}

	private void psychologistNightPhase() {
		this.broadcastMessage(Constants.MSG_PSYCHOLOGIST_WAKEUP);
		Player psychologist = this.getPsychologist();
		if (psychologist != null) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					if (psychologist.getIsAlive()) {
						psychologist.setCanVote(true);
					}
				}
			};
			Timer timer = new Timer("Mute");
			timer.schedule(task, Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			psychologist.setCanVote(false);
		}
		this.broadcastMessage(Constants.MSG_PSYCHOLOGIST_SLEEP);

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
		for (Player player : this.killNight) {
			player.kill();
			this.broadcastMessage(String.format("[!!] Last night, the player with username: %s and chair number: killed", player.getUsername(), player.getChairNumber()));
		}

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
			if (player.getRoll() instanceof DoctorLecter) {
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

	private Player getPsychologist() {
		for (Player player : players) {
			if (player.getRoll() instanceof Psychologist) {
				return player;
			}
		}
		return null;
	}

	private Player getDetective() {
		for (Player player : players) {
			if (player.getRoll() instanceof Detective) {
				return player;
			}
		}
		return null;
	}

	private Player getProfessional() {
		for (Player player : players) {
			if (player.getRoll() instanceof Professional) {
				return player;
			}
		}
		return null;
	}

	private Player getDieHard() {
		for (Player player : players) {
			if (player.getRoll() instanceof DieHard) {
				return player;
			}
		}
		return null;
	}

	private Player getDoctor() {
		for (Player player : players) {
			if (player.getRoll() instanceof Doctor) {
				return player;
			}
		}
		return null;
	}

	private void checkGameIsOver() {
		ArrayList<Player> mafiaAliveTeam = new ArrayList();
		ArrayList<Player> citizenAliveTeam = new ArrayList();
		for (Player player : this.players) {
			if (player.getIsAlive()) {
				if (player.getRoll() instanceof Mafia) {
					mafiaAliveTeam.add(player);
				} else {
					citizenAliveTeam.add(player);
				}
			}
		}
		if (mafiaAliveTeam.size() == 0) {
			this.gameIsOver = true;
			this.broadcastMessage(Constants.MSG_CITIZENS_WIN);
			return;
		}
		if (mafiaAliveTeam.size() >= citizenAliveTeam.size()) {
			this.gameIsOver = true;
			this.broadcastMessage(Constants.MSG_MAFIA_WIN);
			return;
		}

	}

	private void doctorVoteNight(Player votedPlayer) {
		Player tmp;
		for (int i = 0; i < this.killNight.size(); i++) {
			tmp = this.killNight.get(i);
			if (tmp.equals(votedPlayer)) {
				killNight.remove(tmp);
			}
		}
	}

	private void detectiveVoteNight(Player votedPlayer) {
		if (votedPlayer.getRoll() instanceof GodFather) {
			try {
				this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_NOT_MAFIA);
				return;
			} catch (IOException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
			}
			if (votedPlayer.getRoll() instanceof Citizen) {
				try {
					this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_NOT_MAFIA);
				} catch (IOException ex) {
					Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
				}
				if (votedPlayer.getRoll() instanceof Mafia) {
					try {
						this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_MAFIA);
					} catch (IOException ex) {
						Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}
	}

	private void psychologistVoteNight(Player votedPlayer) {
		this.mutPlayer = votedPlayer;
	}

	private void professionalVoteNight(Player votedPlayer) {
		if (votedPlayer.getRoll() instanceof GodFather) {
			return;
		}
		if (votedPlayer.getRoll() instanceof Mafia) {
			this.killNight.add(votedPlayer);
			return;
		}
		this.killNight.add(getProfessional());
	}

	private void mafiaVoteNight(Player mafiaPlayer, Player votedPlayer) {
		try {
			Player shooter = this.getMafiaShooter();
			if (mafiaPlayer.equals(shooter)) {
				this.killNight.add(votedPlayer);
				return;
			}
			mafiaPlayer.getStream().writeUTF(String.format(Constants.MSG_YOU_CANT_SHOOT, shooter.getUsername()));
		} catch (IOException ex) {
			Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private Player getMafiaShooter() {
		Player godFather = this.getGodFather();
		Player doctorLecter = this.getDoctorLecter();
		if (godFather.getIsAlive()) {
			return godFather;
		}
		if (doctorLecter.getIsAlive()) {
			return doctorLecter;
		}
		for (Player player : players) {
			if (player.getIsAlive() && player.getRoll() instanceof Mafia) {
				return player;
			}
		}
		return null;
	}
}
