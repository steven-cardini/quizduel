package quizduell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Arrays;

public class QuestionFileHandler {
	private static File file = new File("questions.dat");
	private static ArrayList<Question> questionCatalogue = new ArrayList<Question>();
	
	public static void loadQuestions () {
		
		if (!file.exists()) return;
		
		try (
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))	
			) {
			
			questionCatalogue = (ArrayList<Question>) in.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			
	}
	
	public static void addQuestion (String question, ArrayList<Answer> answers) {
		questionCatalogue.add(new Question(question,answers));
		saveQuestions();
	}
	
	public static void saveQuestions () {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(questionCatalogue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String formatQuestionCatalogue () {
		String str = "";
		for (int i=0; i<questionCatalogue.size(); i++) {
			str += questionCatalogue.get(i).format();
			str += "\n \n";
		}
		return str;
	}
	
	public static int getTotalQuestionNumber() {
		return questionCatalogue.size();
	}
	
	public static ArrayList<Question> getQuestions (int numRequestedQuestions) {
		ArrayList<Question> duelQuestions = new ArrayList<Question>();
		ArrayList<Integer> usedQuestionIndexes = new ArrayList<Integer>();
		int numAvailableQuestions = questionCatalogue.size();
		int randomQuestionIndex = (int) (Math.random() * numAvailableQuestions);
				
		for (int i=0; i<numRequestedQuestions; i++) {
			duelQuestions.add(questionCatalogue.get(randomQuestionIndex));
			usedQuestionIndexes.add(randomQuestionIndex);
			
			//get next random question index
			boolean foundNewRandomIndex=false;
			while (!foundNewRandomIndex) {
				randomQuestionIndex = (int) (Math.random() * numAvailableQuestions);
				int matches=0;
				for (int k=0; k<usedQuestionIndexes.size(); k++) {
					if (randomQuestionIndex==(int)usedQuestionIndexes.get(k)) matches++;
				}
				if (matches==0) foundNewRandomIndex=true;
			}
		}
		
		return duelQuestions;
	}
	
	
}
