package com;

public class SemanticActionError extends CompilerError {
    public SemanticActionError(String s){
        super(s);
    }

    public static SemanticActionError ErrorMsg(int ecode, Token t) throws SemanticActionError{
        switch(ecode){
            case 1:
                throw new SemanticActionError("Undeclared Variable Error \n Token caught: " + t.getType() + ", " + t.getVal());
            case 2:
                throw new SemanticActionError("Type Mismatch Error \n Token caught: " + t.getType() + ", " + t.getVal());
            case 3:
                throw new SemanticActionError("Bad Parameter Error \n Token caught: " + t.getType() + ", " + t.getVal());
        }
        return null;
    }

}
