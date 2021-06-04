/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author mohammadreza
 */
public class Handler {
	private Player player;
	private Socket connection;
	private DataInputStream  input;
    private DataOutputStream out;
	private String username;
	public Handler(String host, int port, String username){
		this.connection = ConnectionHandler.createConnection(host, port, username);
	}
	
}
