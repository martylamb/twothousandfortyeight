package com.martiansoftware._2048.core;

/**
 *
 * @author mlamb
 */
public class LoggingGameListener implements GameListener {
    
    private void out(String s, Object... f) {
        System.out.format(s, f);
    }
    
    @Override
    public void gameStarted() {
        out("The game has started.\n");
    }
    
    @Override
    public void turnStarted(int turnNumber) {
        out("Starting turn %d\n", turnNumber);
    }

    @Override
    public void turnFinished(int turnNumber) {
        out("Finished turn %d.\n", turnNumber);
    }
    
    @Override
    public void gameOver() {
        out("Game over.\n");
    }

    @Override
    public void cellMoved(Board.Cell from, Board.Cell to) {
        out("The value %d moved from %s to %s\n", to.get(), from.coords, to.coords);
    }

    @Override
    public void cellsMerged(Board.Cell from, Board.Cell to) {
        out("Cell %s has merged with %s and now holds the value %d.\n", from.coords, to.coords, to.get());
    }

    @Override
    public void cellAdded(Board.Cell cell) {
        out("The value %d has appeared in cell %s.\n", cell.get(), cell.coords);
    }

    @Override
    public void points(int newPoints, int score) {
        out("You have gained %d points for a total score of %d.\n", newPoints, score);
    }

    @Override
    public void gameQuit() {
        out("You have quit.\n");
    }

    @Override
    public void turnDidNothing() {
        out("Turn did nothing.\n");
    }

    @Override
    public void win() {
        out("You win!\n");
    }
    
}
