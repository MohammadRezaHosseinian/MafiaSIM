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
public class DieHard extends Citizen{
	
	private int armorTimes;
	private int queryTimes;
	
	public DieHard(int armortimes, int query) {
		super();
		this.armorTimes = armortimes;
		this.queryTimes = query;
	}
	
	public boolean checkCanQuery(){
		if(queryTimes > 0){
			this.queryTimes--;
			return true;
		}
		return false;
	}
	
	public boolean checkHasArmor(){
		if(this.armorTimes > 0){
			this.armorTimes--;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNightAct(){
		return true;
	}
	
}
