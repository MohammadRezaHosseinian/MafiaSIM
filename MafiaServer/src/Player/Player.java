/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import java.io.DataOutputStream;
import rolling.Role;

/**
 *
 * @author mohammadreza
 */
public class Player {

	private final String username;
	private int chairNumber;
	private boolean isAlive;
	private boolean canSpeak;
	private Role role;
	private boolean isReady;
	private final DataOutputStream dos;
	private boolean canVote;

	public Player(String username, int chairNumber, DataOutputStream dos) {
		this.username = username;
		this.chairNumber = chairNumber;
		this.isAlive = true;
		this.canSpeak = false;
		this.isReady = false;
		this.canVote = false;
		this.dos = dos;
	}

	public void setIsReady(boolean isReady) {
		this.isReady = isReady;
	}

	public boolean getIsReady() {
		return isReady;
	}

	public String getUsername() {
		return username;
	}

	public int getChairNumber() {
		return chairNumber;
	}
	
	public Role getRole() {
		return role;
	}

	public void setChairNumber(int chairNumber) {
		this.chairNumber = chairNumber;
	}

	public void setRole(Role roll) {
		this.role = roll;
	}

	public boolean getIsAlive() {
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
		this.canVote = false;
	}

	public DataOutputStream getStream() {
		return this.dos;
	}

	public void setCanVote(boolean canVote) {
		this.canVote = canVote;
	}

	public boolean getCanVote() {
		return this.canVote;
	}

	public void setIsAlive(boolean b) {
		this.isAlive = true;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nname : ").append(username);
		sb.append("\nis-alive ").append(isAlive);
		sb.append("\nisReady : ").append(isReady);
		sb.append("\ncan speak : ").append(canSpeak);
		sb.append("\ncan vote : ").append(canVote);
//		sb.append("role : ").append(role.getRole());
		
		return sb.toString();
	}
}
