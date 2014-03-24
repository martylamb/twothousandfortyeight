package com.martiansoftware._2048.core;

import com.martiansoftware._2048.core.Board.Cell;

/**
 *
 * @author mlamb
 */
public interface GameListener {

    public void gameStarted();
    public void turnStarted(int turnNumber);
    public void turnFinished(int turnNumber);
    public void turnDidNothing();
    public void cellMoved(Cell from, Cell to);
    public void cellsMerged(Cell from, Cell to);
    public void cellAdded(Cell cell);
    public void gameOver();
    public void gameQuit();
    public void points(int newPoints, int score);
    public void win();
}
