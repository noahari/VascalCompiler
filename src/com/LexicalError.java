package com;

public class LexicalError extends CompilerError {
    public LexicalError(String s){
        super(s);
    }

    public static LexicalError ErrorMsg(int ecode, int linerr, int charerr) throws LexicalError{
        switch(ecode){
            case 1:
                throw new LexicalError("INVALID CHARACTER at line " + Integer.toString(linerr) + ", character " + Integer.toString(charerr));
            case 2:
                throw new LexicalError("MALFORMED CONSTANT at line " + Integer.toString(linerr) + ", character " + Integer.toString(charerr));
            case 3:
                throw new LexicalError("MALFORMED COMMENT at line " + Integer.toString(linerr) + ", character " + Integer.toString(charerr));
            case 4:
                throw new LexicalError("IDENTIFIER TOO LONG at line " + Integer.toString(linerr) + ", character " + Integer.toString(charerr));
            //an extra error for added specificity during debugging, and possibly worth leaving in for the imaginary user experience
            case 5:
                throw new LexicalError("IDENTIFIER ADJACENT TO ALPHANUMERIC CHARACTERS at line " + Integer.toString(linerr) + ", character " + Integer.toString(charerr));
        }
        return null;
    }

}
