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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 */
public class Handler implements Runnable{

	private Player player;
	private DataInputStream input;
	private DataOutputStream out;
	private final String username;
	private GameState currentState;
	
	public Handler(String host, int port, String username) {
		Config config = Config.createConnection(host, port, username);
		Socket connection = config.getConnection();
		this.currentState = GameState.SHOW_MENU_STATE;
		
		try {
			this.out = new DataOutputStream(connection.getOutputStream());
			this.input = new DataInputStream(connection.getInputStream());
		} catch (IOException ex) {
			Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
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
	
	public GameState getGameState(){
		return this.currentState;
	}
	
	public synchronized void setGameState(GameState s){
		this.currentState = s;
	}

	public String getUsername(){
		return this.username;
	}
}
