package com.martiansoftware._2048.clients;

import com.martiansoftware._2048.core.Game;
import com.martiansoftware._2048.core.LoggingGameListener;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 *
 * @author mlamb
 */
public class ConsoleClient {
    private static LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
    
    private static void out(String f, Object... o) {
        System.out.printf(f, o);
    }
    
    private static void out(Object o) {
        System.out.printf(o.toString());
    }
    
    private static String in() throws IOException {
        return in.readLine();
    }
    
    public static void main(String[] args) throws IOException {
        
        Game g = new Game(17, 2, new LoggingGameListener());
        g.setWinTile(2048);
        while(!g.isGameOver()) {
            out(g);
            out("N,S,E,W: ");                    
            String cmd = in().trim().toUpperCase();
            switch(cmd) {
                case "N": g.N(); break;
                case "S": g.S(); break;
                case "E": g.E(); break;
                case "W": g.W(); break;
                case "Q": g.quit(); break;
                default: out("Huh?\n");
            }
        }
        
        System.out.format("You scored a total of %d points in %d turns.\n", g.getScore(), g.getTurnCount());
    }
}
        