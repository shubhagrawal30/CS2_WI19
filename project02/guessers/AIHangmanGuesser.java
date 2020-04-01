package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {

  private final static String dictionary = "data/scrabble.txt";

  @Override
  public char getGuess(String pattern, Set<Character> guesses) {

    try {
      List<String> words = availableWords(pattern);

      int maxCount = -1;
      char guess = 'a';

      for (char letter = 'a'; letter <= 'z'; letter++) {
        if (guesses.contains(letter))
          continue;
        int count = 0;
        for (String word : words) {
          for (int i = 0; i < word.length(); i++)
            if (word.charAt(i) == letter)
              count++;
        }
        if (count > maxCount) {
          maxCount = count;
          guess = letter;
        }
      }

      return guess;
    }
    catch (FileNotFoundException e){
      throw new IllegalStateException("No word dictionary file found.");
    }

  }

  private List<String> availableWords(String pattern) throws FileNotFoundException {
    Scanner in = new Scanner(new File(dictionary));
    int length = pattern.length();
    List<String> words = new ArrayList<>();

    while (in.hasNext()) {
      String word = in.next();
      if (word.length() == length && typeMatch(word, pattern))
        words.add(word);
    }

    return words;
  }

  private boolean typeMatch(String word, String pattern) {
    boolean value = true;

    for (int i = 0; i < pattern.length(); i++)
      if (pattern.charAt(i) != '-' &&
              pattern.charAt(i) != word.charAt(i))
        value = false;

    return value;
  }
}
