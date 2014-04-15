package quizduell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DuelFileHandler {
	private static File file = new  File ("duels.dat");
	private static ArrayList<Duel> allDuels = new ArrayList<Duel>();
	
	public static void loadDuels() {
		
		if (!file.exists()) return;
		
		try (
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))	
			) {
			
			allDuels = (ArrayList<Duel>) in.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveDuels () {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(allDuels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addDuel (Duel duel) {
		allDuels.add(duel);
		saveDuels();
	}
	
	public static Duel getSingleDuel (Player player) {
		for (int i=0; i<allDuels.size(); i++) {
			if (allDuels.get(i).isSingleDuel() && !allDuels.get(i).getPlayer1().equals(player)) {
				return allDuels.get(i);
			}
		}	
		return null;
	}
	
	public static ArrayList<Duel> getOngoingPlayerDuels (Player player) {
		ArrayList<Duel> ongoingPlayerDuels = new ArrayList<Duel>();
		for (int i=0; i<allDuels.size(); i++) {
			Duel tempDuel = allDuels.get(i);
			if ( !tempDuel.isFinished() && /* tempDuel.isAnswerAllowed(player) && */ (player.equals(tempDuel.getPlayer1())|| (tempDuel.getPlayer2()!=null&&player.equals(tempDuel.getPlayer2())) ) ) ongoingPlayerDuels.add(tempDuel);
		}
		return ongoingPlayerDuels;
	}
	
	public static ArrayList<Duel> getFinishedPlayerDuels (Player player) {
		ArrayList<Duel> finishedPlayerDuels = new ArrayList<Duel>();
		for (int i=0; i<allDuels.size(); i++) {
			Duel tempDuel = allDuels.get(i);
			if ( tempDuel.isFinished() && (player.equals(tempDuel.getPlayer1())||player.equals(tempDuel.getPlayer2())) ) finishedPlayerDuels.add(tempDuel);
		}
		return finishedPlayerDuels;
	}
}
