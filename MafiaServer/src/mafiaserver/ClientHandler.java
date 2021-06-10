/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import gameplay.Room;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 */
public class ClientHandler implements Runnable {

	private final Socket connection;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ArrayList<Room> rooms;

	public ClientHandler(Socket connection) {
		this.connection = connection;
		this.rooms = new ArrayList<>();
		try {
			dos = new DataOutputStream(this.connection.getOutputStream());
			dis = new DataInputStream(this.connection.getInputStream());
		} catch (IOException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private Room getRoom(String roomName) {
		for (Room room : rooms) {
			if (room.getName().equals(roomName)) {
				return room;
			}
		}
		return null;
	}

	@Override
	public void run() {
		this.serv();
	}

	private void serv() {
		String request;
		while (true) {
			try {
				request = dis.readUTF();
				System.out.println("get new req : " + request);
				this.parseRequest(request);
			} catch (IOException ex) {
				Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void parseRequest(String request) {
		String username, roomname, cmd;
		String[] spliteReq = request.split("/");
		switch (spliteReq.length) {
			case 1:
				if (spliteReq[0].equals(Constants.ROUTE_LIST_ROOMS)) {
					this.roomListCmd();
				}
				if (spliteReq[0].equals(Constants.ROUTE_LIST_USERS)) {
					this.allUserListCmd();
				}
				break;
			case 2:
				if(spliteReq[0].equals(Constants.ROUTE_LIST_ROOM_USERS)){
					String roomName = spliteReq[1];
					this.roomUsersListCmd(roomName);
				}
				break;
			case 3:
				if (spliteReq[0].equals(Constants.ROUTE_CREATE_ROOM)) {
					this.createRoomCmd(spliteReq[1], spliteReq[2]);
				} else {
					roomname = spliteReq[0];
					Room room = this.getRoom(roomname);
					room.handleReq(this.dos, spliteReq[1], spliteReq[2]);
				}
				break;
			default:
				break;
		}

	}

	private void createRoomCmd(String roomname, String roomSize) {
		System.out.println("Create Room : " + roomname);
		int playersCount;
		try {
			playersCount = Integer.parseInt(roomSize);
		} catch (NumberFormatException e) {
			System.out.format("[-] Oops , can't parse %s as int", roomSize);
			return;
		}

		Room room = new Room(roomname, playersCount);
		this.rooms.add(room);
		new Thread(room).start();

	}

	private void roomListCmd() {
		try {
			System.out.println("[+] rooms list cmd func: ");
			this.dos.writeUTF(Utils.listRooms(this.rooms));
		} catch (IOException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void allUserListCmd() {
		System.out.println("[+] users list cmd func: ");

		try {
			this.dos.writeUTF(Utils.listAllUser(this.rooms));
		} catch (IOException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void roomUsersListCmd(String roomName) {
		Room room = this.getRoom(roomName);
		if(room != null){
			try {
				this.dos.writeUTF(Utils.listRoomUser(room));
			} catch (IOException ex) {
				Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
			return;
		}
		try {
			this.dos.writeUTF(String.format(Constants.MSG_BAD_ROOM_NAME_ERRORE,roomName,Constants.ROUTE_LIST_ROOMS));
		} catch (IOException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

}
