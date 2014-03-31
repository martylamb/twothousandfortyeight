package com.martiansoftware._2048.clients;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  TODO: console
 * @author mlamb
 */
public class AsyncAdventureServer {
   
    private static void usage() {
        System.err.println("You must specify a port number.");   
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 1) usage();
        int port = 0;
        try { port = Integer.parseInt(args[0]); } catch (NumberFormatException e) { usage(); }
        
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
        final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(port));
        listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel v, Void a) {
                listener.accept(null, this);
                AsyncTextMachineSession session = new AsyncTextMachineSession(v);
                session.start();
            }

            @Override
            public void failed(Throwable t, Void a) {
                Log.log("Error accepting connection: %s", t.getMessage());
                t.printStackTrace();
            }
            
        });
        
        System.out.format("Listening for connections on port %d...\n", port);
        System.out.println("Ctrl-C to shut down.\n");
        while (true) { Thread.sleep(Integer.MAX_VALUE); }
    }

    private static class AsyncTextMachineSession implements CompletionHandler<Integer, AsyncTextMachineSession> {
        private static final byte CR = 13, LF=10;
        private static final int MAX_INPUT_LEN = 16; // minimal self-defense mechanism; discard extra chars in long input lines

        private static final AtomicLong nextId = new AtomicLong();
        
        private final AsynchronousSocketChannel _s; // the client connection
        private final String _logId;// identifies this session
        
        private final TextMachine _t; // the actual game
        private final ByteBuffer _readBuf; // buffer input - we might receive less or more than a single line at once.
        private final StringBuilder _input; // (up to) one line of user input, pulled from _readBuf
        private boolean _skipNextCharIfLF = false; // EOL is CR, LF, or CRLF.  This helps handle the latter.
        
        // don't really need the enum but it helps with clarity
        // we only use one completionhandler (this) for reading and for writing;
        // state tells us what is actually completing
        private enum STATE {READING, WRITING};
        private STATE _state;        
                
        public AsyncTextMachineSession(AsynchronousSocketChannel s) {
            _s = s;
            _readBuf = ByteBuffer.allocate(MAX_INPUT_LEN);
            _readBuf.flip(); // start out ready to read.
            _input = new StringBuilder(MAX_INPUT_LEN);
            StringBuilder logId = new StringBuilder();
            logId.append(nextId.getAndIncrement());
            try {
                logId.append(String.format("-%s", _s.getRemoteAddress().toString()));
            } catch (IOException ohWell) {}
            _logId = logId.toString();
            _t = new AdventureSession(_logId);
        }
        
        private void log(String f, Object... params) {
            Log.log(_logId, f, params);
        }        
        
        public void start() {
            log("New session started.");
            send(_t.start());
        }
        
        private void send(String s) {
            _state = STATE.WRITING;            
            _s.write(ByteBuffer.wrap(s.getBytes()), 10, TimeUnit.SECONDS, this, this);
        }

        private void processInput() {
            while (true) {                
                if (!_readBuf.hasRemaining()) { // need more from user to complete line
                    recv();
                    break;
                }
                
                byte b = _readBuf.get();
                char c = (char) b;

                if (b == 0) continue; // seems to be required for char mode telnet.  TODO investigate further
                
                if (b == LF && _skipNextCharIfLF) { // skip the LF in a CRLF pair
                    _skipNextCharIfLF = false;
                    continue;
                }
                
                if (b == CR || b == LF) { // end of a line of user input.  handle it.
                    _skipNextCharIfLF = (b == CR);
                    String input = _input.toString();
                    _input.setLength(0);
                    send(_t.handle(input));
                    break;
                }
                
                if (_input.length() < MAX_INPUT_LEN) _input.append(c);                
                _skipNextCharIfLF = false;
            }
        }
                
        private void recv() {
            _state = STATE.READING;
            _readBuf.clear();
            _s.read(_readBuf, 5, TimeUnit.MINUTES, this, this); // allow up to 5 minutes for input
        }
                
        @Override
        public void completed(Integer v, AsyncTextMachineSession a) {
            if (_t.isFinished()) {
                close();
            } else {
                if (_state == STATE.READING) {
                    if (v == -1) {
                        log("Ungraceful socket close by remote.");
                        close();
                        return;
                    }
                    _readBuf.flip();
                }
                processInput();
            }
        }

        @Override
        public void failed(Throwable t, AsyncTextMachineSession a) {
            log("Failed while %s: %s", _state.name(), t.getMessage());
            close();
        }
        
        private void close() {
            log("Closing socket.");
            try {
                _s.close();
            } catch (IOException wtf) {
                System.out.format("IOException on close: %s\n", wtf.getMessage());
            }
        }
    }
}
