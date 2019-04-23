package com;

import java.util.ArrayList;

public class ProcedureEntry extends SymbolTableEntry {

    private String name;
    private int parameterCount;
    private ArrayList<SymbolTableEntry> parameterInfo;

    public ProcedureEntry(String name, int p, ArrayList<SymbolTableEntry> l){
        this.name = name.toUpperCase();
        this.parameterCount = p;
        this.parameterInfo = l;
    }

    //new constructor made for use in Action17
    public ProcedureEntry(String name){
        this.name = name;
        this.parameterInfo= new ArrayList<SymbolTableEntry>();
    }

    boolean isProcedure(){
        return true;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public ArrayList<SymbolTableEntry> getParameterInfo() {
        return parameterInfo;
    }

    public void setParameterInfo(ArrayList<SymbolTableEntry> parameterInfo) {
        this.parameterInfo = parameterInfo;
    }

    public void addParam(SymbolTableEntry s){ this.parameterInfo.add(s); }

}
