package com;

public class ConstantEntry extends SymbolTableEntry
{

    private String name, type;

    ConstantEntry(String n, String t)
    {
        this.name = n.toUpperCase();
        this.type = t.toUpperCase();
    }

    //with all the mention of searching for constants I felt that addding this may prove useful
    //delete later if unused
    boolean IsConstant()
    {
        return true;
    }

    public String GetName()
    {
        return name;
    }

    public void SetName(String name)
    {
        this.name = name;
    }

    public String GetType()
    {
        return type;
    }

    public void SetType(String type)
    {
        this.type = type;
    }
}
