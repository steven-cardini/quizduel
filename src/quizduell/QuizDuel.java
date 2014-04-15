package quizduell;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class QuizDuel {
	
	private static Scanner in = new Scanner(System.in);
	private static boolean playing = true;
	private static Player loggedPlayer = null;
	private static Duel activeDuel = null;
	private final static int roundSize=3; //questions per round
	private final static int numberOfRounds=6; //total number of rounds
	
	public static void main(String[] args) {
		PlayerFileHandler.loadPlayers();
		QuestionFileHandler.loadQuestions();
		DuelFileHandler.loadDuels();
		while (playing==true) showSelection();
	}
	
	private static void showSelection () {
		activeDuel=null;
		System.out.println("\nLog I)n  N)ew Duel  C)ontinue Duel  D)isplay Duels  S)how all questions  A)dd question  Log O)ut  Q)uit");
		String mode = "";
		
		if (in.hasNext()) {
			mode = in.next();
		}
		
		if (!mode.toLowerCase().matches("[incdsaoq]")) {
			System.out.println("Invalid input!");
			showSelection();
		}
		
		if (mode.toLowerCase().equals("a")) createQuestion();
		else if (mode.toLowerCase().equals("s")) showAllQuestions();
		else if (mode.toLowerCase().equals("i")) login();
		else if (mode.toLowerCase().equals("n")) newDuel();
		else if (mode.toLowerCase().equals("c")) continueDuel();
		else if (mode.toLowerCase().equals("d")) displayStatistics();
		else if (mode.toLowerCase().equals("o")) logout();
		else if (mode.toLowerCase().equals("q")) quitGame();
	}
	
	private static void login () {
		if (loggedPlayer!=null) {
			System.out.println("You are already logged in as "+loggedPlayer.getName()+"!\nPlease log out first.");
			return;
		}
		String playerName=null;
		System.out.println("User name:");
		if (in.hasNext()) playerName=in.next().toUpperCase();
		
		int index = PlayerFileHandler.searchPlayer(playerName);
		
		if (index>=0) {
			loggedPlayer=PlayerFileHandler.getPlayer(index);
		} else {
			PlayerFileHandler.addPlayer(playerName);
			index = PlayerFileHandler.searchPlayer(playerName);
			loggedPlayer=PlayerFileHandler.getPlayer(index);
		}
		
		System.out.println("\nWelcome to QuizDuel, " + loggedPlayer.getName() + "!");
	}
	
	// TODO: currently, only a random question is being displayed
	private static void newDuel () {
		if (loggedPlayer==null) {
			System.out.println("\nLog in to start a new duel!");
			return;
		}
		
		if (QuestionFileHandler.getTotalQuestionNumber()<roundSize*numberOfRounds) {
			System.out.println("\nThere are not enough questions. Please add at least "+ (roundSize*numberOfRounds-QuestionFileHandler.getTotalQuestionNumber()) +" more questions!");
			return;
		}
		
		if (DuelFileHandler.getSingleDuel(loggedPlayer)!=null) {
			activeDuel = DuelFileHandler.getSingleDuel(loggedPlayer);
			activeDuel.addOpponent(loggedPlayer);
			System.out.println("\nYou were added to a duel with " +activeDuel.getPlayer1().getName() + " as opponent!");
		} else {
			activeDuel = new Duel (loggedPlayer, roundSize, numberOfRounds);
			DuelFileHandler.addDuel(activeDuel);
			System.out.println("\nA new duel has been created!");
		}
		
		DuelFileHandler.saveDuels();
		duel();
		
	}
	
	private static void continueDuel () {
		if (loggedPlayer==null) {
			System.out.println("\nLog in to continue a duel!");
			return;
		}
		activeDuel=chooseOngoingPlayerDuel();
		if (activeDuel==null) return;
		
		DuelFileHandler.saveDuels();
		duel();
	}
	
	private static void displayStatistics () {
		if (loggedPlayer==null) {
			System.out.println("\nLog in to display your duels!");
			return;
		}
		
		ArrayList<Duel> ongoingPlayerDuels = DuelFileHandler.getOngoingPlayerDuels(loggedPlayer);
		ArrayList<Duel> finishedPlayerDuels = DuelFileHandler.getFinishedPlayerDuels(loggedPlayer);
		
		System.out.println("\nFINISHED DUELS:");
		if (finishedPlayerDuels.size()==0) System.out.println("none");
		for (int i=0; i<finishedPlayerDuels.size(); i++) {
			Duel tempDuel=finishedPlayerDuels.get(i);
			
			if (loggedPlayer.equals(tempDuel.getWinner())) {
				System.out.println("<> Duel against " + tempDuel.getOpponentName(loggedPlayer) + ".\t You won " + tempDuel.getNumberOfCorrectQuestions(loggedPlayer) + ":" + tempDuel.getNumberOfCorrectQuestions(tempDuel.getOpponent(loggedPlayer)));
			} else {
				System.out.println("<> Duel against " + tempDuel.getOpponentName(loggedPlayer) + ".\t You lost " + tempDuel.getNumberOfCorrectQuestions(loggedPlayer) + ":" + tempDuel.getNumberOfCorrectQuestions(tempDuel.getOpponent(loggedPlayer)));
			}
		}
		
		System.out.println("\nONGOING DUELS:");
		if (ongoingPlayerDuels.size()==0) System.out.println("none");
		for (int i=0; i<ongoingPlayerDuels.size(); i++) {
			Duel tempDuel=ongoingPlayerDuels.get(i);
			String firstPhrase=null;
			String score=null;
			if (tempDuel.getOpponent(loggedPlayer)==null) {
				firstPhrase = "<> Duel without opponent yet.";
				score = "Your current score is " + tempDuel.getNumberOfCorrectQuestions(loggedPlayer) + ":0";
			} else {
				firstPhrase = "<> Duel against " + tempDuel.getOpponentName(loggedPlayer) + ".\t";
				score = "Your current score is " + tempDuel.getNumberOfCorrectQuestions(loggedPlayer) + ":" + tempDuel.getNumberOfCorrectQuestions(tempDuel.getOpponent(loggedPlayer));
			}
			
			if (tempDuel.getCurrentQuestionNumber(loggedPlayer)>tempDuel.getTotalQuestionNumber()) {
				System.out.println(firstPhrase + "\tYou finished this duel. Waiting for " + tempDuel.getOpponentName(loggedPlayer) + ".\t" + score);
			} else {
				System.out.println(firstPhrase + "\tNext question: " + tempDuel.getCurrentQuestionNumber(loggedPlayer) + "/" + tempDuel.getTotalQuestionNumber() + ".\t" + score);
			}
		}
		System.out.println();
	}
	
	private static void duel () {
		boolean finished=false;
		
		if (!activeDuel.isAnswerAllowed(loggedPlayer)) {
			System.out.println("\nYou cannot continue this duel right now. You have to wait for your opponent to answer.");
			return;
		}
		
		System.out.println("\nWelcome to your duel, " + loggedPlayer.getName());
		System.out.println("\nEnter the letter of your answer or Q to quit and save the duel");
		
		while (!finished) {
			if (!activeDuel.isAnswerAllowed(loggedPlayer)) {
				System.out.println("\nYou cannot continue this duel right now. You either have to wait for your opponent to answer or the duel is finished.");
				return;
			}
			
			System.out.println("\n____________________________________________________________________");
			System.out.println("Question " + activeDuel.getCurrentQuestionNumber(loggedPlayer) + ", Round " + activeDuel.getCurrentRoundNumber(loggedPlayer));
			System.out.println("Your opponent: " + activeDuel.getOpponentName(loggedPlayer));
			System.out.println("");
			Question question = activeDuel.getCurrentQuestion(loggedPlayer);
			String questionText = activeDuel.getCurrentQuestion(loggedPlayer).format();
			Answer answerA = question.getAnswers().get(0);
			Answer answerB = question.getAnswers().get(1);
			Answer answerC = question.getAnswers().get(2);
			Answer answerD = question.getAnswers().get(3);
			System.out.println(questionText);
			System.out.println("\n____________________________________________________________________");
			
			in.nextLine();
			String input="";
			if (in.hasNext()) {
				input = in.next();
			}
			
			if (!input.toLowerCase().matches("[abcdq]")) {
				System.out.println("Invalid input!");
				continue;
			
			} else if (input.toLowerCase().matches("[abcd]")) {
				switch (input.toLowerCase()) {
				case "a":
					activeDuel.answer(loggedPlayer, answerA);
					if (answerA.isCorrect()) System.out.println("You answered correctly!");
					else System.out.println("Your answer is wrong.");
					break;
				case "b":
					activeDuel.answer(loggedPlayer, answerB);
					if (answerB.isCorrect()) System.out.println("You answered correctly!");
					else System.out.println("Your answer is wrong.");
					break;
				case "c":
					activeDuel.answer(loggedPlayer, answerC);
					if (answerC.isCorrect()) System.out.println("You answered correctly!");
					else System.out.println("Your answer is wrong.");
					break;
				case "d":
					activeDuel.answer(loggedPlayer, answerD);
					if (answerD.isCorrect()) System.out.println("You answered correctly!");
					else System.out.println("Your answer is wrong.");
					break;
				default:
					System.out.println("An unknown error occured. Please try again!");
					return;
				}
				System.out.println("Correct answer: "+ question.getCorrectAnswer().format());
			
			} else finished = true;
			
			DuelFileHandler.saveDuels();
		}
	}
	
	private static Duel chooseOngoingPlayerDuel () {
		ArrayList<Duel> ongoingPlayerDuels = DuelFileHandler.getOngoingPlayerDuels(loggedPlayer);
		
		if (ongoingPlayerDuels.size()<1) {
			System.out.println("You have no active duels, please start a new one!");
			return null;
		}
		
		System.out.println("\nEnter the number of duel to be continued. Number in brackets () -> duel cannot be continued right now.");
		for (int i=0; i<ongoingPlayerDuels.size(); i++) {
			Duel tempDuel = ongoingPlayerDuels.get(i);
			String firstPhrase=null;
			if (tempDuel.getOpponent(loggedPlayer)==null) firstPhrase = "Duel without opponent yet.";
			else firstPhrase = "Duel against " + tempDuel.getOpponentName(loggedPlayer) + ".";
			
			if (tempDuel.isAnswerAllowed(loggedPlayer))	System.out.println(i+1 + ": " + firstPhrase + "\tRound " + tempDuel.getCurrentRoundNumber(loggedPlayer) + ", Question: " + tempDuel.getCurrentQuestionNumber(loggedPlayer));
			else System.out.println("(" + (i+1) + "): "+ firstPhrase +"\tRound " + tempDuel.getCurrentRoundNumber(loggedPlayer) + ", Question: " + tempDuel.getCurrentQuestionNumber(loggedPlayer) + "/" + tempDuel.getTotalQuestionNumber() + ".");
		}
		
		in.nextLine();
		String input = "";
		if (in.hasNext()) {
			input = in.next();
		}
		if (!input.matches("\\d+") || Integer.parseInt(input)>ongoingPlayerDuels.size()) {
			System.out.println("Invalid input!");
			return null;
		} else if (!ongoingPlayerDuels.get(Integer.parseInt(input)-1).isAnswerAllowed(loggedPlayer)) {
			System.out.println("You cannot continue this duel right now. Please wait for your opponent to play.");
			return null;
		} else {
			return ongoingPlayerDuels.get(Integer.parseInt(input)-1);
		}
	}
	
	private static void createQuestion () {
		String question = null;
		Answer correctAnswer = null;
		Answer wrongAnswer1 = null;
		Answer wrongAnswer2 = null;
		Answer wrongAnswer3 = null;
		
		System.out.println("Enter Question:");
		in.nextLine();
		if (in.hasNextLine()) {
			question = in.nextLine();
		}
		
		System.out.println("Enter correct answer:");
		if (in.hasNextLine()) {
			correctAnswer = new Answer(in.nextLine(),true);
		}
		
		System.out.println("Enter first wrong answer:");
		if (in.hasNextLine()) {
			wrongAnswer1 = new Answer(in.nextLine(),false);
		}
		
		System.out.println("Enter second wrong answer:");
		if (in.hasNextLine()) {
			wrongAnswer2 = new Answer (in.nextLine(),false);
		}
		
		System.out.println("Enter third wrong answer:");
		if (in.hasNextLine()) {
			wrongAnswer3 = new Answer (in.nextLine(),false);
		}
		
		ArrayList<Answer> answers = new ArrayList<Answer>();
		answers.add(correctAnswer);
		answers.add(wrongAnswer1);
		answers.add(wrongAnswer2);
		answers.add(wrongAnswer3);
		
		QuestionFileHandler.addQuestion(question, answers);
		System.out.println("Question was added to the catalogue!");
	}
	
	private static void showAllQuestions() {
		System.out.println(QuestionFileHandler.formatQuestionCatalogue());
	}
	
	
	private static void logout() {
		if (loggedPlayer==null) {
			System.out.println("You are not logged in, hence you cannot log out!");
		} else {
			loggedPlayer=null;
			System.out.println("You are now logged out. Have a nice day!");
		}
	}
	
	private static void quitGame() {
		playing=false;
	}

}
