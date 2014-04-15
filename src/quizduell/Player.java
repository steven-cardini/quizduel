package quizduell;

import java.io.Serializable;

public class Player implements Serializable {

  private static final long serialVersionUID = 3328169442151076139L;
  private String name;

  public Player(String name) {
    this.name = name;
  }
  
  public String getName() {
	  return this.name.toUpperCase();
  }
  
  public boolean equals (Player player) {
	  return player.getName().equalsIgnoreCase(this.name);
  }
  

}
