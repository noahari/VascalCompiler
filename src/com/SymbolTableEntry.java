package com;

public class SymbolTableEntry {

    //global flag to determine whether an identifier needs to be inserted in the symbol table
    private boolean iFlag = false;

    boolean isConstant(){
        return false;
    }
    boolean isVariable(){
        return false;
    }
    boolean isProcedure(){
        return false;
    }
    boolean isFunction(){
        return false;
    }
    boolean isFunctionResult(){
        return false;
    }
    boolean isParameter(){
        return false;
    }
    boolean isArray(){
        return false;
    }
    boolean isReserved(){
        return false;
    }

    String getName(){return null;}

    String getType(){return null;}

    int getAddress(){return -1;}

    public boolean getiFlag(){
        return iFlag;
    }

    public void iFlagToggle(){
        iFlag = !iFlag;
    }
}
