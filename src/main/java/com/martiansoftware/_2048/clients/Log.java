package com.martiansoftware._2048.clients;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// would use slf4j for anything more complicated but it feels good to have no dependencies for once.
class Log {
    
    private static final SimpleDateFormat _fmt = new SimpleDateFormat("YYYY-MM-dd HH:MM:ss.SSS");
    private static final PrintStream out = System.out;
    
    public static void log(String id, String f, Object... o) {
        synchronized(_fmt) {
            out.printf("%s [%s] [%s]: %s\n", _fmt.format(new Date()), Thread.currentThread().getName(), id, String.format(f, o));
        }
    }
}
