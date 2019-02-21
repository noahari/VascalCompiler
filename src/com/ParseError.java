package com;

public class ParseError extends CompilerError{

    public ParseError(String s){
            super(s);
        }

    public static ParseError ErrorMsg(int ecode, String stacktop, String token, String line) throws ParseError{
        switch(ecode){
            case 1:
                throw new ParseError("UNRECOGNIZED TERMINAL: " + stacktop);
            case 2:
                throw new ParseError("UNEXPECTED " + token + " following " + stacktop + " at line " + line);
            case 3:
                throw new ParseError("IMPROPERLY TERMINATED STACK: " + stacktop);
        }
        return null;
    }

}
