/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rolling;

/**
 *
 * @author mohammadreza
 */
public class Doctor  extends Citizen{
	
	private int canSaveSelfTimes;
	
	public Doctor(int saveSelf) {
		super();
		this.canSaveSelfTimes = saveSelf;
	}
	
	public boolean checkCanSaveSelf(){
		if(this.canSaveSelfTimes > 0){
			this.canSaveSelfTimes--;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNightAct(){
		return true;
	}
	
}
