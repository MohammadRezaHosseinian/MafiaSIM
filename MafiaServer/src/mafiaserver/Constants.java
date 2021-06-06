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
	public static final String ROUTE_LIST_USERS = "user-list";
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
	public static final String MSG_PERFESSIONAL_WAKEUP = "[!] Ferfessional wakeup";
	public static final String MSG_PSYCHOLOGIST_WAKEUP = "[!] Psychologist wakeup";
	public static final int MAFIA_TURN_TIME = 1;
	public static final String MSG_MAFIA_NIGHT_PHASE = "[+] Mafia players have " + MAFIA_TURN_TIME + " minute to select victim";
	public static final String MSG_MAFIA_END_NIGHT = "[+] Mafia team sleap ";
	public static final String MSG_NO_GODFATHER_IN_GAME = "[-] No god father in game";
	public static final String MSG_NO_DOCTOR_LECTER_IN_GAME = "[-] No doctor lecter in game";

}
