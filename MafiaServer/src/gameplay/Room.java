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
import mafiaserver.Constants;
import rolling.Citizen;
import rolling.Detective;
import rolling.DieHard;
import rolling.Doctor;
import rolling.DoctorLecter;
import rolling.GodFather;
import rolling.Mafia;
import rolling.Mayor;
import rolling.Professional;
import rolling.Psychologist;

/**
 *
 * @author mohammadreza In this class we build the room and manage all the related operations
 */
public class Room implements Runnable {

	private final String name;
	private final int playersCount;
	private ArrayList<Player> players;
	private boolean gameIsStart;
	private boolean gameIsOver;
	private boolean firstNight;
	private final ArrayList<Player> killNight;
	private Player mutPlayer;
	private boolean isDay;
	private final HashMap<Player, Integer> vottingSystem;
	private GameState state;
	private boolean mayorCanceledVotting;
	private Player killedByVottingPlayer;
	private boolean dieHardInquiry;

	public Room(String name, int playersCount) {
		this.name = name;
		this.playersCount = playersCount;
		this.players = new ArrayList<>(playersCount);
		this.gameIsStart = false;
		this.gameIsOver = false;
		this.firstNight = true;
		this.killNight = new ArrayList<>();
		this.vottingSystem = new HashMap<>();
		state = GameState.READY_PENDING;
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
		if (players.size() >= this.playersCount) {
			try {
				dos.writeUTF(Constants.MSG_ROOM_IS_FULL);
			} catch (IOException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> addPlayer");
			}
			return false;
		}
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				System.out.println("This username used Please enter another name");
				try {
					dos.writeUTF(Constants.MSG_BAD_USERNAME);
				} catch (IOException ex) {
					System.out.format(" Oops the connection is closed!:\n");
					System.out.format("in gameplay.room -> addPlayer");
				}
				return false;
			}
		}
		System.out.println("join room -> " + username);
		Player p = new Player(username, this.players.size(), dos);
		this.players.add(p);
		try {
			p.getStream().writeUTF(String.format(Constants.MSG_JOINED_SUCCESSFULLY, this.name));
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\n");
			System.out.format("in gameplay.room -> addPlayer");
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

	public void handleReq(DataOutputStream dos, String cmd, String arg) {
		System.out.println("----> " + cmd + "   //" + arg);
		switch (cmd) {
			case Constants.ROUTE_JOIN_ROOM:
				this.addPlayer(arg, dos);
				break;
			case Constants.ROUTE_READY_PALYER:
				Player p = getPlayer(arg);
				p.setIsReady(true);
				 {
					try {
						p.getStream().writeUTF(Constants.MSG_READY_RESPONSE);
					} catch (IOException ex) {
						System.out.format(" Oops the connection is closed!:\n");
						System.out.format("in gameplay.room -> first hanleReq");
					}
				}
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
				if (state == GameState.DAY_PHASE && p.isCanSpeak()) {
					this.broadcastMessage(arg1 + " : " + arg2 + "\n");
				} else if (state == GameState.MAFIA_TEAM_NIGHT_ACT && p.isCanSpeak()) {
					this.mafiaBroadcast(arg1 + " : " + arg2 + "\n");
				} else {
					try {
						p.getStream().writeUTF("you can't chat at this time");
					} catch (IOException ex) {
						System.out.format(" Oops the connection is closed!:\n");
						System.out.format("in gameplay.room -> second hanleReq");
					}
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
						System.out.format(" Oops the connection is closed!:\n");
						System.out.format("in gameplay.room -> first hanleReq");
					}
				}
				int chairNumber = Integer.parseInt(arg2);
				Player votedPlayer = this.getPlayer(chairNumber);
				if (!votedPlayer.getIsAlive()) {
					break;
				}
				if (state == GameState.VOTE_AFTER_DAY) {
					if (votedPlayer != null) {
						if (!vottingSystem.containsKey(votedPlayer)) {
							vottingSystem.put(votedPlayer, 0);
						}
						int oldVoteCount = this.vottingSystem.get(votedPlayer);
						this.vottingSystem.put(votedPlayer, oldVoteCount + 1);
					}
					break;
				}
				if (player.getRole() instanceof Mayor && state == GameState.MAYOR_ACT) {
					if (votedPlayer != null) {
						this.mayorCanceledVotting = true;
					} else {
						this.mayorCanceledVotting = false;
					}
					break;
				}
				if (player.getRole() instanceof Doctor && state == GameState.DOCTOR_ACT) {
					this.doctorVoteNight(votedPlayer);
					break;
				}
				if (player.getRole() instanceof Detective && state == GameState.DETECTIVE_ACT) {
					this.detectiveVoteNight(votedPlayer);
					break;
				}
				if (player.getRole() instanceof Psychologist && state == GameState.PSYCO_ACT) {
					this.psychologistVoteNight(votedPlayer);
					break;
				}
				if (player.getRole() instanceof Professional && state == GameState.PROFESSIONAL_ACT) {
					this.professionalVoteNight(votedPlayer);
					break;
				}
				if (player.getRole() instanceof DieHard && state == GameState.DIE_HARD_ACT) {
					this.dieHardAct(votedPlayer);
					break;
				}
				if (player.getRole() instanceof DoctorLecter && state == GameState.DOCTOR_LECTER_ACT) {
					this.doctorLecterAct(votedPlayer);
					break;
				}
				if (player.getRole() instanceof Mafia && state == GameState.MAFIA_TEAM_NIGHT_ACT) {
					this.mafiaVoteNight(player, votedPlayer);
					break;
				}
				break;
		}
	}

	private void broadcastMessage(String msg) {
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			if (player.getIsAlive()) {
				try {
					player.getStream().writeUTF(msg);
				} catch (IOException ex) {
					// connection closed, remove player from room
					player.setIsAlive(false);
					this.players.remove(player);
					this.broadcastMessage(String.format(Constants.MSG_CLOSED_CONNECTION, player.getUsername()));
				}
			}
		}
	}

	private boolean cheakAllPlayersIsReady() {
		if (players.size() != playersCount) {
			return false;
		}
		for (Player player : players) {
			if (!player.getIsReady()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		while (!this.cheakAllPlayersIsReady()) {
			this.broadcastMessage("[-] waiting to all users be ready!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> run");
			}
		}
		this.assignRoles();
		state = GameState.INTER_STATES_PHASE;
		while (!this.gameIsOver) {
			showAlivePlayers();
			this.dayPhase();
			showAlivePlayers();
			this.votePhase();
			this.mayorAct();
			this.mutPlayer = null;
			this.nightPhase();
			this.firstNight = false;
			this.checkNightKills();
			this.checkGameIsOver();
		}
	}

	private void showAlivePlayers() {
		StringBuilder sb = new StringBuilder();
		sb.append("[+] Alive players :\n");
		for (Player player : players) {
			if (player.getIsAlive()) {
				sb.append(String.format("\t\t [%d] %s\n", player.getChairNumber(), player.getUsername()));
			}
		}
		this.broadcastMessage(sb.toString());
	}

	private void dayPhase() {
		state = GameState.DAY_PHASE;
		if (this.dieHardInquiry) {
			this.broadCastKillRole();
		}
		this.isDay = true;

		setCanSpeekPlayers(true);
		broadcastMessage(Constants.MSG_BEGINING_OF_DAY);
		try {
			broadcastMessage(String.format(Constants.MSG_PHASE_TIME, Constants.DAY_TIME));
			Thread.sleep(Constants.DAY_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			System.out.format(" Oops the connection is closed!:\n");
			System.out.format("in gameplay.room -> dayPhase");
		}
		setCanSpeekPlayers(false);
		this.broadcastMessage(Constants.MSG_END_OF_DAY);
		state = GameState.INTER_STATES_PHASE;
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
		state = GameState.VOTE_AFTER_DAY;

		broadcastMessage(Constants.MSG_BEGINING_OF_VOTING);
		setPlayerCanVote(true);

		try {
			broadcastMessage(String.format(Constants.MSG_PHASE_TIME, Constants.VOTING_TIME));
			Thread.sleep(Constants.VOTING_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			System.out.format(" Oops the connection is closed!:\n");
			System.out.format("in gameplay.room -> votePhase");
		}

		this.broadcastMessage(Constants.MSG_END_OF_VOTING);
		this.killByVoting();

		this.vottingSystem.clear();
		this.setPlayerCanVote(false);
		state = GameState.INTER_STATES_PHASE;
	}

	private void setPlayerCanVote(boolean canVote) {
		for (Player player : players) {
			if (player.getIsAlive()) {
				player.setCanVote(canVote);
			}
		}
	}

	private void killByVoting() {
		showTest();
		ArrayList<Integer> sortedVotes = Utilty.votingsystemValuseList(vottingSystem);
		System.out.println(sortedVotes);
		if (sortedVotes.isEmpty()) {
			this.broadcastMessage(Constants.MSG_NOBODY_KILLED);
			return;
		}
		Collections.sort(sortedVotes);
		int quorum = (int) 0.5 * this.alivePlayersCount();

		int maxVote = sortedVotes.get(sortedVotes.size() - 1);
		System.out.println("quorum : " + quorum);
		System.out.println("maxVote  :" + maxVote);
		if (sortedVotes.size() >= 2 && maxVote == sortedVotes.get(sortedVotes.size() - 2)) {
			this.broadcastMessage(Constants.MSG_EQUAL_MAX_VOTES);
			return;
		}
		if (maxVote >= quorum) {
			for (Player player : vottingSystem.keySet()) {
				if (vottingSystem.get(player) == maxVote) {
					player.kill();
					this.killedByVottingPlayer = player;
					this.broadcastMessage("At this stage,"
							+ player.getUsername() + " leaves the game");
					return;
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
		state = GameState.MAFIA_TEAM_NIGHT_ACT;
		this.broadcastMessage(Constants.MSG_GOD_FATHER_WAKEUP);
		this.broadcastMessage(Constants.MSG_DOCTOR_LECTER_WAKEUP);
		this.broadcastMessage(Constants.MSG_SIMPLE_MAFIA_WAKEUP);
		this.setMafiaCanSpeek(true);
		if (this.firstNight) {
			this.introduceMafia();
			return;
		}

		broadcastMessage(Constants.MSG_MAFIA_NIGHT_PHASE);
		setMafiaCanVoteAndChat(true);
		setMafiaCanSpeek(true);
		try {
			broadcastMessage(String.format(Constants.MSG_PHASE_TIME, Constants.MAFIA_TURN_TIME));
			Thread.sleep(Constants.MAFIA_TURN_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			System.out.format(" Oops the connection is closed!:\n");
			System.out.format("in gameplay.room -> mafiaNightPhase");
		}

		this.setMafiaCanVoteAndChat(false);
		this.setMafiaCanSpeek(false);
		this.broadcastMessage(Constants.MSG_MAFIA_END_NIGHT);
		state = GameState.INTER_STATES_PHASE;
	}

	private void doctorNightPhase() {
		state = GameState.DOCTOR_ACT;
		this.broadcastMessage(Constants.MSG_DOCTOR_WAKEUP);
		Player doctor = this.getDoctor();
		if (doctor != null) {

			if (doctor.getIsAlive()) {
				doctor.setCanVote(true);
			}
			try {
				broadcastMessage(String.format(Constants.MSG_PHASE_TIME_FOR_CITIZEN, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> doctorNightPhase");
			}
			doctor.setCanVote(false);
			this.broadcastMessage(Constants.MSG_DOCTOR_SLEEP);
			state = GameState.INTER_STATES_PHASE;
		}
	}

	private void dieHardNighPhase() {
		state = GameState.DIE_HARD_ACT;
		this.broadcastMessage(Constants.MSG_DIEHARD_WAKEUP);
		Player dieHard = this.getDieHard();
		if (dieHard != null) {

			if (dieHard.getIsAlive()) {
				dieHard.setCanVote(true);
			}
			try {
				broadcastMessage(String.format(Constants.MSG_PHASE_TIME_FOR_CITIZEN, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> dieHardPhase");
			}

			dieHard.setCanVote(false);
			this.broadcastMessage(Constants.MSG_DIEHARD_SLEEP);
		}
		state = GameState.INTER_STATES_PHASE;
	}

	private void professionalNightPhase() {
		state = GameState.PROFESSIONAL_ACT;
		this.broadcastMessage(Constants.MSG_PROFESSIONAL_WAKEUP);
		Player professional = this.getProfessional();
		if (professional != null) {

			if (professional.getIsAlive()) {
				professional.setCanVote(true);
			}
			try {
				broadcastMessage(String.format(Constants.MSG_PHASE_TIME_FOR_CITIZEN, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> professionalNightPhase");
			}

			professional.setCanVote(false);
		}

		this.broadcastMessage(Constants.MSG_PROFESSIONAL_SLEEP);
		state = GameState.INTER_STATES_PHASE;
	}

	private void detectiveNightPhase() {
		state = GameState.DETECTIVE_ACT;
		this.broadcastMessage(Constants.MSG_DETECTIVE_WAKEUP);
		Player detective = this.getDetective();
		if (detective != null) {

			if (detective.getIsAlive()) {
				detective.setCanVote(true);
			}
			try {
				broadcastMessage(String.format(Constants.MSG_PHASE_TIME_FOR_CITIZEN, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> detectiveNightPhase");
			}
			detective.setCanVote(false);
		}
		this.broadcastMessage(Constants.MSG_DETECTIVE_SLEEP);
		state = GameState.INTER_STATES_PHASE;
	}

	private void psychologistNightPhase() {
		state = GameState.PSYCO_ACT;
		this.broadcastMessage(Constants.MSG_PSYCHOLOGIST_WAKEUP);
		Player psychologist = this.getPsychologist();
		if (psychologist != null) {

			if (psychologist.getIsAlive()) {
				psychologist.setCanVote(true);
			}
			try {
				broadcastMessage(String.format(Constants.MSG_PHASE_TIME_FOR_CITIZEN, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			} catch (InterruptedException ex) {
				System.out.format(" Oops the connection is closed!:\n");
				System.out.format("in gameplay.room -> psychologistNightPhase");
			}
			psychologist.setCanVote(false);
		}
		this.broadcastMessage(Constants.MSG_PSYCHOLOGIST_SLEEP);
		state = GameState.INTER_STATES_PHASE;
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
			if (player.getRole() instanceof Mafia) {
				msg.append(String.format("the player with username: %s and chair number: %d is mafia", player.getUsername(), player.getChairNumber()));
			}
		}
		this.mafiaBroadcast(msg.toString());
	}

	private void setMafiaCanSpeek(boolean b) {
		for (Player player : players) {
			if (player.getRole() instanceof Mafia) {
				player.setCanSpeak(b);
			}
		}
	}

	private void setMafiaCanVoteAndChat(boolean b) {
		for (Player player : players) {
			if (player.getRole() instanceof Mafia && player.getIsAlive()) {
				player.setCanSpeak(b);
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
			if (player.getRole() instanceof GodFather) {
				return player;
			}
		}
		this.mafiaBroadcast(Constants.MSG_NO_GODFATHER_IN_GAME);
		return null;
	}

	private Player getDoctorLecter() {
		for (Player player : players) {
			if (player.getRole() instanceof DoctorLecter) {
				return player;
			}
		}
		this.mafiaBroadcast(Constants.MSG_NO_DOCTOR_LECTER_IN_GAME);
		return null;
	}

	private void mafiaBroadcast(String msg) {
		for (Player player : players) {
			if (player.getRole() instanceof Mafia) {
				try {
					player.getStream().writeUTF(msg);
				} catch (IOException ex) {
					System.out.format(" Oops the connection is closed!:\nin gameplay.room -> mafiaBroadcast");
				}
			}
		}
	}

	private Player getPsychologist() {
		for (Player player : players) {
			if (player.getRole() instanceof Psychologist) {
				return player;
			}
		}
		return null;
	}

	private Player getDetective() {
		for (Player player : players) {
			if (player.getRole() instanceof Detective) {
				return player;
			}
		}
		return null;
	}

	private Player getProfessional() {
		for (Player player : players) {
			if (player.getRole() instanceof Professional) {
				return player;
			}
		}
		return null;
	}

	private Player getDieHard() {
		for (Player player : players) {
			if (player.getRole() instanceof DieHard) {
				return player;
			}
		}
		return null;
	}

	private Player getDoctor() {
		for (Player player : players) {
			if (player.getRole() instanceof Doctor) {
				return player;
			}
		}
		return null;
	}

	private void checkGameIsOver() {
		ArrayList<Player> mafiaAliveTeam = new ArrayList<>();
		ArrayList<Player> citizenAliveTeam = new ArrayList<>();
		for (Player player : this.players) {
			if (player.getIsAlive()) {
				if (player.getRole() instanceof Mafia) {
					mafiaAliveTeam.add(player);
				} else {
					citizenAliveTeam.add(player);
				}
			}
		}
		if (mafiaAliveTeam.isEmpty()) {
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
		Player doctor = this.getDoctor();
		Doctor doctorRole = (Doctor) doctor.getRole();
		if (votedPlayer.equals(doctor)) {
			if (!doctorRole.checkCanSaveSelf()) {
				return;
			}
		}
		for (int i = 0; i < this.killNight.size(); i++) {
			tmp = this.killNight.get(i);
			if (tmp.equals(votedPlayer)) {
				killNight.remove(tmp);
			}
		}
	}

	private void detectiveVoteNight(Player votedPlayer) {
		if (votedPlayer.getRole() instanceof GodFather) {
			try {
				this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_NOT_MAFIA);
				return;
			} catch (IOException ex) {
				System.out.format(" Oops the connection is closed!:\nin gameplay.room -> detectiveVoteNight");
			}
			if (votedPlayer.getRole() instanceof Citizen) {
				try {
					this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_NOT_MAFIA);
				} catch (IOException ex) {
					System.out.format(" Oops the connection is closed!:\nin gameplay.room -> detectiveVoteNight");
				}
				if (votedPlayer.getRole() instanceof Mafia) {
					try {
						this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_MAFIA);
					} catch (IOException ex) {
						System.out.format(" Oops the connection is closed!:\nin gameplay.room -> detectiveVoteNight");
					}
				}
			}
		}
	}

	private void psychologistVoteNight(Player votedPlayer) {
		Psychologist psycoRole = (Psychologist) this.getPsychologist().getRole();
		if (psycoRole.canMutePlayer(votedPlayer)) {
			this.mutPlayer = votedPlayer;
		}
	}

	private void professionalVoteNight(Player votedPlayer) {
		if (votedPlayer.getRole() instanceof GodFather) {
			return;
		}
		if (votedPlayer.getRole() instanceof Mafia) {
			this.killNight.add(votedPlayer);
			return;
		}
		this.killNight.add(getProfessional());
	}

	private void mafiaVoteNight(Player mafiaPlayer, Player votedPlayer) {
		try {
			Player shooter = this.getMafiaShooter();
			if (mafiaPlayer.equals(shooter) && !(votedPlayer.getRole() instanceof DieHard)) {
				if (votedPlayer.getRole() instanceof DieHard) {
					DieHard dieHardRole = (DieHard) votedPlayer.getRole();
					if (dieHardRole.checkHasArmor()) {
						return;
					}
				}
				this.killNight.add(votedPlayer);
				return;
			}
			mafiaPlayer.getStream().writeUTF(String.format(Constants.MSG_YOU_CANT_SHOOT, shooter.getUsername()));
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin gameplay.room -> mafiaVoteNight");
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
			if (player.getIsAlive() && player.getRole() instanceof Mafia) {
				return player;
			}
		}
		return null;
	}

	private void mayorAct() {
		Player mayor = getMayor();
		mayor.setCanVote(true);
		this.state = GameState.MAYOR_ACT;
		Mayor mayorRole = (Mayor) this.getMayor().getRole();
		try {
			if (mayorRole.canCancelVotting()) {
				this.broadcastMessage(String.format(Constants.MSG_MAYOR_ACT, Constants.CITIZEN_TIME));
				Thread.sleep(Constants.CITIZEN_TIME * Constants.SECOND_TO_MILISECOND);
			}
		} catch (InterruptedException ex) {
			System.out.format(" Oops the connection is closed!:\nin gameplay.room -> mayorAct");
		}

		if (this.mayorCanceledVotting && mayorRole.canCancelVotting()) {
			mayorRole.cancelVotting();
			this.killedByVottingPlayer.setIsAlive(true);
		}
		this.killedByVottingPlayer = null;
		this.mayorCanceledVotting = false;
		mayor.setCanVote(false);
	}

	private void doctorLecterAct(Player votedPlayer) {
		Player tmp;
		Player doctorLecterPlayer = getDoctorLecter();
		DoctorLecter doctorLecterRole = (DoctorLecter) doctorLecterPlayer.getRole();
		if (doctorLecterPlayer.equals(votedPlayer)) {
			if (!doctorLecterRole.canSaveSelf()) {
				return;
			}
		}
		for (int i = 0; i < this.killNight.size(); i++) {
			tmp = this.killNight.get(i);
			if (tmp.equals(votedPlayer)) {
				if (tmp.getRole() instanceof Mafia) {

					killNight.remove(tmp);
				}
			}
		}

	}

	private void dieHardAct(Player votedPlayer) {
		DieHard dieHardRole = (DieHard) this.getDieHard().getRole();
		if (votedPlayer == null) {
			this.dieHardInquiry = false;
			return;
		}
		if (dieHardRole.checkCanQuery()) {
			this.dieHardInquiry = true;
		}
	}

	private void broadCastKillRole() {
		ArrayList<Player> killedPlayers = new ArrayList<>();
		for (Player player : players) {
			if (!player.getIsAlive()) {
				killedPlayers.add(player);
			}
		}
		Collections.shuffle(killedPlayers);
		for (Player killedPlayer : killedPlayers) {
			this.broadcastMessage(killedPlayer.getRole().toString());
		}
		this.dieHardInquiry = false;
	}

	private void assignRoles() {
		this.players = Utilty.assignPlayersRole(players);
		for (Player player : players) {
			try {
				player.getStream().writeUTF(String.format(Constants.MSG_ASSIGN_ROLE_FOR_PLAYER, player.getUsername(), player.getRole().getRole()));
			} catch (IOException ex) {
				System.out.format(" Oops the connection is closed!:\nin gameplay.room -> assignRoles");
			}
		}
	}

	private Player getMayor() {
		for (Player player : players) {
			if (player.getRole() instanceof Mayor) {
				return player;
			}
		}
		return null;
	}

	private void showTest() {
		for (Player player : vottingSystem.keySet()) {
			System.out.println("------------------");
			System.out.println("[---] " + player.getUsername() + " has " + vottingSystem.get(player) + " votes");
		}
	}
}
