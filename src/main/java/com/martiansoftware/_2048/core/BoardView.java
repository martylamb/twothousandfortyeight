package com.martiansoftware._2048.core;

/**
 *
 * @author mlamb
 */
public interface BoardView {

    public int colCount();
    public int rowCount();
    public Board.Cell getCell(int row, int col);
}
