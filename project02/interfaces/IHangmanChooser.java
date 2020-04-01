package edu.caltech.cs2.project02.interfaces;

        import java.util.SortedSet;

public interface IHangmanChooser {
  public int makeGuess(char letter);
  public boolean isGameOver();
  public String getPattern();
  public SortedSet<Character> getGuesses();
  public int getGuessesRemaining();
  public String getWord();
}
