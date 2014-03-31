package com.martiansoftware._2048.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 *
 * @author mlamb
 */
public class ConsoleUI {
    
    public static void main(String[] args) throws IOException {
        
        LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
        AdventureSession a = new AdventureSession();
        a.setLogEnabled(false);
        
        System.out.print(a.start());
        String s = in.readLine();
        while (s != null && !a.isFinished()) {
            System.out.print(a.handle(s));
            s = a.isFinished() ? null : in.readLine();
        }

    }
}
        