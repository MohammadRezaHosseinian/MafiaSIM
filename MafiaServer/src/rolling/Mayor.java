/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rolling;

/**
 *
 * @author mohammadreza
 * in this class we build thread for mayor
 */
public class Mayor extends Citizen{
	private int cancelVotting;
	public Mayor(int cancelvotting) {
		super();
		this.cancelVotting = cancelvotting;
	}
	
	@Override
	public boolean hasAfterDayAct(){
		return true;
	}
	
	public boolean canCancelVotting(){
		if(this.cancelVotting > 0){
			return true;
		}
		return false;
	}
	
	public void cancelVotting(){
		if(this.canCancelVotting()){
			this.cancelVotting--;
		}
	}
}
