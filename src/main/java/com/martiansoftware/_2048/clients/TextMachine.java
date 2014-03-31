package com.martiansoftware._2048.clients;

/**
 *
 * @author mlamb
 */
interface TextMachine {
    
    public String start();
    
    public String handle(String text);
    
    public boolean isFinished();
    
}
