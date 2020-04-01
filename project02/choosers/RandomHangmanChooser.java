package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RandomHangmanChooser implements IHangmanChooser {

  private final String word;
  private int guessesLeft;
  private SortedSet<Character> guesses;
  private static Random rand = new Random();

  public RandomHangmanChooser(int wordLength, int maxGuesses) {
    if (wordLength < 1)
      throw new IllegalArgumentException("Passed argument for length of word should be positive.");
    if (maxGuesses < 1)
      throw new IllegalArgumentException("Passed argument for maximum guesses should be positive.");

    try {

      SortedSet<String> words = wordsOfLength(wordLength);

      if (words.size() == 0)
        throw new IllegalStateException("No words of length " + wordLength + " found.");

      this.word = new ArrayList<>(words).get(rand.nextInt(words.size()));
    }
    catch (FileNotFoundException e) {
      throw new IllegalStateException("No word dictionary file found.");
    }

    this.guessesLeft = maxGuesses;
    this.guesses = new TreeSet<>();
  }

  private SortedSet<String> wordsOfLength(int wordLength) throws FileNotFoundException {
    Scanner in = new Scanner(new File("data/scrabble.txt"));
    SortedSet<String> words = new TreeSet<>();

    while (in.hasNext()) {
      String word = in.next();
      if (word.length() == wordLength)
        words.add(word);
    }

    return words;
  }

  @Override
  public int makeGuess(char letter) {
    if (this.guesses.contains(letter))
      throw new IllegalArgumentException("Guess made already.");
    if (!Character.isLowerCase(letter))
      throw new IllegalArgumentException("Passed argument for letter must be lowercase.");
    if (this.guessesLeft < 0)
      throw new IllegalStateException("No guesses left.");

    int count = 0;

    for (char c : this.word.toCharArray())
      if (c == letter)
        count++;

    this.guesses.add(letter);
    if (count == 0)
      this.guessesLeft--;

    return count;
  }

  @Override
  public boolean isGameOver() {
    if (this.guessesLeft == 0)
      return true;

    boolean win = true;

    for (char letter : this.word.toCharArray())
      if (!this.guesses.contains(letter))
        win = false;

    return win;
  }

  @Override
  public String getPattern() {
    String pattern = "";

    for (char letter : this.word.toCharArray())
      if (this.guesses.contains(letter))
        pattern += letter;
      else
        pattern += "-";

    return pattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return guessesLeft;
  }

  @Override
  public String getWord() {
    System.exit(0);
    return word;
  }
}