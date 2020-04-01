package edu.caltech.cs2.project08.game;

public interface Evaluator<B extends Board> {
    /**
     * Get the score for the current edu.caltech.cs2.board position.
     * @param board edu.caltech.cs2.board to evaluate
     * @return score of the edu.caltech.cs2.board
     */
    int eval(B board);
}
