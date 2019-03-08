package com;

public class SymbolTableError extends CompilerError {
    public SymbolTableError(String s){
        super(s);
    }

    public static SymbolTableError ErrorMsg(int ecode, String index, SymbolTableEntry mapping) throws SymbolTableError{
        switch(ecode){
            case 1:
                throw new SymbolTableError("Attempting to insert the name " + mapping + " that already exists at index " + index);
        }
        return null;
    }

}
