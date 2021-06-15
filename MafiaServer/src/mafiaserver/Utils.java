/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import Player.Player;
import gameplay.Room;
import java.util.ArrayList;

/**
 *
 * @author mohammadreza
 * In this class we handled 
 * a number of client requests
 */
public class Utils {

	// this method shows list of rooms
	public static String listRooms(ArrayList<Room> rooms) {
		StringBuilder sb = new StringBuilder();
		for (Room room : rooms) {
			sb.append(room.getName()).append("\n");
		}
		return sb.toString();
	}

	// this method shows list of players in a room
	public static String listRoomUser(Room room) {
		StringBuilder sb = new StringBuilder();
		for (Player player : room.getPlayers()) {
			sb.append(player.getUsername()).append("\n");
		}
		return sb.toString();
	}
	
	// this method shows list of all player
	public static String listAllUser(ArrayList<Room> rooms){
		StringBuilder sb = new StringBuilder();
		for (Room room : rooms) {
			sb.append(Utils.listRoomUser(room)).append("\n");
		}
		return sb.toString();
	}
}
