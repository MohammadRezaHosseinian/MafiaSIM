/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 * In this class we created 
 * the user connection to the 
 * server by displaying the menu
 */
public class ClientCommandHandler implements Runnable {

	private final BufferedReader inputReader;
	private final DataOutputStream dos;
	private final Handler handler;
	private final Scanner input;
	private String roomName;

	// in this method we read user request 
	public ClientCommandHandler(DataOutputStream dos, Handler handler) {
		this.dos = dos;
		this.handler = handler;
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
		this.input = new Scanner(System.in);
	}
	// this method shows menu
	public void showMenu() {
		System.out.println("[CR] : " + Constant.ROUTE_CREATE_ROOM);
		System.out.println("[JR] : " + Constant.ROUTE_JOIN_ROOM);
		System.out.println("[LR] : " + Constant.ROUTE_LIST_ROOMS);
		System.out.println("[LUR] : " + Constant.ROUTE_LIST_USERS_IN_ROOM);
		System.out.println("[LAU] : " + Constant.ROUTE_LIST_ALL_USERS);
		System.out.println("[RR] : " + Constant.ROUTE_READY_PALYER);
		System.out.println("[CH] : " + Constant.ROUTE_CHAT);
		System.out.println("[VU] : " + Constant.ROUTE_VOTE);
		System.out.println("[EX] : " + Constant.EXIT);

	}

	@Override
	public synchronized void run() {
		while (true) {
			this.menu();
			this.handler.setGameState(GameState.SHOW_RECEIVED_MESSAGE_STATE);
		}

	}
	// by this method user make own choice
	private void menu() {
		this.showMenu();
		this.handler.setGameState(GameState.SHOW_RECEIVED_MESSAGE_STATE);
		this.choiceMenuItem();
	}

	// handle user choice
	private void choiceMenuItem() {

		String choice = this.input.next().toLowerCase();
		switch (choice) {
			case "cr":
				this.createRoomCmd();
				break;
			case "jr":
				this.joinRoomCmd();
				break;
			case "rr":
				this.readyRequestCmd();
				break;
			case "lr":
				this.listRoomCmd();
				break;
			case "lur":
				this.listUserInRoom();
				break;
			case "lau":
				this.listAllUsers();
				break;
			case "ch":
				this.chatCmd();
				break;
			case "vu":
				this.voteToUserCmd();
				break;
			case "ex":
				return;
			default:
				System.out.println(Constant.MSG_WRONG_CHOICE);
		}
	}

	// if user want to create room this mehod handle user's cmd
	private void createRoomCmd() {
		System.out.println(Constant.MSG_INPUT_ROOM_NAME);
		String roomname = input.next();
		System.out.println(Constant.MSG_INPUT_ROOM_SIZE);
		int roomSize = input.nextInt();
		String request = String.format("%s/%s/%d", Constant.ROUTE_CREATE_ROOM, roomname, roomSize);
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to join room this mehod handle user's cmd
	private void joinRoomCmd() {
		System.out.println(Constant.MSG_INPUT_ROOM_NAME);
		this.roomName = input.next();
		String request = String.format("%s/%s/%s", Constant.ROUTE_JOIN_ROOM, roomName, this.handler.getUsername());
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to declaration of readiness this mehod handle user's cmd
	private void readyRequestCmd() {
		String request = String.format("%s/%s/%s", this.roomName, Constant.ROUTE_READY_PALYER, this.handler.getUsername());
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to see list of rooms this mehod handle user's cmd
	private void listRoomCmd() {
		String request = String.format("%s", Constant.ROUTE_LIST_ROOMS);
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to see all users list in one room this mehod handle user's cmd
	private void listUserInRoom() {
		String request = String.format("%s/%s", Constant.ROUTE_LIST_USERS_IN_ROOM, this.roomName);
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to see all users list this mehod handle user's cmd
	private void listAllUsers() {
		String request = String.format("%s", Constant.ROUTE_LIST_ALL_USERS);
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to chat this mehod handle user's cmd
	private void chatCmd() {
		System.out.println("");
		try {
			String chatMessage = this.inputReader.readLine();
			String request = String.format("%s/%s/%s/%s", this.roomName, Constant.ROUTE_CHAT, this.handler.getUsername(), chatMessage);
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// if user want to vote this mehod handle user's cmd
	private void voteToUserCmd() {
		System.out.println("----");

		try {
			int voteNumber = this.input.nextInt();
			String request = String.format("%s/%s/%s/%d", this.roomName, Constant.ROUTE_VOTE, this.handler.getUsername(), voteNumber);
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}catch(Exception ex){
			
		}
	}

}
