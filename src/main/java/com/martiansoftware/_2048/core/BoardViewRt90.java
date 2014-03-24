package com.martiansoftware._2048.core;

/**
 *
 * @author mlamb
 */
public class BoardViewRt90 implements BoardView {

    private final BoardView _bv;
    
    public BoardViewRt90(BoardView bv) {
        _bv = bv;
    }

    @Override
    public int colCount() {
        return _bv.rowCount();
    }

    @Override
    public int rowCount() {
        return _bv.colCount();
    }

    @Override
    public Board.Cell getCell(int row, int col) {
        int r = _bv.rowCount() - col - 1;
        int c = row;
        return _bv.getCell(r, c);
    }
    
}
