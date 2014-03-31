package com.martiansoftware._2048.clients;

/**
 *
 * @author mlamb
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        switch(args.length) {
            case 0: ConsoleUI.main(args); break;
            case 1: AsyncAdventureServer.main(args); break;
            default:
                System.err.println("usage: java com.martiansoftware._2048.clients [PORT]");
                System.exit(1);
        }
    }
}
