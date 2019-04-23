package com;

import java.util.List;

public class SymbolTableEntry {

    //global flag to determine whether an identifier needs to be inserted in the symbol table
    private boolean iFlag = false;
    private boolean Parameter = false;

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

    public SymbolTableEntry getResult(){return null;}

    public void setResult(){}

    public void setType(String s){}

    public void setResultType(String s){}

    public void setParameterCount(int i){}

    public void addParam(SymbolTableEntry s){}

    public void setParameter(){
        this.Parameter = !Parameter;
    }

    public int getParameterCount(){return -1;}

    public List<SymbolTableEntry> getParameterInfo(){
        return null;
    }
}
