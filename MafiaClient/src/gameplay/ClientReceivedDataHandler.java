/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 */
public class ClientReceivedDataHandler implements Runnable {

	private final DataInputStream dis;
	private final Handler handler;

	public ClientReceivedDataHandler(DataInputStream dis, Handler handler) {
		this.dis = dis;
		this.handler = handler;
	}

	@Override
	public void run() {
		String receivedData;
		while (true) {
//			if (this.handler.getGameState().equals(GameState.SHOW_MENU_STATE)) {
//				try {
//					Thread.sleep(150);
//				} catch (InterruptedException ex) {
//					Logger.getLogger(ClientReceivedDataHandler.class.getName()).log(Level.SEVERE, null, ex);
//				}
//				continue;
//			}
			try {
				receivedData = this.dis.readUTF();
				System.out.println(String.format("[+] new message from server:\t%s",receivedData));
				this.handler.setGameState(GameState.SHOW_MENU_STATE);
			} catch (IOException ex) {
				Logger.getLogger(ClientReceivedDataHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
