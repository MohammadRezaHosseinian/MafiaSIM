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
import rolling.Mayor;
import rolling.Professional;
import rolling.Psychologist;

/**
 *
 * @author mohammadreza
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
		System.out.println("join room -> " + username);
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				System.out.println("This username used Please enter another name");
				try {
					dos.writeUTF(Constants.MSG_BAD_USERNAME);
				} catch (IOException ex) {
					Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
				}
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
		System.out.println("----> " + cmd + "   //" + arg);
		switch (cmd) {
			case Constants.ROUTE_JOIN_ROOM:
				this.addPlayer(arg, dos);
				break;
			case Constants.ROUTE_READY_PALYER:
				Player p = getPlayer(arg);
				p.setIsReady(true);
				break;

		}
	}

	public void handleReq(String cmd, String arg1, String arg2) {
		System.out.println("state is : " + state.name());
		System.out.println("players count : " + playersCount);
		System.out.println("players size : " + players.size());
//		showTest();
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
						Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
						Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				int chairNumber = Integer.parseInt(arg2);
				Player votedPlayer = this.getPlayer(chairNumber);
				if (!votedPlayer.getIsAlive()) {
					break;
				}
				if (state == GameState.DAY_PHASE) {
					if (votedPlayer != null) {
						int oldVoteCount = this.vottingSystem.get(votedPlayer);
						this.vottingSystem.put(votedPlayer, oldVoteCount + 1);
					}
					break;
				}
				if (state == GameState.MAYOR_ACT && player.getRole() instanceof Mayor) {
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
		for (int i=0; i<players.size(); i++) {
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

	@Override
	public void run() {
		while (players.size() < playersCount) {
			this.broadcastMessage("[-] waiting to all users be ready!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		this.assignRoles();
		state = GameState.INTER_STATES_PHASE;
		while (!this.gameIsOver) {
			this.dayPhase();
			this.votePhase();
			this.mayorAct();
			this.mutPlayer = null;
			this.nightPhase();
			this.firstNight = false;
			this.checkNightKills();
			this.checkGameIsOver();
		}
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
			Thread.sleep(Constants.DAY_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
			Thread.sleep(Constants.VOTING_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
		List<Integer> sortedVotes = new ArrayList<>(vottingSystem.values());
		if (sortedVotes.isEmpty()) {
			return;
		}
		Collections.sort(sortedVotes);
		int quorum = (int) 0.5 * this.alivePlayersCount();

		int maxVote = sortedVotes.get(sortedVotes.size() - 1);
		if (maxVote != sortedVotes.get(sortedVotes.size() - 2)) {
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
		setMafiaCanVote(true);
		try {
			Thread.sleep(Constants.MAFIA_TURN_TIME * Constants.MIN_TO_MILISECOND);
		} catch (InterruptedException ex) {
			Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.setMafiaCanVote(false);
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
				Thread.sleep(Constants.CITIZEN_TIME * Constants.MIN_TO_MILISECOND);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
				Thread.sleep(Constants.CITIZEN_TIME * Constants.MIN_TO_MILISECOND);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
				Thread.sleep(Constants.CITIZEN_TIME * Constants.MIN_TO_MILISECOND);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
				Thread.sleep(Constants.CITIZEN_TIME * Constants.MIN_TO_MILISECOND);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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
				Thread.sleep(Constants.CITIZEN_TIME * Constants.MIN_TO_MILISECOND);
			} catch (InterruptedException ex) {
				Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
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

	private void setMafiaCanVote(boolean b) {
		for (Player player : players) {
			if (player.getRole() instanceof Mafia) {
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
					Logger.getLogger(Room.class
							.getName()).log(Level.SEVERE, null, ex);
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
				Logger.getLogger(Room.class
						.getName()).log(Level.SEVERE, null, ex);
			}
			if (votedPlayer.getRole() instanceof Citizen) {
				try {
					this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_NOT_MAFIA);
				} catch (IOException ex) {
					Logger.getLogger(Room.class
							.getName()).log(Level.SEVERE, null, ex);
				}
				if (votedPlayer.getRole() instanceof Mafia) {
					try {
						this.getDetective().getStream().writeUTF(Constants.MSG_PLAYER_IS_MAFIA);
					} catch (IOException ex) {
						Logger.getLogger(Room.class
								.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(Room.class
					.getName()).log(Level.SEVERE, null, ex);
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
		Mayor mayorRole = (Mayor) this.getMayor().getRole();
		if (this.mayorCanceledVotting) {
			if (mayorRole.canCancelVotting()) {
				this.killedByVottingPlayer.setIsAlive(true);
			}
		}
		this.killedByVottingPlayer = null;
		this.mayorCanceledVotting = false;
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
				Logger.getLogger(Room.class
						.getName()).log(Level.SEVERE, null, ex);
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
		for (Player player : players) {
			System.out.println("------------------");
			System.out.println(player.toString());
		}
	}
}
