package com;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.stream.Stream;

public class CharStream {

    private BufferedReader stream;
    private Stack<Character> buffer;
    private int charerr;
    private int linerr;

    public int getCharerr() {
        return charerr;
    }


    public int getLinerr() {
        return linerr;
    }

    public CharStream() {
        Stack<Character> buffer = new Stack<>();
    }

    public CharStream(String file) throws FileNotFoundException {
        stream = new BufferedReader(new FileReader(file));
        buffer = new Stack<>();
        charerr = 0;
        linerr = 0;
    }

    public BufferedReader getStream() {
        return stream;
    }

    //used for lookahead
    public char getChar() throws IOException {
        char c;
        //checks if we passed a lookahead char to read
        if (!buffer.empty()) {
            c = buffer.pop();
        }
        //
        else if (this.empty()){
            c = '!';
        }
        else{
            //
            c = (char) this.stream.read();
        }
        charerr++;
        return c;
    }

    //pushback
    public void pushback(char c) {
        this.buffer.push(c);
    }

    //checks if stream is empty
    public boolean empty() throws IOException {
        return !this.stream.ready();
    }

    //white checker helper
    //have to include \r since I run on windows
    public Boolean whitecheck(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    //skips whitespace
    public char passWhite(char c) throws IOException, LexicalError {
        switch (c) {
            case (' '):
                break;
            case '\n':
                linerr++;
                charerr = 0;
                break;
            case '\t':
                break;
            case '{':
                while (c != '}') {
                    c = getChar();
                    if(this.empty()){
                        //Malformed comment: comment never closed
                        throw LexicalError.ErrorMsg(3, linerr, charerr);
                    }
                }
                break;
        }
        return c;
    }
}
