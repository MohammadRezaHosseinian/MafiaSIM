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
public class DoctorLecter extends Mafia {
	
	private int saveSelfTimes;
	
	public DoctorLecter(int times) {
		super();
		this.saveSelfTimes = times;
	}
	
	public boolean canSaveSelf(){
		if(saveSelfTimes > 0){
			this.saveSelfTimes--;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNightAct(){
		return true;
	}
}
