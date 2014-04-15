package quizduell;

import java.io.Serializable;

public class Answer implements Serializable {

  private static final long serialVersionUID = 5327687360688327570L;
  private final String txt;
  private final boolean correct;

  public Answer(String text, boolean correct) {
    this.txt = text;
    this.correct = correct;
  }

  public boolean isCorrect() {
    return correct;
  }
  
  public String format() {
	  return this.txt;
  }

}
