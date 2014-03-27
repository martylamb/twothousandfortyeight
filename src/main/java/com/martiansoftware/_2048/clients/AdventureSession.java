package com.martiansoftware._2048.clients;

import com.martiansoftware._2048.core.Board;
import com.martiansoftware._2048.core.Game;
import com.martiansoftware._2048.core.GameListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author mlamb
 */
public class AdventureSession implements GameListener {

    private static final int DEFAULT_WIDTH = 4;
    private static final int DEFAULT_HEIGHT = 4;
    private static final int DEFAULT_WINTILE = 2048;
    
    private final LineNumberReader in;
    private final PrintWriter out;
    private final PrintWriter log;
    
    private Game game;
    private int badCommandCount = 0;
    private boolean showedHelp = false;
    private boolean permacheat = false;
    private boolean justCheated = false;
    
    public AdventureSession(InputStream in, OutputStream out, OutputStream log) {
        this.in = new LineNumberReader(new InputStreamReader(in));
        this.out = new PrintWriter(new OutputStreamWriter(out));
        this.log = (log == null) ? null : new PrintWriter(new OutputStreamWriter(log));
    }
    
    public void play() throws IOException {
        restart();
        while (!game.isGameOver()) {
            prompt();
            justCheated = false;
            switch(userAction()) {
                case "?": case "H": case "HELP": help(); break;
                   
                case "U": case "UP": game.U(); break;
                    
                case "D": case "DOWN": game.D(); break;
                    
                case "R": case "RIGHT": game.R(); break;
                    
                case "L": case "LEFT": game.L(); break;
                
                case "C": case "CHEAT": cheat(); break;
                    
                case "RESTART": restart(); break;
                    
                case "LOOK": look(); break;
                    
                case "Q": case "QUIT": case "X": case "EXIT": quit(); break;
                    
                case "XYZZY": permacheat(); break;
                    
                default: badCommand(); break;
            }
        }
    }
    
    private void prompt() {
        if (permacheat && !justCheated) showBoard();
        out("\n> ");
    }
    
    private void showBoard() {
        out("\n");
        out (game.toString());
        out("Current score: %d\n", game.getScore());                                
    }
    
    private void permacheat() {
        if (permacheat) {
            out("As you shout the magic word, the grid again becomes impossible to take in all at once.\n");
        } else {
            out("You cast the spell and with a flash the grid becomes comprehensible in its entirety!\n");
        }
        permacheat = !permacheat;
    }
    
    private void badCommand() {
        out("%s\n", oneOf("Huh?", "What?", "Come again?", "That doesn't make sense.", "I don't understand.", "You sound like an idiot."));
        ++badCommandCount;
        if (badCommandCount == 3 && !showedHelp) help();
    }

    private void initGame() {
        game = new Game(DEFAULT_WIDTH, DEFAULT_HEIGHT, this);
        game.setWinTile(DEFAULT_WINTILE);
    }

    private void restart() throws IOException {
        initGame();
        intro();        
    }
    
    private void intro() throws IOException {
        out("\n\nTwo Thousand Forty-Eight\n");
        out("------------------------\n");
        out("Marty Lamb, Martian Software, Inc.\n\n");
        out("Based on 2048 by Gabriele Cirulli, which is based on 1024 by Veewo Studio,\n");
        out("and conceptually similar to Threes by Asher Vollmer.\n\n");
                
        out("West of House\n");
        out("You are standing in an open field west of a white house, with a boarded\nfront door.  ");
        out("There is a %dx%d grid here, with columns marked A-%c and rows marked 1-%d.\n", game.colCount(), game.rowCount(), 'A' + game.colCount() - 1, game.rowCount());
        out("The grid is beautiful - so beautiful in fact that your mind cannot process it\n");
        out("all at once.  You find that the best you can do is look at small parts of the grid\n");
        out("a bit at a time.\n\n");
        
        out("Unsure how long you have been standing there, shocked at the grid's beauty,\n");
        out("you notice the wrinkled face of an old man appearing through a dirty window.\n");
        out("He gazes down at you and intones as follows:\n\n");
        
        out("\"I am the master of the grid, whose task it is to insure that none\n");
        out("but the most scholarly and masterful adventurers waste their time here.\n");
        out("You appear to be neither... but whatever.\"\n\n");
        
        out("\"You must combine grid tiles to create one with the number %d on it.\"\n\n", game.getWinTile());
        
        out("The old man cackles and disappears.\n\n");
        
        out("You think you can still hear is voice, telling you you say 'Up', 'Down',\n");
        out("'Left', and 'Right' to control the grid.  Or maybe you are imagining that.\n\n");
        
        out("(press Return...)");
        in.readLine();
        out("\n");
        out("%s\n", oneOf(
                "The grid shudders for a moment.",
                "In the distance a wolf howls.  You look at the grid.",
                "You find yourself transfixed by the grid.",
                "You realize this is stupid, but are drawn to the grid nonetheless."));
        out("Although still unable to behold the entire grid, you can make out some motion...\n");
        game.start();
    }
    
    private void look() {
        out("I told you, you see a grid.  Sheesh.\n");
        out("Who said this was a valid command, anyway?\n\n");
    }
    
