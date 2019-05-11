package com;

import java.util.ArrayList;

public class ProcedureEntry extends SymbolTableEntry
{

    private String name;
    private int parameterCount;
    private ArrayList<SymbolTableEntry> parameterInfo;

    ProcedureEntry(String name, int p, ArrayList<SymbolTableEntry> l)
    {
        this.name = name.toUpperCase();
        this.parameterCount = p;
        this.parameterInfo = l;
    }

    //new constructor made for use in Action17
    ProcedureEntry(String name)
    {
        this.name = name;
        this.parameterInfo = new ArrayList<SymbolTableEntry>();
    }

    boolean IsProcedure()
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

}
