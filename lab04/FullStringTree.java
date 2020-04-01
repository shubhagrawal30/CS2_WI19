package edu.caltech.cs2.lab04;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class FullStringTree {
    protected static class StringNode {
        public final String data;
        public StringNode left;
        public StringNode right;

        public StringNode(String data) {
            this(data, null, null);
        }

        public StringNode(String data, StringNode left, StringNode right) {
            this.data = data;
            this.left = left;
            this.right = right;
            // Ensures that the StringNode is either a leaf or has two child nodes.
            if ((this.left == null || this.right == null) && !this.isLeaf()) {
                throw new IllegalArgumentException("StringNodes must represent nodes in a full binary tree");
            }
        }

        // Returns true if the StringNode has no child nodes.
        public boolean isLeaf() {
            return left == null && right == null;
        }
    }

    protected StringNode root;

    protected FullStringTree() {
        this.root = null;
    }

    public FullStringTree(Scanner in) {
        this.root = deserialize(in);
    }


    private StringNode deserialize(Scanner in) {
        String type = in.next();
        String data = in.nextLine().trim();
        if (type.equals("I:"))
            return new StringNode(data, deserialize(in), deserialize(in));
        return new StringNode(data);
    }

    public List<String> explore() {
        List<String> l = new ArrayList<>();
        explore(l, root);
        return l;
    }

    private void explore(List<String> l, StringNode n) {
        if (!n.isLeaf()) {
            l.add("I: " + n.data);
            explore(l, n.left);
            explore(l, n.right);
        } else {
            l.add("L: " + n.data);
        }
    }

    public void serialize(PrintStream output) {
        for(String word: explore())
            output.println(word);
    }
}