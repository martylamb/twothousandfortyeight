package com.martiansoftware._2048.core;

import com.martiansoftware._2048.core.Board.Cell;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mlamb
 */
public class Game implements BoardView {

    private final Board _board;
    private final GameListenerCollection _listeners = new GameListenerCollection();
    private int _turn = 0;
    private boolean _gameOver = false, _quit = false, _win = false;
    private int _turnScore = 0, _totalScore = 0;
    private int _winTile = 2048;
    private boolean _started = false;
    
    // rotated views of the board in which the named direction is considered "down"
    private final BoardView NORTH_DOWN, SOUTH_DOWN, EAST_DOWN, WEST_DOWN;
    
    public Game(int colCount, int rowCount, GameListener... initialListeners) {
        for (GameListener l : initialListeners) _listeners.add(l);
        
        _board = new Board(rowCount, colCount);
        SOUTH_DOWN = _board;
        EAST_DOWN = new BoardViewRt90(SOUTH_DOWN);
        NORTH_DOWN = new BoardViewRt90(EAST_DOWN);
        WEST_DOWN = new BoardViewRt90(NORTH_DOWN);
        
    }

    public void N() { doTurn(NORTH_DOWN); }
    public void S() { doTurn(SOUTH_DOWN); }
    public void E() { doTurn(EAST_DOWN); }
    public void W() { doTurn(WEST_DOWN); }
    public boolean isGameStarted() { return _started; }
    public boolean isGameOver() { return _gameOver; }
    public boolean isGameQuit() { return _quit; }
    public int getScore() { return _totalScore; }
    public int getTurnCount() { return _turn; }
    public int getWinTile() { return _winTile; }
    public void setWinTile(int winTile) { _winTile = winTile; }
    
    @Override
    public int colCount() { return _board.colCount; }

    @Override
    public int rowCount() { return _board.rowCount; }  
    
    @Override
    public Board.Cell getCell(int row, int col) { return _board.getCell(row, col); }
    
    @Override
    public String toString() { return _board.toString(); }

    public void start() {
        if (!_started) {
            fillAnEmptyCell();
            fillAnEmptyCell();
            _started = true;
        }
    }
    
    public void quit() {
        _quit = true;
        _listeners.gameQuit();
        endGame();
    }   
    
    // a single turn.  BoardView is the view that makes the direction the player moved "down"
    private void doTurn(BoardView bv) {
        boolean alreadyWon = _win;
        _turnScore = 0;
        _listeners.turnStarted(_turn);
        if (!collapse(bv, false)) _listeners.turnDidNothing();
        fillAnEmptyCell();
        if (_turnScore != 0) {
            _totalScore += _turnScore;
            _listeners.points(_turnScore, _totalScore);
        }
        _listeners.turnFinished(_turn++);
        if (_win && !alreadyWon) {
            _listeners.win();
        }
        if (!hasMoves()) endGame();
    }

    // returns null if board is full
    private Cell getRandomEmptyCell() {
        List<Cell> e= _board.getEmptyCells();
        if(e.isEmpty()) return null;
        return e.get((int) (Math.random() * e.size()));
    }
    
    private void fillAnEmptyCell() {
        Cell c = getRandomEmptyCell();
        if (c != null) {
            c.set(2);
            _listeners.cellAdded(c);
        }
    }
    
    private void endGame() {
        _gameOver = true;
        _listeners.gameOver();
    }    
    
    private boolean hasMoves() {
        return collapse(NORTH_DOWN, true) || collapse(SOUTH_DOWN, true) || collapse(EAST_DOWN, true) || collapse(WEST_DOWN, true);
    }
    
    /**
     * "Collapses" the board downwards according to the specified view, moving and merging cells as appropriate.
     * @param bv the view that considers the direction the player moved "down"
     * @param justChecking if true, don't actually change the board; we're just checking if a valid move exists.
     * @return did anything change, or would anything have changed if just checking for moves?
     */
    private boolean collapse(BoardView bv, boolean justChecking) {
        boolean result = false;
        for (int c = 0; c < bv.colCount(); ++c) {
            result |= collapseColumn(bv, c, justChecking);
            if (justChecking && result) return true;
        }
        return result;
    }
    
