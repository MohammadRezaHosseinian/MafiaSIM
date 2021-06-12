/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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

	public ClientCommandHandler(DataOutputStream dos, Handler handler) {
		this.dos = dos;
		this.handler = handler;
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
	}

	public void showMenu() {
		System.out.println("[CR] : create room");
		System.out.println("[JR] : join room");
		System.out.println("[LR] : list room");
		System.out.println("[LUR] : list users in room");
		System.out.println("[LAU] : list all users");
		System.out.println("[RR] : ready request");
		System.out.println("[CH] : chat");
		System.out.println("[VU] : vote to user");
		System.out.println("[EX] : exit");
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
