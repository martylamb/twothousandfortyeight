package com.martiansoftware._2048.clients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO: switch to async io.  Threads per session were trivial to implement but are terrible for scaling.
 * @author mlamb
 */
public class AdventureServer {

    private static File logDir = null;
    
    private static void startSession(final Socket s) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    String sessionID = s.getRemoteSocketAddress().toString().replaceAll(":", "_").replaceAll("/", "") + "_" + System.currentTimeMillis();                    
                    FileOutputStream fout = (logDir == null) ? null : new FileOutputStream(new File(logDir, sessionID + ".txt"));
                    System.out.format("Session started: %s\n", sessionID);
                    AdventureSession a = new AdventureSession(s.getInputStream(), s.getOutputStream(), fout);
                    a.play();
                    if (fout != null) fout.close();
                    s.close();
                    System.out.format("Session finished: %s\n", sessionID);                    
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        };
        new Thread(r).start();
    }
    
    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.err.println("Specify a log directory.");
//            System.exit(1);
//        }
//        logDir = new File(args[0]);
//        if (!logDir.isDirectory()) {
//            System.err.println("That's not a directory.");
//            System.exit(1);
//        }
        ServerSocket ss = new ServerSocket(2048);       
        System.out.println("Listening for connections...");
        while(true) startSession(ss.accept());        
    }
}
