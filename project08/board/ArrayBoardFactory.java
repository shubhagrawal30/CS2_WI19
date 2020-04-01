package edu.caltech.cs2.project08.board;

import edu.caltech.cs2.project08.game.BoardFactory;

public class ArrayBoardFactory implements BoardFactory<ArrayBoard> {
    private String pos;

    public void setPosition(String pos) {
        this.pos = pos;
    }

    public ArrayBoard create() {
        return new ArrayBoard(pos);
    }
}
