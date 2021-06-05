/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import rolling.Roll;

/**
 *
 * @author mohammadreza
 */
public class Player {

	private final String username;
	private int chairNumber;
	private boolean isAlive;
	private boolean canSpeak;
	private Roll roll;

	public Player(String username, int chairNumber) {
		this.username = username;
		this.chairNumber = chairNumber;
		this.isAlive = true;
		this.canSpeak = false;
	}

	public String getUsername() {
		return username;
	}

	public int getChairNumber() {
		return chairNumber;
	}

	public Roll getRoll() {
		return roll;
	}

	public void setChairNumber(int chairNumber) {
		this.chairNumber = chairNumber;
	}

	public void setRoll(Roll roll) {
		this.roll = roll;
	}

	public boolean isIsAlive() {
		return isAlive;
	}

	public boolean isCanSpeak() {
		return canSpeak;
	}

	public void setCanSpeak(boolean canSpeak) {
		this.canSpeak = canSpeak;
	}

	public void kill() {
		this.isAlive = false;
		this.canSpeak = false;
	}

}
