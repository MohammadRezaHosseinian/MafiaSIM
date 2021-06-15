/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import Player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import rolling.Role;
import mafiaserver.Constants;
import rolling.Detective;
import rolling.DieHard;
import rolling.Doctor;
import rolling.DoctorLecter;
import rolling.GodFather;
import rolling.Mayor;
import rolling.Professional;
import rolling.Psychologist;
import rolling.SimpleCitizen;
import rolling.SimpleMafia;

/**
 *
 * @author mohammadreza
 * In this class, we create 
 * role-playing games and
 * We randomly role each player
 * and determine the number of mafias 
 * and citizens in the game according
 * to the number of players.
 */
public class Utilty {
	// in this static method we randomly role each player
	private static ArrayList<Role> getRolesFromPlayersCount(int playerscount) {
		if (playerscount < Constants.MINIMUM_PLAYERS_COUNT) {
			return null;
		}
		ArrayList<Role> roles = new ArrayList<>();
		//<-------- create citizens roles -------->
		roles.add(Utilty.createDoctorRole());
		roles.add(Utilty.createMayorRole());
		roles.add(Utilty.creaPsychologistRole());
		roles.add(Utilty.creatDetectiveRole());
		roles.add(Utilty.createDieHardRole());
		roles.add(Utilty.createProfessionalRole());
		
		//<--------- create mafias roles -------->
		roles.add(Utilty.createDoctorLecterRole());
		roles.add(Utilty.createGodFatherRole());
		
		int mafia_team_count = (int) (playerscount * .3);
		int simple_mafia_counts = mafia_team_count - 2;
		for (int i = 0; i < simple_mafia_counts; i++) {
			roles.add(Utilty.createSimpleMafiaRole());			
		}
		int citizen_team_count = playerscount - mafia_team_count;
		int simple_citizen_count = citizen_team_count - 6;
		for (int i = 0; i < simple_citizen_count; i++) {
			roles.add(Utilty.createSimpleCitizenRole());			
		}
		Collections.shuffle(roles);
		return roles;
	}
	
	// in this static method we create doctor role
	private static Doctor createDoctorRole() {
		Doctor doctor = new Doctor(Constants.ROLE_DOCTOR_MAX_SELF_SAVE_TIMES);
		return doctor;
	}
	
	// in this static method we create detective role
	private static Detective creatDetectiveRole() {
		Detective detective = new Detective();
		return detective;
	}
	
	// in this static method we creatr diehard role
	private static DieHard createDieHardRole() {
		DieHard dieHard = new DieHard(Constants.ROLE_DIEHARD_ARMOR_TIMES, Constants.ROLE_DIEHARD_QUERY_TIMES);
		return dieHard;
	}
	
	// in this static method we create psychologist role
	private static Psychologist creaPsychologistRole() {
		Psychologist psychologist = new Psychologist();
		return psychologist;
	}

	// in this static method we create professional role
	private static Professional createProfessionalRole() {
		Professional professional = new Professional();
		return professional;
	}

	// in this static method we create simple citizen role
	private static SimpleCitizen createSimpleCitizenRole() {
		SimpleCitizen simpleCitizen = new SimpleCitizen();
		return simpleCitizen;
	}

	// in this static method we create mayor role
	private static Mayor createMayorRole() {
		Mayor mayor = new Mayor(Constants.ROLE_MAYOR_CANCEL_VOTTING);
		return mayor;
	}

	// in this static method we create simple mafia role
	private static SimpleMafia createSimpleMafiaRole() {
		SimpleMafia simpleMafia = new SimpleMafia();
		return simpleMafia;
	}

	// in this static method we create doctorlecter role
	private static DoctorLecter createDoctorLecterRole() {
		DoctorLecter doctorLecter = new DoctorLecter(Constants.ROLE_DOCTOR_LECTER_MAX_SELF_SAVE_TIMES);
		return doctorLecter;
	}

	// in this static method we create godfather role
	private static GodFather createGodFatherRole() {
		GodFather godFather = new GodFather();
		return godFather;
	}
	
	public static ArrayList<Player> assignPlayersRole(ArrayList<Player> players){
		ArrayList<Role> roles = Utilty.getRolesFromPlayersCount(players.size());
		for (int i = 0; i < players.size(); i++) {			
			players.get(i).setRole(roles.get(i));
		}
		return players;
	}
	// // in this static method we handle votting system
	public static ArrayList<Integer> votingsystemValuseList(HashMap<Player, Integer> votingsystem){
		ArrayList<Integer> values = new ArrayList<>();
		for (Integer voteCount:votingsystem.values()) {
			values.add(voteCount);
		}
		return values;
	}

}
