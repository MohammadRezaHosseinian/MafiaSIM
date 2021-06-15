/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author mohammadreza in this class client read server message
 */
public class ClientReceivedDataHandler implements Runnable {

	private final DataInputStream dis;
	private final Handler handler;

	public ClientReceivedDataHandler(DataInputStream dis, Handler handler) {
		this.dis = dis;
		this.handler = handler;
	}

	// in this synchronized method  all client recive the server message
	@Override
	public synchronized void run() {
		String receivedData;
		while (true) {
			try {
				receivedData = this.dis.readUTF();
				System.out.println(String.format("[+] new message from server:\t%s", receivedData));
				this.handler.setGameState(GameState.SHOW_MENU_STATE);
			} catch (IOException ex) {
				System.out.format(" Oops the server connection is closed!:\nin gameplay.ClientReceivedDataHandler -> run");

			}
		}
	}

}
