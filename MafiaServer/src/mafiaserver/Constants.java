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
	public static String MSG_END_OF_DAY = "[+] Day finished ";
	public static String MSG_BEGINING_OF_VOTING = "[+] Start pre-trial voting for" + VOTING_TIME
			+ "[!] please highlighting own vote \n ";
	public static int MIN_TO_MILISECOND = 60 * 1000;
	public static String MSG_END_OF_VOTING = "[+] End of preVoting";
}
