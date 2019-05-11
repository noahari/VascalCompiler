package com;

import java.util.ArrayList;

public class FunctionEntry extends SymbolTableEntry
{

    private String name;
    private int parameterCount;
    private ArrayList<SymbolTableEntry> parameterInfo;
    private VariableEntry result;

    public FunctionEntry(String name, int p, ArrayList<SymbolTableEntry> l, VariableEntry r)
    {
        this.name = name.toUpperCase();
        this.parameterCount = p;
        this.parameterInfo = l;
        this.result = r;
    }

    //Added constructor specifically for use in Action15
    FunctionEntry(String name, VariableEntry result)
    {
        this.name = name.toUpperCase();
        this.parameterInfo = new ArrayList<SymbolTableEntry>();
        this.result = result;
    }

    boolean IsFunction()
    {
        return true;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super

    public String GetName()
    {
        return name;
    }

    public void SetName(String name)
    {
        this.name = name;
    }

    public int GetParameterCount()
    {
        return parameterCount;
    }

    public void SetParameterCount(int parameterCount)
    {
        this.parameterCount = parameterCount;
    }

    public ArrayList<SymbolTableEntry> GetParameterInfo()
    {
        return parameterInfo;
    }

    public void SetParameterInfo(ArrayList<SymbolTableEntry> parameterInfo)
    {
        this.parameterInfo = parameterInfo;
    }

    public void AddParam(SymbolTableEntry s)
    {
        this.parameterInfo.add(s);
    }

    public VariableEntry GetResult()
    {
        return result;
    }

    public void SetResult(VariableEntry result)
    {
        this.result = result;
    }
}
