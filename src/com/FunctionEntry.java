package com;

import java.util.ArrayList;

public class FunctionEntry extends SymbolTableEntry {

    private String name;
    private int parameterCount;
    private ArrayList<SymbolTableEntry> parameterInfo;
    private VariableEntry result;

    public FunctionEntry(String name, int p, ArrayList<SymbolTableEntry> l, VariableEntry r){
        this.name = name.toUpperCase();
        this.parameterCount = p;
        this.parameterInfo = l;
        this.result = r;
    }

    //Added constructor specifically for use in Action15
    public FunctionEntry(String name, VariableEntry result){
        this.name = name.toUpperCase();
        this.parameterInfo = new ArrayList<SymbolTableEntry>();
        this.result = result;
    }

    boolean isFunction(){
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

    public VariableEntry getResult() {
        return result;
    }

    public void setResult(VariableEntry result) {
        this.result = result;
    }
}
