package quizduell;

import java.io.Serializable;
import java.util.HashMap;

public class AnswerSet implements Serializable {

  private HashMap<Player, Answer> answers;

  public AnswerSet() {
	  this.answers = new HashMap<>();
  }


  public Answer getAnswer(Player player) {
    return this.answers.get(player);
  }

  public void setAnswer (Player player, Answer answer) {
	  this.answers.put(player, answer);
  }

}
