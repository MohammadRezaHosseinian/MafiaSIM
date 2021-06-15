/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.util.ArrayList;

/**
 *
 * @author mohammadreza
 * In this class, we connect 
 * the threads to the room where
 * the game is running
 * 
 */
public class RoomHandler {
	private final ArrayList<Room> rooms;
	
	public RoomHandler(){
		this.rooms = new ArrayList<>();
	}
// if user create room this method add room in roomlist
	public synchronized void addRoom(String roomname, int roomsize){
		for (Room room : rooms) {
			if(room.getName().equals(roomname))
				return;
		}
		Room newRoom = new Room(roomname, roomsize);
		this.rooms.add(newRoom);
	}
// in this method we can search room by name 
	public synchronized Room getRoomByName(String roomname){
		for (Room room : rooms) {
			if(room.getName().equals(roomname))
				return room;
		}
		return null;
	}

	public synchronized ArrayList<Room> getRooms(){
		return this.rooms;
	}
// this method show all rooms	
	public void showRooms(){
		for (Room room : rooms) {
			System.out.println(room.getName());
		}
	}
}
