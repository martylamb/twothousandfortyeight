package com.martiansoftware._2048.clients;

import com.martiansoftware._2048.core.Board;
import com.martiansoftware._2048.core.Game;
import com.martiansoftware._2048.core.GameListener;

/**
 *
 * @author mlamb
 */
class AdventureSession implements GameListener, TextMachine  {

    private static final int DEFAULT_WIDTH = 4;
    private static final int DEFAULT_HEIGHT = 4;
    private static final int DEFAULT_WINTILE = 2048;
    
    private final String _logId;
    private final StringBuilder _out = new StringBuilder();
    
    private Game _game;
    private int _badCommandCount = 0;
    private boolean _showedHelp = false;
    private boolean _permacheat = false;
    private boolean _justCheated = false;
    private int _cheatCount = 0;
    private boolean _finishedIntro = false;
    private boolean _logEnabled = true;
    
    public AdventureSession(String logId) {
        _logId = logId;
        restart();
    }

    public AdventureSession() {
        this(null);
    }
    
    private void log(String f, Object... params) {
        if (_logEnabled) Log.log(_logId, f, params);
    }        
    
    public void setLogEnabled(boolean logEnabled) {
        this._logEnabled = logEnabled;        
    }
    
    @Override
    public String start() {
        intro();
        return getOutput();
    }

    @Override
    public String handle(String text) {
        _justCheated = false;
        text = text.trim().toUpperCase();
        if (!_finishedIntro) {
            intro2();            
        } else switch(text) {
            case "?": case "H": case "HELP": help(); break;
            case "W": case "UP": _game.U(); break;
            case "S": case "DOWN": _game.D(); break;
            case "D": case "RIGHT": _game.R(); break;
            case "A": case "LEFT": _game.L(); break;
            case "C": case "CHEAT": cheat(); break;
            case "RESTART": restart(); break;
            case "L": case "LOOK": look(); break;
            case "QUIT": case "EXIT": quit(); break;
            case "XYZZY": permacheat(); break;
            default: badCommand(text); break;
        }
        if (!isFinished()) prompt();
        return getOutput();
    }

    @Override
    public boolean isFinished() { return _game.isGameOver(); }
        
    private void prompt() {
        if (_permacheat && !_justCheated) showBoard();
        out("\n> ");
    }
    
    private void showBoard() {
        out("\n");
        out (_game.toString());
        out("Current score: %d\n", _game.getScore());                                
    }
    
    private void permacheat() {
        if (_permacheat) {
            out("As you shout the magic word, the grid again becomes impossible to\ntake in all at once.\n");
        } else {
            out("You cast the spell and with a flash the grid becomes comprehensible\nin its entirety!\n");
        }
        _permacheat = !_permacheat;
    }
    
    private void badCommand(String cmd) {
        if (cmd.length() == 0) return;
        log("Unrecognized command \"%s\"", cmd);
        out("%s\n", oneOf("Huh?", "What?", "Come again?", "That doesn't make sense.", "I don't understand."));
        ++_badCommandCount;
        if (_badCommandCount == 3 && !_showedHelp) help();
    }

    private void initGame() {
        _game = new Game(DEFAULT_WIDTH, DEFAULT_HEIGHT, this);
        _game.setWinTile(DEFAULT_WINTILE);
    }

    private void restart() {
        initGame();
        intro();  
    }
    
    private void intro() {
        out("\n\nTwo Thousand Forty-Eight, by Marty Lamb\n");
        out("An adventurous port of Gabriele Cirulli's \"2048\"\n\n");

        out("You are standing in an open field west of a white house, with a boarded\nfront door.  ");
        out("There is a %dx%d grid here, with columns marked A-%c and rows\n", _game.colCount(), _game.rowCount(), 'A' + _game.colCount() - 1);
        out("marked 1-%d.\n\n", _game.rowCount());
        
        out("The grid is beautiful - so beautiful in fact that your mind cannot process it\n");
        out("all at once;  You can only look at parts of the grid a bit at a time.\n\n");
        
        out("Unsure how long you have been standing there, shocked at the grid's beauty,\n");
        out("you notice the wrinkled face of an old man appearing through a dirty window.\n");
        out("He gazes down at you and intones as follows:\n\n");
        
        out("\"I am the master of the grid, whose task it is to insure that none\n");
        out("but the most scholarly and masterful adventurers waste their time here.\n");
        out("You appear to be neither... but whatever.\"\n\n");
        
        out("\"You must combine grid tiles to create one with the number %d on it.\"\n\n", _game.getWinTile());
        
        out("The old man cackles and disappears.\n\n");
                
        out("(press Return...)");
        _finishedIntro = false;
    }
    
    private void intro2() {
        out("\n");
        out("You think you can still hear his voice, telling you to use 'Up', 'Down',\n");
        out("'Left', and 'Right' (or WASD if you prefer) to control the grid and to ask\n");
        out("for 'Help' if you need it.\n\n");
        out("Or maybe you are imagining that.\n\n");        
        
        out("%s\n", oneOf(
                "The grid shudders for a moment.",
                "In the distance a wolf howls.  You look at the grid.",
                "You find yourself transfixed by the grid.",
                "You realize this is stupid, but are drawn to the grid nonetheless."));
        out("Although still unable to behold the entire grid, you can make out some motion...\n");
        _game.start();
        _finishedIntro = true;
    }
    
