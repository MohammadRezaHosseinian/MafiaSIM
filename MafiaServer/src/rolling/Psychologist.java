/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rolling;

import Player.Player;

/**
 *
 * @author mohammadreza
 */
public class Psychologist extends Citizen {
	
	Player lastMutedPlayer;
	
	public Psychologist() {
		super();		
	}

	@Override
	public boolean hasNightAct(){
		return true;
	}
	
	public boolean canMutePlayer(Player victim){
		if(lastMutedPlayer.equals(victim))
		    return false;
		this.lastMutedPlayer = victim;
		return true;
	}
}
