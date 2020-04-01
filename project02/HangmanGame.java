package edu.caltech.cs2.project02;

import edu.caltech.cs2.project02.choosers.EvilHangmanChooser;
import edu.caltech.cs2.project02.guessers.AIHangmanGuesser;
import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import edu.caltech.cs2.project02.guessers.ConsoleHangmanGuesser;
import edu.caltech.cs2.project02.choosers.RandomHangmanChooser;

import java.util.Scanner;

public class HangmanGame {
  public static IHangmanGuesser GUESSER;
  public static IHangmanChooser CHOOSER;

  public static void main(String[] args) {
    System.out.println("Welcome to the cs2 console hangman game.");
    System.out.println();

    // set basic parameters
    Scanner console = new Scanner(System.in);
    System.out.print("What length word do you want to use? ");
    int length = console.nextInt();
    System.out.print("How many wrong answers allowed? ");
    int max = console.nextInt();
    System.out.println();

    // set up the the hangman chooser and start the game
    CHOOSER = new EvilHangmanChooser(length, max);
    GUESSER = new AIHangmanGuesser();
    playGame(CHOOSER, GUESSER);
    showResults(CHOOSER);
  }

  // Plays one game with the user
  public static void playGame(IHangmanChooser chooser, IHangmanGuesser guesser) {
    while (!chooser.isGameOver()) {
      System.out.println("guesses : " + chooser.getGuessesRemaining());
      System.out.println("guessed : " + chooser.getGuesses());
      System.out.println("current : " + chooser.getPattern());
      char ch = guesser.getGuess(chooser.getPattern(), chooser.getGuesses());
      int count = -1;
      while (count == -1) {
        try {
          count = chooser.makeGuess(ch);
        } catch (IllegalArgumentException e) {
          System.out.println("'" + ch + "' wasn't a valid guess because " + e.getMessage() + ".");
          ch = guesser.getGuess(chooser.getPattern(), chooser.getGuesses());
        }
      }

      if (count == 0) {
        System.out.println("Sorry, there are no " + ch + "'s");
      } else if (count == 1) {
        System.out.println("Yes, there is one " + ch);
      } else {
        System.out.println("Yes, there are " + count + " " + ch + "'s");
      }
      System.out.println();
    }
  }

  // reports the results of the game, including showing the answer
  public static void showResults(IHangmanChooser hangman) {
    if (hangman.getGuessesRemaining() >= 0) {
      System.out.println("That was my word! You beat me!");
    } else {
      System.out.println("Sorry, you lose!");
      String answer = hangman.getWord();
      System.out.println("My word was '" + answer + "'.");
    }
  }
}