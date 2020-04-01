package edu.caltech.cs2.project02.interfaces;

import java.util.Set;

public interface IHangmanGuesser {
  public char getGuess(String pattern, Set<Character> guesses);
}
