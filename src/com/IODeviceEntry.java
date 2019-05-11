package com;

public class IODeviceEntry extends SymbolTableEntry
{

    private String name;

    IODeviceEntry(String s)
    {
        this.name = s;
    }

    public String GetName()
    {
        return name;
    }

    public void SetName(String name)
    {
        this.name = name;
    }
}
