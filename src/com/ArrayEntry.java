package com;

public class ArrayEntry extends SymbolTableEntry
{

    private String name;
    private int address;
    private String type;
    private int upperBound;
    private int lowerBound;

    ArrayEntry(String name, int address, String type, int upperBound, int lowerBound)
    {
        this.name = name.toUpperCase();
        this.address = address;
        this.type = type.toUpperCase();
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }


    boolean IsArray()
    {
        return true;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super

    public String GetName()
    {
        return name;
    }

    public int GetAddress()
    {
        return address;
    }

    public String GetType()
    {
        return type;
    }

    int GetUpperBound()
    {
        return upperBound;
    }

    int GetLowerBound()
    {
        return lowerBound;
    }

    public void SetName(String name)
    {
        this.name = name;
    }

    void SetAddress(int address)
    {
        this.address = address;
    }

    public void SetType(String type)
    {
        this.type = type;
    }

    void SetUpperBound(int upperBound)
    {
        this.upperBound = upperBound;
    }

    void SetLowerBound(int lowerBound)
    {
        this.lowerBound = lowerBound;
    }

}
