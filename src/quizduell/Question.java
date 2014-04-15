package quizduell;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {

  private static final long serialVersionUID = -6390313647516594087L;
  private String question;
  private ArrayList<Answer> answers;

  public Question(String question, ArrayList<Answer> answers) {
	  this.question=question;
	  this.answers=answers;
  }
  
  public String format () {
	  this.randomizeAnswers();
	  String str = this.question + "\n A: " + this.answers.get(0).format() + "\n B: " + this.answers.get(1).format() + "\n C: " + this.answers.get(2).format() + "\n D: " + this.answers.get(3).format();
	  return str;
  }
  
  public ArrayList<Answer> getAnswers () {
	  return this.answers;
  }
  
  public Answer getCorrectAnswer () {
	  int index=-1;
	  for (int i=0; i<this.answers.size(); i++) {
		  if (this.answers.get(i).isCorrect()) index=i;
	  }
	  
	  return this.answers.get(index);
  }
  
  private void randomizeAnswers() {
	  int numAnswers = this.answers.size();
	  int[] randomIndex = new int[numAnswers];
	  randomIndex[0] = (int) (Math.random() * numAnswers);
	  randomIndex[1]=randomIndex[0];
	  while (randomIndex[1]==randomIndex[0]) {
		  randomIndex[1]=(int) (Math.random() * numAnswers);
	  }
	  randomIndex[2]=randomIndex[1];
	  while (randomIndex[2]==randomIndex[1] || randomIndex[2]==randomIndex[0]) {
		  randomIndex[2]=(int) (Math.random() * numAnswers);
	  }
	  randomIndex[3]=randomIndex[2];
	  while (randomIndex[3]==randomIndex[2] || randomIndex[3]==randomIndex[1] || randomIndex[3]==randomIndex[0]) {
		  randomIndex[3]=(int) (Math.random() * numAnswers);
	  }
	  ArrayList<Answer> tempAnswers = new ArrayList<Answer>();
	  for (int i=0; i<numAnswers; i++) {
		  tempAnswers.add(answers.get(randomIndex[i]));
	  }
	  this.answers=tempAnswers;
  }

}
