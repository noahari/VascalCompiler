package com;


public class VariableEntry extends SymbolTableEntry
{

    private String name;
    private int address;
    private String type;
    private boolean Parameter;

    VariableEntry(String name, int add, String type)
    {
        this.name = name;
        this.address = add;
        this.type = type;
    }

    //constructor for generic case
    VariableEntry()
    {
    }

    public boolean IsVariable()
    {
        return true;
    }

    public boolean IsParameter()
    {
        return Parameter;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super
    public String GetName()
    {
        return name;
    }

    public void SetParameter()
    {
        Parameter = !Parameter;
    }

    void SetName(String name)
    {
        this.name = name;
    }

    public int GetAddress()
    {
        return address;
    }

    void SetAddress(int address)
    {
        this.address = address;
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
