package edu.caltech.cs2.lab04;

// The QuestionsGame class can be used to play a "twenty questions" game with
// the user in which the computer attempts to guess an object the user is
// thinking of by asking yes/no questions.  If it fails to guess correctly, it
// asks the user for information about the object and adds it to the tree.  It
// includes methods for writing the current tree to a file and loadQuestions in a
// tree from a file.

import java.util.Scanner;

public class QuestionTree extends FullStringTree {
    private Scanner console;

    public QuestionTree(Scanner in) {
        super(in);
        this.console = new Scanner(System.in);
    }

    // The game guesses what the object is and updates the tree if necessary.
    private StringNode makeFinalGuess(StringNode leaf) {
        System.out.println("I guess that your object is " + leaf.data + "!");
        if (isYes("Am I right?")) {
            System.out.println("Awesome! I win!");
        }
        else {
            // Now, the game will get smarter! To learn from the loss, the game collects information about the
            // object from the user and adds the object and its corresponding distinguishing question to the tree.
            System.out.println("Boo! I Lose.  Please help me get better!");
            System.out.print("What is your object? ");
            String name = console.nextLine();
            System.out.print("Please give me a yes/no question that");
            System.out.println(" distinguishes between " + name + " and " + leaf.data + ".");
            System.out.print("Q: ");
            String question = console.nextLine();
            if (isYes("Is the answer \"yes\" for " + name + "?")) {
                leaf = new StringNode(question, new StringNode(name), leaf);
            }
            else {
                leaf = new StringNode(question, leaf, new StringNode(name));
            }
        }
        return leaf;
    }

    // Returns true if the inputted response starts with the letter "y" (upper case or lower case)
    private boolean isYes(String prompt) {
        System.out.print(prompt + " (y/n)? ");
        return console.nextLine().trim().toLowerCase().startsWith("y");
    }

    public void play() {
        play(root);
    }

    private void play(StringNode node) {
        if (node.isLeaf())
            makeFinalGuess(node);
        else
            if (isYes(node.data))
                play(node.left);
            else play(node.right);
    }
}