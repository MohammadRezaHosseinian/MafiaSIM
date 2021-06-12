package mafiaserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mohammadreza
 */
public class Constants {

	public static final String ROOM_FILE_PATH = "rooms.txt";
	public static final String USER_FILE_PATH = "users.txt";
	public static final String ROUTE_LIST_USERS = "alluser";
	public static final String ROUTE_LIST_ROOMS = "room-list";
	public static final String ROUTE_CREATE_ROOM = "create-room";
	public static final String ROUTE_JOIN_ROOM = "join-room";
	public static final String ROUTE_READY_PALYER = "ready";
	public static final int DAY_TIME = 3;
	public static final int VOTING_TIME = 1;
	public static final String MSG_BEGINING_OF_DAY = "[+] Ok, day began, all alive players can speek "
			+ DAY_TIME + " min";
	public static final String MSG_END_OF_DAY = "[+] Day finished ";
	public static final String MSG_BEGINING_OF_VOTING = "[+] Start pre-trial voting for" + VOTING_TIME
			+ "[!] please highlighting own vote \n ";
	public static int MIN_TO_MILISECOND = 60 * 1000;
	public static final String MSG_END_OF_VOTING = "[+] End of preVoting";
	public static final String MSG_WHITOUT_KILL_IN_VOTTING = "At this stage, she leaves the game";
	public static final String MSG_BEGING_OF_NIGHT = "[+] Night began, all alive player must be sleep";
	public static final String MSG_END_OF_NIGHT = "[+] Night finished";
	public static final String MSG_GOD_FATHER_WAKEUP = "[!] GodFather wakeup";
	public static final String MSG_DOCTOR_LECTER_WAKEUP = "[!] DoctorLecter wakeup";
	public static final String MSG_SIMPLE_MAFIA_WAKEUP = "[!] SimpleMafia wakeup";
	public static final String MSG_DOCTOR_WAKEUP = "[!] Doctor wakeup";
	public static final String MSG_DIEHARD_WAKEUP = "[!] DieHard wakeup";
	public static final String MSG_DETECTIVE_WAKEUP = "[!] Detective wakeup";
	public static final String MSG_MAYOR_WAKEUP = "[!] Mayor wakeup";
	public static final String MSG_PROFESSIONAL_WAKEUP = "[!] Professional wakeup";
	public static final String MSG_PSYCHOLOGIST_WAKEUP = "[!] Psychologist wakeup";
	public static final int MAFIA_TURN_TIME = 1;
	public static final String MSG_MAFIA_NIGHT_PHASE = "[+] Mafia players have " + MAFIA_TURN_TIME + " minute to select victim";
	public static final String MSG_MAFIA_END_NIGHT = "[+] Mafia team sleap ";
	public static final String MSG_NO_GODFATHER_IN_GAME = "[-] No god father in game";
	public static final String MSG_NO_DOCTOR_LECTER_IN_GAME = "[-] No doctor lecter in game";
	public static final int CITIZEN_TIME = 30;
	public static final int SECOND_TO_MILISECOND = 1000;
	public static final String MSG_PSYCHOLOGIST_SLEEP = "[+] Psychologist sleep";
	public static final String MSG_DETECTIVE_SLEEP = "[+] Detective sleep";
	public static final String MSG_PROFESSIONAL_SLEEP = "[+] Professional sleep";
	public static final String MSG_DIEHARD_SLEEP = "[+] DieHard sleep";
	public static final String MSG_DOCTOR_SLEEP = "[+] Doctor sleep";
	public static final String MSG_JOINED_SUCCESSFULLY = "[+] You joined to %s successfully";
	public static final String ROUTE_CHAT = "chat";
	public static final String ROUTE_VOTE = "vote";
	public static final String MSG_CITIZENS_WIN = "[+] Citizen win";
	public static final String MSG_MAFIA_WIN = "[+] Mafi  win";
	public static final String MSG_CANT_VOTE = "[-] you can't vote at the moment!!!";
	public static final String MSG_PLAYER_IS_NOT_MAFIA = "[+] Player is not mafia";
	public static final String MSG_PLAYER_IS_MAFIA = "[+] Player is mafia";
	public static final String MSG_YOU_CANT_SHOOT = "[-] Player with user name %s can shoot and you can't shoot";
	public static final String MSG_ASSIGN_ROLE_FOR_PLAYER = "[+] Dear %s , your role is assigned %s !!!";
	public static final int MINIMUM_PLAYERS_COUNT = 10;
	public static final int ROLE_DOCTOR_MAX_SELF_SAVE_TIMES = 1;
	public static final int ROLE_DIEHARD_ARMOR_TIMES = 1;
	public static final int ROLE_DIEHARD_QUERY_TIMES = 2;
	public static final int ROLE_DOCTOR_LECTER_MAX_SELF_SAVE_TIMES = 1;
	public static final int ROLE_MAYOR_CANCEL_VOTTING = 2;
	public static final String ROUTE_LIST_ROOM_USERS = "room-users";
	public static final String MSG_BAD_ROOM_NAME_ERRORE = "[-] No exists any room with name: %s, please use %s to find all rooms";
	public static final String EXIT = "exit";
}
