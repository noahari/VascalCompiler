package com;

public class ParseError extends CompilerError{

    public ParseError(String s){
            super(s);
        }

    public static ParseError ErrorMsg(int ecode, String stacktop, Token token) throws ParseError{
        switch(ecode){
            case 1:
                throw new ParseError("UNRECOGNIZED TERMINAL: " + stacktop);
            case 2:
                throw new ParseError("UNRECOGNIZED NON TERMINAL: " + stacktop);
            case 3:
                throw new ParseError("IMPROPERLY TERMINATED STACK: " + stacktop);
        }
        return null;
    }

}
