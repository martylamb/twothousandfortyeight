package com.martiansoftware._2048.core;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author mlamb
 */
public class Board implements BoardView {
    
    public final int colCount, rowCount;
    public final Cell[][] cell;
    public final List<Cell> cellList;
        
    public Board(int rows, int cols) {
        if (cols < 2 || rows < 2) throw new IllegalArgumentException("Board must be at least 2x2 cells.");
        colCount = cols;
        rowCount = rows;
        cell = new Cell[rowCount][colCount];
        List<Cell> tmp = new java.util.ArrayList<>(colCount * rowCount);
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                cell[r][c] = new Cell(r, c);
                tmp.add(cell[r][c]);
            }
        }
        cellList = Collections.unmodifiableList(tmp);
    }

    public Board(BoardView bv) {
        colCount = bv.colCount();
        rowCount = bv.rowCount();
        cell = new Cell[rowCount][colCount];
        List<Cell> tmp = new java.util.ArrayList<>(colCount * rowCount);
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                cell[r][c] = new Cell(r, c);
                cell[r][c].set(bv.getCell(r, c).get());
                tmp.add(cell[r][c]);
            }
        }
        cellList = Collections.unmodifiableList(tmp);        
    }
    
    private void hr(StringBuilder s) {
        for (int c = 0; c < colCount; ++c) s.append("--------");
        s.append("\n");        
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
    
        hr(s);
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                if (c > 0) s.append(' ');
                s.append("| ");
                s.append(cell[r][c]);
                s.append(' ');
            }
            s.append("|\n");
            hr(s);
        }

        return s.toString();
    }

    @Override
    public int colCount() {
        return colCount;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    @Override
    public Cell getCell(int row, int col) {
        return cell[row][col];
    }
    
    public List<Cell> getEmptyCells() {
        List<Cell> result = new java.util.ArrayList<>();
        for (Cell c : cellList) if (c.isEmpty()) result.add(c);
        return result;
    }

    public class Cell {
        public final int row;
        public final int col;
        public final String coords;
        private int _v;
        
        private Cell(int row, int col) {
            this.row = row;
            this.col = col;
            this.coords = String.format("%c%d", 'A' + col, row + 1);
        }
        
        public int get() { return _v; }
        void set(int v) { _v = v; }      
        public boolean isEmpty() { return _v == 0; }
        void clear() { set(0); }
                
        @Override
        public String toString() {
            if (_v == 0) return "    ";
            return String.format("%4d", _v);
        }
    }
}