    /**
     * Checks to see if two cells can be merged, and optionally merges them
     * @param from the cell we might be merging from.  After a merge, this cell will be empty.
     * @param to the cell we might be merging to.  After a merge, this cell contains the sum of the merged cells.
     * @param justChecking if true, don't actually merge the cells; we're just checking if we can.
     * @return did anything change, or would anything have changed if just checking for moves?
     */
    private boolean merge(Cell from, Cell to, boolean justChecking) {
        if (!to.isEmpty() && from.get() == to.get()) {
            if (justChecking) return true;
            to.set(from.get() + to.get());            
            _turnScore += to.get();
            from.clear();
            if (!_win && to.get() == _winTile) _win = true;
            return true;
        }
        return false;
    }
    
    /**
     * "Collapses" a single column downwards according to the specified view, moving and merging cells as appropriate.
     * @param bv the view that considers the direction the player moved "down"
     * @param col the column to collapse
     * @param justChecking if true, don't actually change the board; we're just checking if a valid move exists.
     * @return did anything change, or would anything have changed if just checking for moves?
     */
    private boolean collapseColumn(BoardView bv, int col, boolean justChecking) {
        boolean result = false;
        int bottom = bv.rowCount() - 1;
        int lastMerge = bv.rowCount(); // track to prevent double-merging
        for (int r = bv.rowCount() - 1; r >= 0; --r) {
            Cell cR = bv.getCell(r, col);
            if (!cR.isEmpty()) {
                Cell cBottom = bv.getCell(bottom, col);
                if (r != bottom) { 
                    result = true;
                    if (justChecking) return true;
                    cBottom.set(cR.get());
                    cR.set(0);
                    _listeners.cellMoved(cR, cBottom);
                }
                if (bottom < bv.rowCount() - 1) { // only if bottom is not in the last row                    
                    if (lastMerge != bottom + 1 && merge(cBottom, bv.getCell(bottom + 1, col), justChecking)) {
                        result = true;
                        if (justChecking) return true;
                        _listeners.cellsMerged(cBottom, bv.getCell(bottom + 1, col));
                        ++bottom;
                        lastMerge = bottom;
                    }
                }
                --bottom;
            }
        }
        return result;
    }
        
    public void addGameListener(GameListener gl) { _listeners.add(gl); }
    public void removeGameListener(GameListener gl) { _listeners.remove(gl); }

    private class GameListenerCollection implements GameListener {
        private Collection<GameListener> _listeners = new java.util.LinkedHashSet<>();
        public void add(GameListener gl) { _listeners.add(gl); }
        public void remove(GameListener gl) { _listeners.remove(gl); }

        @Override
        public void turnStarted(int turnNumber) {
            for (GameListener gl : _listeners) gl.turnStarted(turnNumber);
        }

        @Override
        public void turnFinished(int turnNumber) {
            for (GameListener gl : _listeners) gl.turnFinished(turnNumber);
        }
        
        @Override
        public void gameOver() {
            for (GameListener gl : _listeners) gl.gameOver();
        }

        @Override
        public void cellMoved(Cell from, Cell to) {
            for (GameListener gl : _listeners) gl.cellMoved(from, to);
        }

        @Override
        public void cellsMerged(Cell from, Cell to) {
            for (GameListener gl : _listeners) gl.cellsMerged(from, to);
        }

        @Override
        public void cellAdded(Cell cell) {
            for (GameListener gl : _listeners) gl.cellAdded(cell);
        }

        @Override
        public void points(int newPoints, int score) {
            for (GameListener gl : _listeners) gl.points(newPoints, score);
        }

        @Override
        public void gameQuit() {
            for (GameListener gl : _listeners) gl.gameQuit();
        }

        @Override
        public void turnDidNothing() {
            for (GameListener gl : _listeners) gl.turnDidNothing();
        }

        @Override
        public void win() {
            for (GameListener gl : _listeners) gl.win();            
        }

        @Override
        public void gameStarted() {
            for (GameListener gl : _listeners) gl.gameStarted();            
        }
    }
}
