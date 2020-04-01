package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {

  private int guessesLeft;
  private SortedSet<Character> guesses;
  private String pattern;
  private SortedSet<String> words;

  public EvilHangmanChooser(int wordLength, int maxGuesses){
    if (wordLength < 1)
      throw new IllegalArgumentException("Passed argument for length of word should be positive.");
    if (maxGuesses < 1)
      throw new IllegalArgumentException("Passed argument for maximum guesses should be positive.");

    try {

      SortedSet<String> words = wordsOfType(wordLength);

      if (words.size() == 0)
        throw new IllegalStateException("No words of length " + wordLength + " found.");

      this.words = words;

    }
    catch (FileNotFoundException e) {
      throw new IllegalStateException("No word dictionary file found.");
    }

    this.pattern = "";
    for (int i = 0; i < wordLength; i++)
      this.pattern += "-";
    this.guessesLeft = maxGuesses;
    this.guesses = new TreeSet<>();

  }


  private SortedSet<String> wordsOfType(int wordLength) throws FileNotFoundException {
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

    Map<String, SortedSet<String>> families= new HashMap<>();

    for (String word : this.words){

      String type = typeOfWord(word, letter);
      if (families.get(type) == null)
        families.put(type, new TreeSet<String>());
      families.get(type).add(word);

    }

    int maxSize = 0;
    String finalPattern = this.pattern;

    for (String family : families.keySet()) {
      int size = families.get(family).size();
      if (size > maxSize) {
        maxSize = size;
        finalPattern = family;
      }
      if (size == maxSize) {
        finalPattern = (finalPattern.compareTo(family) < 0) ? finalPattern : family;
      }
    }

    this.pattern = finalPattern;
    this.words = families.get(finalPattern);

    int count = 0;

    for (int i = 0; i < this.pattern.length(); i++)
      if (letter == this.pattern.charAt(i))
        count ++;

    this.guesses.add(letter);
    if (count == 0)
      this.guessesLeft--;

    return count;
  }

  private String typeOfWord(String word, char guess) {

    String type = "";

    for (int i = 0; i < word.length(); i++){
      if (word.charAt(i) == guess)
        type += guess;
      else type += this.pattern.charAt(i);
    }
    return type;
  }


  @Override
  public boolean isGameOver() {
    if (this.guessesLeft == 0)
      return true;

    boolean win = true;

    for (char letter : this.pattern.toCharArray())
      if (letter == '-')
        win = false;

    return win;
  }

  @Override
  public String getPattern() {
    return this.pattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return this.guessesLeft;
  }

  @Override
  public String getWord() {
    System.exit(0);
    return this.pattern;
  }
}