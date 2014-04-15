package quizduell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Duel implements Serializable {

  private static final long serialVersionUID = -331578286682418157L;
  private final int roundSize;
  private final int numberOfRounds;
  
  private Player player1;
  private Player player2;
  
  private int currentQuestionPlayer1;
  private int currentQuestionPlayer2;
  private int correctAnswersPlayer1;
  private int correctAnswersPlayer2;
  private boolean duelFinished;


  private ArrayList<Question> duelQuestions;
  private ArrayList<AnswerSet> duelAnswers;
  private HashMap<Question, AnswerSet> answeredQuestions;

  public Duel(Player player, int roundSize, int numberOfRounds) {

    this.roundSize=roundSize;
    this.numberOfRounds=numberOfRounds;
	  
	this.currentQuestionPlayer1=0;
    this.currentQuestionPlayer2=0;
    this.correctAnswersPlayer1=0;
    this.correctAnswersPlayer2=0;
    
    this.player1 = player;
    this.player2=null;
    
    this.duelQuestions = QuestionFileHandler.getQuestions(numberOfRounds*roundSize);
    this.duelAnswers = new ArrayList<AnswerSet>();
    this.answeredQuestions = new HashMap<>();
    
    for (int i=0; i<numberOfRounds*roundSize; i++) { 
    	duelAnswers.add(new AnswerSet());
    	answeredQuestions.put(duelQuestions.get(i), duelAnswers.get(i));
    }
  }
  
  public void addOpponent (Player player) {
	  this.player2=player;
  }
  
  public Question getCurrentQuestion (Player player) {
	  if (player.equals(this.player1)) return this.duelQuestions.get(currentQuestionPlayer1);
	  else return this.duelQuestions.get(currentQuestionPlayer2);
  }
  
  public int getCurrentQuestionNumber (Player player) {
	  if (player.equals(this.player1)) return this.currentQuestionPlayer1+1;
	  else return this.currentQuestionPlayer2+1;
  }
  
  public int getTotalQuestionNumber () {
	  return this.numberOfRounds*this.roundSize;
  }
  
  public int getCurrentRoundNumber (Player player) {
	  if (player.equals(this.player1)) return (this.currentQuestionPlayer1 / roundSize) + 1;
	  else return (this.currentQuestionPlayer2 / roundSize) + 1;
  }
  
  public Player getOpponent (Player player) {
	  if (player.equals(this.player1) && this.player2!=null) return this.player2;
	  else if (player.equals(this.player1) && this.player2==null) return null;
	  else return this.player1;
  }
  
  public String getOpponentName (Player player) {
	  if (player.equals(this.player1) && this.player2!=null) return this.player2.getName();
	  else if (player.equals(this.player1) && this.player2==null) return "Not yet set";
	  else return this.player1.getName();
  }
  
  public boolean isAnswerAllowed (Player player) {
	  if (this.duelFinished) return false;
	  
	  if (player.equals(player1)) {
		  return this.currentQuestionPlayer1/this.roundSize <= this.currentQuestionPlayer2/this.roundSize;
	  } else {
		  return this.currentQuestionPlayer2/this.roundSize <= this.currentQuestionPlayer1/this.roundSize;
	  }
  }
  
  public boolean isFinished () {
	  return this.duelFinished;
  }
  
  public void answer (Player player, Answer answer) {
	  this.answeredQuestions.get(getCurrentQuestion(player)).setAnswer(player, answer);
	  if (answer.isCorrect()) {
		  if (player.equals(this.player1)) this.correctAnswersPlayer1++;
		  else this.correctAnswersPlayer2++;
	  }
	  this.nextQuestion(player);
  }
  
  public boolean isSingleDuel () {
	  return this.player2==null;
  }
  
  public Player getPlayer1 () {
	  return this.player1;
  }
  
  public Player getPlayer2 () {
	  return this.player2;
  }
  
  public Player getWinner () {
	  if (this.isFinished()) {
		  if (this.correctAnswersPlayer1>this.correctAnswersPlayer2) return this.player1;
		  if (this.correctAnswersPlayer1<this.correctAnswersPlayer2) return this.player2;
	  } 
	  return null;
	  
  }
  
  public int getNumberOfCorrectQuestions (Player player) {
	  if (player.equals(this.player1)) {
		  return this.correctAnswersPlayer1;
	  } else {
		  return this.correctAnswersPlayer2;
	  }
  }
  
  private void nextQuestion(Player player) {
	  if (player.equals(this.player1)) {
		  this.currentQuestionPlayer1++;
	  }
	  
	  if (this.player2 != null && player.equals(this.player2)) {
		  this.currentQuestionPlayer2++;
	  }
	  	  
	  if (currentQuestionPlayer1>=roundSize*numberOfRounds && currentQuestionPlayer2>=roundSize*numberOfRounds) this.duelFinished=true;
  }
}
