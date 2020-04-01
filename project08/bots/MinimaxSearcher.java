package edu.caltech.cs2.project08.bots;

import edu.caltech.cs2.project08.game.Board;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;

public class MinimaxSearcher<B extends Board> extends AbstractSearcher<B> {
    @Override
    public Move getBestMove(B board, int myTime, int opTime) {
        BestMove best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    private static <B extends Board> BestMove minimax(Evaluator<B> evaluator, B board, int depth) {
        int minimax(Position p) {
            2 if (p is a leaf) {
                3 return p.evaluate();
                4 }
            5
            6 int bestValue = −∞;
            7 parallel (move in p.getMoves()) {
                8 p = p.copy();
                9 int value = −minimax(p);
                10 if (value > bestValue) {
                    11 bestValue = value;
                    12 }
                13 }
            14 }
        return null;
    }
}