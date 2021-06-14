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
 */
public class RoomHandler {
	private final ArrayList<Room> rooms;
	
	public RoomHandler(){
		this.rooms = new ArrayList<>();
	}
	
	public synchronized void addRoom(String roomname, int roomsize){
		for (Room room : rooms) {
			if(room.getName().equals(roomname))
				return;
		}
		Room newRoom = new Room(roomname, roomsize);
		this.rooms.add(newRoom);
	}
	
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
	
	public void showRooms(){
		for (Room room : rooms) {
			System.out.println(room.getName());
		}
	}
}
