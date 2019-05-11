package com;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

//This class handles reading characters from an input file and skipping whitespace
class CharStream
{

    private BufferedReader stream;
    private Stack<Character> buffer;
    private int charerr;
    private int linerr;
    private boolean whitepass;

    int GetCharerr()
    {
        return charerr;
    }


    int GetLinerr()
    {
        return linerr;
    }

    boolean GetWhitepass()
    {
        return whitepass;
    }

    void SetWhitepass(boolean whitepass)
    {
        this.whitepass = whitepass;
    }


    CharStream()
    {
        Stack<Character> buffer = new Stack<>();
    }

    CharStream(String file) throws FileNotFoundException
    {
        stream = new BufferedReader(new FileReader(file));
        buffer = new Stack<>();
        charerr = 0;
        linerr = 1;
    }

    BufferedReader getStream()
    {
        return stream;
    }

    //This gets lookahead characters
    char GetChar() throws IOException
    {
        char c;
        //checks if we passed a lookahead char to read
        if (!buffer.empty())
        {
            c = buffer.pop();
        }
        //
        else if (this.Empty())
        {
            c = '!';
        } else
        {
            //
            c = (char) this.stream.read();
        }
        charerr++;
        return c;
    }

    //Pushback
    void Pushback(char c)
    {
        this.buffer.push(c);
    }

    //checks if stream is Empty
    boolean Empty() throws IOException
    {
        return !this.stream.ready();
    }

    //white checker helper
    //have to include \r since I run on windows
    Boolean WhiteCheck(char c)
    {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    //skips whitespace
    void PassWhite(char c) throws IOException, LexicalError
    {
        switch (c)
        {
            case (' '):
                break;
            case '\n':
                linerr++;
                charerr = 0;
                break;
            case '\t':
                break;
            case '{':
                while (c != '}')
                {
                    c = GetChar();
                    if (this.Empty())
                    {
                        //Malformed comment: comment never closed
                        throw LexicalError.ErrorMsg(3, linerr, charerr);
                    }
                }
                break;
        }
        whitepass = true;
    }
}
