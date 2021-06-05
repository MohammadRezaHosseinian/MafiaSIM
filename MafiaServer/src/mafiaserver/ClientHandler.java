/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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

	public ClientHandler(Socket connection) {
		this.connection = connection;

		try {
			dos = new DataOutputStream(this.connection.getOutputStream());
			dis = new DataInputStream(this.connection.getInputStream());
		} catch (IOException ex) {
			Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
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
					this.userListCmd();
				}
				break;
			case 2:
				break;
			case 3:
				username = spliteReq[0];
				roomname = spliteReq[1];
				cmd = spliteReq[2];
				if (cmd.equals(Constants.ROUTE_CREATE_ROOM)) {
					this.createRoomCmd(roomname);
				}
				if (cmd.equals(Constants.ROUTE_JOIN_ROOM)) {
					this.joinRoomCmd(username, roomname);
				}
				break;
			default:
				break;
		}

	}

	private void createRoomCmd(String roomname) {
		System.out.println("Create Room : " + roomname);
		Utils.addRoom(roomname);
	}

	private void joinRoomCmd(String username, String roomname) {
		boolean condition = Utils.addUser(username);
		if (condition) {
			System.out.format("[+] join %s  to %s\n", username, roomname);
		} else {
			System.out.format("[-] %s can not join\n", username);
		}
	}

	private void roomListCmd() {
		System.out.println("[+] rooms list cmd func: ");
		System.out.println(Utils.listRoom());
	}

	private void userListCmd() {
		System.out.println("[+] users list cmd func: ");
		System.out.println(Utils.listUser());
	}

}
