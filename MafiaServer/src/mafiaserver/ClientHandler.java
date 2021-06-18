/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import gameplay.Room;
import gameplay.RoomHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza In this class, we handle some problems
 */
public class ClientHandler implements Runnable {

	private final Socket connection;
	private DataOutputStream dos;
	private DataInputStream dis;
	private RoomHandler roomHandler;

	public ClientHandler(Socket connection, RoomHandler roomHandler) {
		this.connection = connection;
		this.roomHandler = roomHandler;
		try {
			dos = new DataOutputStream(this.connection.getOutputStream());
			dis = new DataInputStream(this.connection.getInputStream());
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> Constructor");
		}
	}

	@Override
	public void run() {
		this.serv();
	}
// read requests of clients

	private void serv() {
		String request;
		while (true) {
			try {
				request = dis.readUTF();
				System.out.println("get new req : " + request);
				this.parseRequest(request);
			} catch (IOException ex) {
				System.out.println(String.format("[-] the connection with ip : %s closed! ", this.connection.getInetAddress().getHostAddress()));
				return;
			}
		}
	}
//  Responds to client commands

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
				if (spliteReq[0].equals(Constants.ROUTE_LIST_ROOM_USERS)) {
					String roomName = spliteReq[1];
					this.roomUsersListCmd(roomName);
				}
				break;
			case 3:
				if (spliteReq[0].equals(Constants.ROUTE_CREATE_ROOM)) {
					this.createRoomCmd(spliteReq[1], spliteReq[2]);
				} else if (spliteReq[0].equals(Constants.ROUTE_JOIN_ROOM)) {
					roomname = spliteReq[1];
					Room room = this.roomHandler.getRoomByName(roomname);
					if (room != null) {
						room.handleReq(this.dos, spliteReq[0], spliteReq[2]);
					} else {
						System.out.println("no room with name: " + spliteReq[1]);
						this.roomHandler.showRooms();
						try {
							this.dos.writeUTF("[-] bad room name! please use lr cmd to show list of rooms!");
						} catch (IOException ex) {
							Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				} else {
					roomname = spliteReq[0];
					Room room = this.roomHandler.getRoomByName(roomname);
					if (room != null) {
						room.handleReq(this.dos, spliteReq[1], spliteReq[2]);
					}
				}
				break;
			case 4:
				roomname = spliteReq[0];
				Room room = this.roomHandler.getRoomByName(roomname);
				if (room != null) {
					room.handleReq(spliteReq[1], spliteReq[2], spliteReq[3]);
				}
				break;
			default:
				break;
		}

	}
// if client want to create room this method response to this request

	private void createRoomCmd(String roomname, String roomSize) {
		System.out.println("Create Room : " + roomname);
		int playersCount = 10;
		try {
			playersCount = Integer.parseInt(roomSize);
			if(playersCount < 10){
				this.dos.writeUTF("[-] room size must be greate than 10");
				return;
			}
			this.dos.writeUTF("ok, room with name : "+roomname +" and size : "+roomSize + " is created!");
		} catch (NumberFormatException e) {
			System.out.format("[-] Oops , can't parse %s as int", roomSize);
			return;
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> createRoomCmd");

		}

		this.roomHandler.addRoom(roomname, playersCount);
		new Thread(this.roomHandler.getRoomByName(roomname)).start();

	}

// if client want to see list of rooms this method response to this request
	private void roomListCmd() {
		try {
			System.out.println("[+] rooms list cmd func: ");
			this.dos.writeUTF(Utils.listRooms(this.roomHandler.getRooms()));
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> roomListCmd");

		}
	}

// if client want to see list of all users this method response to this request
	private void allUserListCmd() {
		System.out.println("[+] users list cmd func: ");

		try {
			this.dos.writeUTF(Utils.listAllUser(this.roomHandler.getRooms()));
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> allUserListCmd");

		}
	}

// if client want to see list of users in one room this method response to this request
	private void roomUsersListCmd(String roomName) {
		Room room = this.roomHandler.getRoomByName(roomName);
		if (room != null) {
			try {
				this.dos.writeUTF(Utils.listRoomUser(room));
			} catch (IOException ex) {
				System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> roomUseListCmd");

			}
			return;
		}
		try {
			this.dos.writeUTF(String.format(Constants.MSG_BAD_ROOM_NAME_ERRORE, roomName, Constants.ROUTE_LIST_ROOMS));
		} catch (IOException ex) {
			System.out.format(" Oops the connection is closed!:\nin mafiaserver.ClientHandler -> roomUseListCmd");

		}

	}

}
