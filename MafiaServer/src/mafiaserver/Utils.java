/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mafiaserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohammadreza
 */
public class Utils {



	public static boolean addUser(String newUser) {
		try {
			BufferedReader userBufferedReader = new BufferedReader(new FileReader(Constants.USER_FILE_PATH));
			String userline = userBufferedReader.readLine();

			while (userline != null) {
				if (userline.equals(newUser)) {
					System.out.println("[-] Repetitious username please change your username");
					return false;
				}
				userline = userBufferedReader.readLine();

			}
			FileWriter userFileWriter = new FileWriter(Constants.USER_FILE_PATH, true);
			userFileWriter.write(newUser + "\n");
			userFileWriter.close();
			System.out.println("[+] User :" + newUser + " added");
			return true;
		
		}
		catch (FileNotFoundException ex){
			try {
				FileWriter userFileWriter = new FileWriter(Constants.USER_FILE_PATH, true);
				userFileWriter.close();
				Utils.addUser(newUser);
			} catch (IOException ex1) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex1);
			}
		}
		catch (IOException ex) {
			
		}
		return false;
	}

	public static void addRoom(String newRoom) {

		try {
			FileWriter fw = new FileWriter(Constants.ROOM_FILE_PATH, true);
			fw.write(newRoom + "\n");
			fw.close();
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static String listRoom() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(Constants.ROOM_FILE_PATH));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return "";
	}

	public static String listUser() {
		try {
			BufferedReader userBufferedReader = new BufferedReader(new FileReader(Constants.USER_FILE_PATH));
			String userline = userBufferedReader.readLine();
			StringBuilder usStringBuilder = new StringBuilder();
			while (userline != null) {
				usStringBuilder.append(userline);
				usStringBuilder.append("\n");
				userline = userBufferedReader.readLine();

			}
			return usStringBuilder.toString();
		} catch (FileNotFoundException ex){
			try {
				FileWriter userFileWriter = new FileWriter(Constants.USER_FILE_PATH, true);
				userFileWriter.close();
				Utils.listUser();
			} catch (IOException ex1) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex1);
			}
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return "";
	}
}
