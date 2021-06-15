/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author mohammadreza in this class we handle relationship of server and client
 */
public class Handler implements Runnable {

	private DataInputStream input;
	private DataOutputStream out;
	private  String username;
	private GameState currentState;

	public Handler(String host, int port, String username) {
		Config config = Config.createConnection(host, port, username);
		Socket connection = config.getConnection();
		this.currentState = GameState.SHOW_MENU_STATE;

		try {
			this.out = new DataOutputStream(connection.getOutputStream());
			this.input = new DataInputStream(connection.getInputStream());
		} catch (IOException ex) {
			System.out.format(" Oops the server connection is closed!:\nin gameplay.Handler -> Constructor");

		}
		this.username = config.getUsername();
	}

	@Override
	public void run() {
		//
		ClientCommandHandler cch = new ClientCommandHandler(this.out, this);
		ClientReceivedDataHandler crdh = new ClientReceivedDataHandler(this.input, this);
		new Thread(cch).start();
		new Thread(crdh).start();
	}

	// Specifies the game state
	public GameState getGameState() {
		return this.currentState;
	}

	public synchronized void setGameState(GameState s) {
		this.currentState = s;
	}

	public String getUsername() {
		return this.username;
	}

	void setUserName(String newUsername) {
		this.username = newUsername;
	}
}
