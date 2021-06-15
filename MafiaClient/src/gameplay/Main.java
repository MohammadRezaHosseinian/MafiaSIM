package gameplay;

import java.util.Scanner;

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
	 * In this class where the main method is located
	 * clients connect to the server
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("[+] Please enter your username");
		Scanner inputScanner = new Scanner(System.in);
		String username = inputScanner.next();
		Handler clientHandler = new Handler(Constant.SERVER_HOST, Constant.SERVER_PORT, username);
		new Thread(clientHandler).start();
	}
	
}
