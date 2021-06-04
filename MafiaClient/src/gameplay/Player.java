/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 *
 * @author mohammadreza
 */
public class Player {

	private String username;
	private int chairNumber;
	private String roll;
	private boolean isAlive;
	private boolean canSpeak;
	private final BufferedReader inputReader;

	public Player(String username, int chairNumber, String roll) {
		this.username = username;
		this.chairNumber = chairNumber;
		this.roll = roll;
		this.isAlive = true;
		this.canSpeak = false;
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
	}

	public String getUsername() {
		return username;
	}

	public int getChairNumber() {
		return chairNumber;
	}

	public String getRoll() {
		return roll;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setChairNumber(int chairNumber) {
		this.chairNumber = chairNumber;
	}

	public void setRoll(String roll) {
		this.roll = roll;
	}

	public boolean getIsAlive() {
		return this.isAlive;
	}

	public void killed() {
		this.isAlive = false;
	}

	public boolean getCanSpeak() {
		return this.canSpeak;
	}

	public void setCanSpeak(boolean v) {
		this.canSpeak = v;
	}

	public String chat() {
		if (this.canSpeak) {
			System.out.println("[+] You can chat, please be nice in the chat!");
			try {
				return this.inputReader.readLine();
			} catch (IOException ex) {
				System.out.println("[-] Oops, an error occured :(");
				return null;
			}
		}
		System.out.println("[-] You have'nt permision to chat now!");
		return null;
	}
}
