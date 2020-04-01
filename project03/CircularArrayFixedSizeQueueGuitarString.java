package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {

  private IFixedSizeQueue<Double> string;
  private final static double sampRate = 44100;
  private final static double decay_fact = 0.996;
  private static Random rand = new Random();

  public CircularArrayFixedSizeQueueGuitarString(double frequency) {

    int N = (int)(sampRate / frequency) + 1;
    this.string = new CircularArrayFixedSizeQueue<>(N);

    for (int i = 0; i < N; i++)
      this.string.enqueue(0.0);
  }

  public int length() {
    return this.string.size();
  }

  public void pluck() {
    for(int i=0; i<this.length(); i++) {
      this.string.dequeue();
      this.string.enqueue(rand.nextDouble()-0.5);
    }
  }

  public void tic() {
    this.string.enqueue(decay_fact*(string.dequeue()+string.peek())/2);
  }

  public double sample() {
      return this.string.peek();
  }
}
