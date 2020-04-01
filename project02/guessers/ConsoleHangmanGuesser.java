package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.util.Scanner;
import java.util.Set;

public class ConsoleHangmanGuesser implements IHangmanGuesser {
  private Scanner console;

  public ConsoleHangmanGuesser(Scanner console) {
    this.console = console;
  }

  @Override
  public char getGuess(String pattern, Set<Character> guesses) {
    System.out.print("Your guess? ");
    return console.next().charAt(0);
  }
}