    private void look() {
        out("I told you, you see a grid.  Sheesh.\n");
        out("Who said this was a valid command, anyway?\n\n");
    }
    
    private void cheat() {
        if (_game.getTurnCount() == 0) {
            out(oneOf(
                    "Seriously?  Only two cells are full and I JUST TOLD YOU where!\nIf you can't visualize THAT you don't stand a chance.\n",
                    "Are you kidding?  The game just started!\n",
                    "You just can't wait to cheat, can you?  At least try a move first.\n"));
        } else {
            ++_cheatCount;
            out("Cheater.\n");
            showBoard();      
            if (_cheatCount == 3) {
                out("\nYou hear the old man's echo in your mind, as if from a colossal\ncave, very far away.\n\n");
                out("\"An ancient magic incantation will save you from this trouble...\"\n");
            }
        }
        _justCheated = true;
    }
    
    private void help() {
        out("\nOK, here's how this works:\n\n");
        
        out("  1. The grid can be shifted with the commands 'Up', 'Down',\n");
        out("     'Left', and 'Right', or the letter 'W', 'A', 'S', and 'D'.\n");
        out("  2. If the description of the grid is not enough for you, use\n");
        out("     'Cheat' or 'C'.\n");
        out("  3. You can start over with 'Restart'.");
        out("  4. You can also 'Quit'\n");
        out("  5. 'Help' or '?' shows this message.\n\n");
        
        out("Some commands can be abbreviated to their first letter.\n");
        _showedHelp = true;
    }
    
    private void quit() {
        _game.quit();
    }
        
    private void out(String f, Object... o) { _out.append(String.format(f, o)); }
    
    private String getOutput() {
        String result = _out.toString();
        _out.setLength(0);
        return result;
    }

    private int randomIntBelow(int i) { return (int) (Math.random() * i);  }
    private <T> T oneOf(T... t) { return t[randomIntBelow(t.length)];  }
    
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
        out(oneOf("You watch in awe as %s is absorbed into %s,nwhich now shows the number %d.\n",
                    "A wizard casts a spell on %s and it disappears!\n%s then glows and changes to %d.\n",
                    "%s is eaten by %s, which belches and changes to %d.\n",
                    "A goblin erases %s, and replaces %s with %d.\n"
        ), from.coords, to.coords, to.get());
    }

    @Override
    public void cellAdded(Board.Cell cell) {
        out(
            oneOf("You hear a distant hum, then the number %d appears in grid cell %s.\n",
                    "Faeries appear and paint the number %d at %s.\n",
                    "Suddenly you find your body outside of your control.\nYou prick your finger and " +
                    "write the number %d at %s in your own blood.\n",
                    "The number %d slowly fades in at cell %s.\n",
                    "A unicorn scratches the number %d at %s with its horn, winks at you,\nand runs away.\n"
            ), cell.get(), cell.coords);
    }

    @Override
    public void gameOver() {
        out("\n\"Your game is over!\" you hear as you suddenly notice the old man\nbehind you.\n\n");
        out("You have no idea how long he has been standing there, and are left\nstaring at the grid:\n\n");
        out(_game.toString());
        _justCheated = true;
        out("\n");
        out("The old man is gone, but again his voice echoes in your mind:\n\n");
        out("\"Your final score was %d.  I thought you would do better than that!\n", _game.getScore());
        out("You took %d turn%s, and ", _game.getTurnCount(), _game.getTurnCount() == 1 ? "" : "s");
        if (_game.getMaxTile() == 0) out("merged no tiles!");
        else out("merged tiles up to %d.", _game.getMaxTile());
        out("\"\n\n\"You'll be back!  They always come back!\"\n\n");
        log("Game over.  Final score %d, max tile %d, total turns %d", _game.getScore(), _game.getMaxTile(), _game.getTurnCount());
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
                "An elf appears and informs you that you have earned %d more points\nfor a total of %d.\n",
                "A great clap of thunder startles you and a loud voice from the heavens\nshouts \"%d POINTS TO YOU!\"\nIf your math is right, that makes for a total score of %d.\n",
                "You earn %d more points and now have somewhere around %d.\n",
                "You got some points or something.\n",
                "You momentarily become one with the universe and, in your state of total \n" +
                "consciousness, know that you just got %d more points.\nSo you've got that going for you, which is nice.\n"
        ), newPoints, score);
    }

    @Override
    public void win() {
        out("\nThe old man reappears before you, looking uncomfortable.\n\n");
        out("\"I really never expected anyone to play the whole game,\" he says.\n\n");
        out("\"But you got yourself a genuine %d tile.  Congratulations, I guess.\n", _game.getWinTile());
        out("Since you have so much free time, go ahead and keep playing if you like.\"\n\n");
        
        out("The old man disappears.\n\n");
    }

    @Override
    public void gameStarted() {}

}
