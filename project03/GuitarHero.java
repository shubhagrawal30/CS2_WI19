package edu.caltech.cs2.project03;

import edu.caltech.cs2.project03.libraries.StdAudio;
import edu.caltech.cs2.project03.libraries.StdDraw;

public class GuitarHero {
  private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
  private static final double CONCERT_A = 440.0;

  public static void main(String[] args) {

    // Create three arrays of strings
    CircularArrayFixedSizeQueueGuitarString[] strings = new CircularArrayFixedSizeQueueGuitarString[KEYBOARD.length()];

    // Populate string arrays with respective string types
    for (int i = 0; i < KEYBOARD.length(); i++) {
      strings[i] = new CircularArrayFixedSizeQueueGuitarString(CONCERT_A * Math.pow(2, (i - 24.0) / 12.0));
    }

    while (true) {
      // check if the user has typed a key; if so, process it
      if (StdDraw.hasNextKeyTyped()) {
        char key = StdDraw.nextKeyTyped();
        int idx = KEYBOARD.indexOf(key);
        if (idx != -1) {
          strings[idx].pluck();
        }
      }

      // compute the superposition of samples
      double sample = 0.0;
      for (int i = 0; i < strings.length; i++) {
        sample += strings[i].sample();
      }

      // play the sample on standard audio
      StdAudio.play(sample);

      // advance the simulation of each guitar string by one step
      for (int i = 0; i < strings.length; i++) {
        strings[i].tic();
      }
    }
  }
}
