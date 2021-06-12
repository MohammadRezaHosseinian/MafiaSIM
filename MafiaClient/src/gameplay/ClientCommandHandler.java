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
 */
public class ClientCommandHandler implements Runnable {

	private final BufferedReader inputReader;
	private final DataOutputStream dos;
	private final Handler handler;
	private Scanner input;

	public ClientCommandHandler(DataOutputStream dos, Handler handler) {
		this.dos = dos;
		this.handler = handler;
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
		this.input = new Scanner(System.in);
	}

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
	public void run() {
		while (true) {
			if (this.handler.getGameState().equals(GameState.SHOW_RECEIVED_MESSAGE_STATE)) {
				try {
					Thread.sleep(150);
				} catch (InterruptedException ex) {
					Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
				}
				continue;
			}
			this.menu();
			this.handler.setGameState(GameState.SHOW_RECEIVED_MESSAGE_STATE);
		}

	}

	private void menu() {
		this.showMenu();
		this.choiceMenuItem();
	}

	private void choiceMenuItem() {
		
		String choice = this.input.next().toLowerCase();
		while (true) {

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
	}

	private void createRoomCmd() {
		System.out.println(Constant.MSG_INPUT_ROOM_NAME);
		String roomName = input.next();
		System.out.println(Constant.MSG_INPUT_ROOM_SIZE);
		int roomSize = input.nextInt();
		String request = String.format("%s/%s/%d",Constant.ROUTE_CREATE_ROOM,roomName,roomSize);
		try {
			this.dos.writeUTF(request);
		} catch (IOException ex) {
			Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void joinRoomCmd() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void readyRequestCmd() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void listRoomCmd() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void listUserInRoom() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void listAllUsers() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void chatCmd() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void voteToUserCmd() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
