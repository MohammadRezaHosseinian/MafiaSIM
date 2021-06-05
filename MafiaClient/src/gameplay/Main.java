package gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author mohammadreza
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			// TODO code application logic here
			Socket connection = new Socket("127.0.0.1",8080);
			System.out.println("[+] connect to server!");
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			DataInputStream dis = new DataInputStream(connection.getInputStream());
			String cmd ;
			Scanner input = new Scanner(System.in);
			while(true){
				System.out.println("[+] please insert your cmd:");
				cmd = input.next();
				dos.writeUTF(cmd);
				dos.flush();
//				System.out.println(dis.readUTF());
			}
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