    private void cheat() {
        if (game.getTurnCount() == 0) {
            out(oneOf(
                    "Seriously?  Only two cells are full and I JUST TOLD YOU where!\nIf you can't visualize THAT you don't stand a chance.\n",
                    "Are you kidding?  The game just started!\n",
                    "You just can't wait to cheat, can you?  At least try a move first.\n"));
        } else {
            out("Cheater.\n");
            showBoard();      
        }
        justCheated = true;
    }
    
    private void help() {
        out("Not too smart, are you?\n\n");
        
        out("OK, here's how this works:\n\n");
        
        out("  1. The grid can be shifted with the commands 'Up', 'Down', 'Left', and 'Right',\n"
           +"     or just the first letter of each.");
        out("  2. If the description of the grid is not enough for you, use 'Cheat' or 'C'.\n");
        out("  3. You can start over with 'Restart'.");
        out("  4. You can also 'Quit'\n");
        out("  5. 'Help' or '?' shows this message.\n\n");
        
        out("Also - any command can be abbreviated to its first letter.\n");
        showedHelp = true;
    }
    
    private void quit() {
        game.quit();
    }
    
    private String userAction() throws IOException {        
        String s = "";
        while (s.length() == 0) {
            s = in.readLine();
            if (s == null) return null;
            s = s.trim().toUpperCase();            
        }
        out("\n");
        if (log != null) log.printf("%s\n", s);
        return s;
    }
    
    private void out(String f, Object... o) {
        String msg = String.format(f, o);
        
        out.printf(msg);
        out.flush();
        
        if (log != null) {
            log.printf(msg);
            log.flush();
        }
    }

    private int randomIntBelow(int i) { return (int) (Math.random() * i);  }
    private <T> T oneOf(T... t) { return t[randomIntBelow(t.length)];  }
    
    public static void main(String[] args) throws IOException {
        AdventureSession a = new AdventureSession(System.in, System.out, null);
        a.play();
    }

    @Override
    public void turnStarted(int turnNumber) {}

    @Override
    public void turnFinished(int turnNumber) {}

    @Override
    public void turnDidNothing() {
        out("%s\n", oneOf(
                "Nothing happens.",                
                "You hear crickets.",
                "What did you think that would accomplish?"                
        ));
    }

    @Override
    public void cellMoved(Board.Cell from, Board.Cell to) {
        out(oneOf("The number %d moves from %s to %s.\n",
                    "A %d appears to teleport from %s to %s.\n",
                    "A butterfly carries the %d from %s to %s.\n",
                    "A bat carries the %d in %s to %s.\n",
                    "A %d seems to slide from %s to %s.\n",
                    "With a great flash of light, the %d in %s moves to %s.\n",
                    "A witch appears and moves the %d from %s to %s.\n",
                    "Three gnomes build a machine that moves a %d from %s to %s.\n",
                    "A dragon burns the %d in %s and sloppily replaces it at %s.\n"
            ), to.get(), from.coords, to.coords);
    }

    @Override
    public void cellsMerged(Board.Cell from, Board.Cell to) {
        out(oneOf("You watch in awe as %s is absorbed into %s, which now shows the number %d.\n",
                    "A wizard casts a spell on %s and it disappears!  %s then glows and changes to %d.\n",
                    "%s is eaten by %s, which belches and changes to %d.\n",
                    "A goblin erases %s, and replaces %s with %d.\n"
        ), from.coords, to.coords, to.get());
    }

    @Override
    public void cellAdded(Board.Cell cell) {
        out(
            oneOf("You hear a distant hum, then suddenly the number %d appears in grid cell %s.\n",
                    "Faeries appear and paint the number %d at %s.\n",
                    "Suddenly you find your body outside of your control.  You prick your finger and\n" +
                    "write the number %d at %s in your own blood.\n",
                    "The number %d slowly fades in at cell %s.\n",
                    "A unicorn scratches the number %d at %s with its horn, winks at you, and runs away.\n"
            ), cell.get(), cell.coords);
    }

    @Override
    public void gameOver() {
        out("\n\"Your game is over!\" you hear as you suddenly notice the old man behind you.\n");
        out("You have no idea how long he has been standing there, and are left staring at the grid:\n\n");
        out(game.toString());
        out("\n");
    }

    @Override
    public void gameQuit() {
        out("%s\n", oneOf(
                "I knew you didn't have it in you.",
                "Quitter.",
                "Good idea.  Get back to work."
        ));
    }

    @Override
    public void points(int newPoints, int score) {
        out(oneOf(
                "An elf appears and informs you that you have earned %d more points for a total of %d.\n",
                "A great clap of thunder startles you and a loud voice from the heavens shouts \"%d POINTS TO YOU!\"\nIf your math is right, that makes for a total score of %d.\n",
                "You earn %d more points and now have somewhere around %d.\n",
                "You got some points or something.\n",
                "You momentarily become one with the universe and, in your state of total consciousness,\n" +
                "know that you just got %d more points.  So you've got that going for you, which is nice.\n"
        ), newPoints, score);
    }

    @Override
    public void win() {
        out("\nThe old man reappears before you, looking uncomfortable.\n\n");
        out("\"I really never expected anyone to play the whole game,\" he says.\n\n");
        out("\"But you got yourself a genuine %d tile.  Congratulations, I guess.\n", game.getWinTile());
        out("Since you have so much free time, go ahead and keep playing if you like.\"\n\n");
        
        out("The old man disappears.\n\n");
    }

    @Override
    public void gameStarted() {}

}
