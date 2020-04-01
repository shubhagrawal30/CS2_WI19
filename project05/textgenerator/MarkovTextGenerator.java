package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.ChainingHashDictionary;
import edu.caltech.cs2.datastructures.IterableString;
import edu.caltech.cs2.datastructures.MoveToFrontDictionary;
import edu.caltech.cs2.datastructures.TrieMap;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;

import java.io.*;
import java.util.Scanner;

public class MarkovTextGenerator {
    public static final String CORPUS_FILE = "data/seuss.txt";
    public static final int NUMBER_OF_TEXTS_TO_GENERATE = 10;
    public static final int NUMBER_OF_SENTENCES_PER_TEXT = 5;
    public static final int MAX_LENGTH = 100000;

    public static <K, V> IDictionary<K, V> newHashTable() {
        return new ChainingHashDictionary<K, V>(MoveToFrontDictionary::new);
    }

    public static IDictionary<NGram, IDictionary<IterableString, Integer>> newOuterTrieMap() {
        return new TrieMap<>((IDeque<String> x) -> new NGram(x));
    }

    public static IDictionary<IterableString, Integer> newInnerTrieMap() {
        return new TrieMap<>((IDeque<Character> x) -> {
            char[] chars = new char[x.size()];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = x.peekFront();
                x.addBack(x.removeFront());
            }
            return new IterableString(new String(chars));
        });
    }

    public static void main(String[] args) throws IOException {
        // Input parameters
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = 2;

        Scanner reader = new Scanner(new File(CORPUS_FILE));
        reader.useDelimiter(NGram.DELIMITER);


        // Create Markov Model
        System.out.print("Generating model for N = 1...");
        NGramMap t1 = new NGramMap(reader, 1,
                MarkovTextGenerator::newHashTable,
                MarkovTextGenerator::newHashTable
        );

        System.out.println("done.");
        System.out.print("Generating model for N = 2...");
        reader = new Scanner(new File(CORPUS_FILE));
        reader.useDelimiter(NGram.DELIMITER);

        NGramMap t2 = new NGramMap(reader, 2,
                MarkovTextGenerator::newHashTable,
                MarkovTextGenerator::newHashTable
        );

        System.out.println("done.");
        System.out.println("Generating text...\n\n");

        // Text generation
        for (int j = 0; j < NUMBER_OF_TEXTS_TO_GENERATE; j++) {
            String word1 = t1.getRandomNext(new NGram("."));
            String word2 = t1.getRandomNext(new NGram(word1));
            String prevWord = word2;
            NGram ngram = new NGram(word1 + " " + word2);
            System.out.print(ngram);
            int numSentences = 0;
            for (int i = 0; i < MAX_LENGTH; i++) {
                String word = t2.getRandomNext(ngram);
                if ("!?.".contains(word)) {
                    numSentences++;
                    if (numSentences >= NUMBER_OF_SENTENCES_PER_TEXT) {
                        System.out.print(word);
                        break;
                    }
                }

                System.out.print(((NGram.NO_SPACE_AFTER.contains(prevWord) || NGram.NO_SPACE_BEFORE.contains(word)) ? "" : " ") + word);

                ngram = ngram.next(word);
                prevWord = word;
            }
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }


}
