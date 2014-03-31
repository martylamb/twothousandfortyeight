twothousandfortyeight
=====================

    ...
    
    Unsure how long you have been standing there, shocked at the grid's beauty,
    you notice the wrinkled face of an old man appearing through a dirty window.
    He gazes down at you and intones as follows:
    
    "I am the master of the grid, whose task it is to insure that none
    but the most scholarly and masterful adventurers waste their time here.
    You appear to be neither... but whatever."
            
    "You must combine grid tiles to create one with the number 2048 on it."
    
    ....

Twothousandfortyeight is a text adventure version of the game [2048](http://gabrielecirulli.github.io/2048/).

**Note:** to call the parser rudimentary would be an understatement.  Read the in-game instructions for help.

Play now on a raspberry pi server: **telnet twothousandfortyeight.martiansoftware.com 2048**<br/>
*&#42;Guaranteed to be only occasionally available*


Or build and launch yourself with maven:
  * Standalone from the command line via **mvn compile exec:java.
  * Or launch a server listening for telnet connections via **mvn compile exec:java -Dexec.args="PORTNUMBER"**, where PORTNUMBER is the port you want to listen on.  Players can connect to the server via **telnet HOSTNAME PORT**.


