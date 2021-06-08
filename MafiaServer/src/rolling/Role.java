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
public class Role {

	private final String role;
	
	public Role() {
		this.role = this.getClass().getSimpleName();
	}
	
	public String getRole(){
		return this.role;
	}
	
	public boolean hasAfterDayAct(){
		return false;
	}
	
	public boolean hasNightAct(){
		return false;
	}
}
