/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import gameplay.RoomHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 */
public class MafiaServer {

	/**
	 * In this class where
	 * the main method is located,
	 * we connected to the clients
	 * @param args
	 */
	public static void main(String[] args) {
		// create server socket and connect to clients
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(8080);
			serverSocket.setReuseAddress(true);
			RoomHandler roomHandler = new RoomHandler();
			System.out.println("[!] waitting to connection:");
			while (true) {
				Socket client = serverSocket.accept();
				System.out.format("[+] new client connected: %s -- %s\n",
						client.getInetAddress().getHostAddress(),
						client.getInetAddress().getHostName()
				);
				ClientHandler clientHandler = new ClientHandler(client, roomHandler);
				new Thread(clientHandler).start();
			}

		} catch (IOException ex) {
			Logger.getLogger(MafiaServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
