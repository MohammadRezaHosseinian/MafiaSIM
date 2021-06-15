/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rolling;

/**
 *
 * @author mohammadreza
 * in this class we build thread for professional
 */
public class Professional extends Citizen{
	
	public Professional() {
		super();
	}
	
	@Override
	public boolean hasNightAct(){
		return true;
	}
}
