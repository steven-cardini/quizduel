package quizduell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlayerFileHandler {
	private static File file = new File("players.dat");
	private static ArrayList<Player> playerCatalogue = new ArrayList<Player>();
	
	public static void loadPlayers () {
		if (!file.exists()) return;
		
		try (
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))	
			) {
			
			playerCatalogue = (ArrayList<Player>) in.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void addPlayer (String name) {
		playerCatalogue.add(new Player(name));
		savePlayers();
	}
	
	public static void savePlayers () {
		try {
			//file.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(playerCatalogue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int searchPlayer (String name) {
		for (int i=0; i<playerCatalogue.size(); i++) {
			if (playerCatalogue.get(i).getName().equals(name)) return i;
		}
		return -1;
	}
	
	public static Player getPlayer (int index) {
		return playerCatalogue.get(index);
	}
}
