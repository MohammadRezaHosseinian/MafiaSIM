/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplay;

import Player.Player;
import java.util.ArrayList;
import java.util.Collections;
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
 */
public class Utilty {

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

	private static Doctor createDoctorRole() {
		Doctor doctor = new Doctor(Constants.ROLE_DOCTOR_MAX_SELF_SAVE_TIMES);
		return doctor;
	}

	private static Detective creatDetectiveRole() {
		Detective detective = new Detective();
		return detective;
	}

	private static DieHard createDieHardRole() {
		DieHard dieHard = new DieHard(Constants.ROLE_DIEHARD_ARMOR_TIMES, Constants.ROLE_DIEHARD_QUERY_TIMES);
		return dieHard;
	}

	private static Psychologist creaPsychologistRole() {
		Psychologist psychologist = new Psychologist();
		return psychologist;
	}

	private static Professional createProfessionalRole() {
		Professional professional = new Professional();
		return professional;
	}

	private static SimpleCitizen createSimpleCitizenRole() {
		SimpleCitizen simpleCitizen = new SimpleCitizen();
		return simpleCitizen;
	}

	private static Mayor createMayorRole() {
		Mayor mayor = new Mayor(Constants.ROLE_MAYOR_CANCEL_VOTTING);
		return mayor;
	}

	private static SimpleMafia createSimpleMafiaRole() {
		SimpleMafia simpleMafia = new SimpleMafia();
		return simpleMafia;
	}

	private static DoctorLecter createDoctorLecterRole() {
		DoctorLecter doctorLecter = new DoctorLecter(Constants.ROLE_DOCTOR_LECTER_MAX_SELF_SAVE_TIMES);
		return doctorLecter;
	}

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

}
