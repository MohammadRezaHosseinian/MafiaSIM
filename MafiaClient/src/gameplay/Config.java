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
import java.util.Scanner;

/**
 *
 * @author mohammadreza
 */
public class Config {
	private final String username;
	private final Socket connection;
	
	public Config(String username , Socket s){
		this.connection = s;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public Socket getConnection() {
		return connection;
	}
	
	
	public static Config createConnection(String host, int ip, String username) {
		try {
			System.out.println("[+] Connecting to server, please wait ...");
			Socket socket = new Socket(host, ip);
			System.out.println("[+] Connection stablished!");
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			
			return new Config(username, socket);
		} catch (IOException ex) {
			System.out.println("[-] Connection failed!");
		}
		return null;
	}
}
