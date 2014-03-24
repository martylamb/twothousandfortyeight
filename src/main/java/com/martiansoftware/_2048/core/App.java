package com.martiansoftware._2048.core;

import com.martiansoftware._2048.core.Board.Cell;

import java.util.List;

public class App {
    
    private static List<Cell> emptyCells(Board b) {
        List<Cell> result = new java.util.ArrayList<>();
        for (Cell c : b.cellList) if (c.isEmpty()) result.add(c);
        return result;        
    }
    
    private static void fillBoard() {
        Board b = new Board(4, 4);

        int i = 0;
        List<Cell> cells = emptyCells(b);
        while (!cells.isEmpty()) {
            cells.get((int) (Math.random() * cells.size())).set(++i);
            System.out.println(b);
            cells = emptyCells(b);
        }        
    }
    
    public static void main(String[] args) {
        Game g = new Game(5, 4);
        g.getCell(0, 1).set(1);
        g.getCell(1, 1).set(1);
        g.getCell(2, 1).set(1);
        g.getCell(3, 1).set(1);
        g.getCell(4, 1).set(1);

        
        System.out.println(g);
        g.S();
        System.out.println(g);
    }
    
}

//        Board b = new Board(5, 7);
//        int i = 0;
//        for (Cell c : b.cellList) c.set(i++);
//        System.out.println(b);       
//        
//        for (i = 0; i < 4; ++i) {
//            BoardView b9 = new BoardViewRt90(b);
//            b = new Board(b9);
//            System.out.println(b);
//        }
