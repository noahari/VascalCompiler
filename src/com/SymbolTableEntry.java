package com;

import java.util.List;

public class SymbolTableEntry
{

    //global flag to determine whether an identifier needs to be inserted in the symbol table
    private boolean iFlag = false;
    private boolean Parameter = false;

    boolean IsConstant()
    {
        return false;
    }

    boolean IsVariable()
    {
        return false;
    }

    boolean IsProcedure()
    {
        return false;
    }

    boolean IsFunction()
    {
        return false;
    }

    boolean IsFunctionResult()
    {
        return false;
    }

    boolean IsParameter()
    {
        return false;
    }

    boolean IsArray()
    {
        return false;
    }

    boolean IsReserved()
    {
        return false;
    }

    String GetName()
    {
        return null;
    }

    String GetType()
    {
        return null;
    }

    int GetAddress()
    {
        return -1;
    }

    public boolean GetiFlag()
    {
        return iFlag;
    }

    public void iFlagToggle()
    {
        iFlag = !iFlag;
    }

    public SymbolTableEntry GetResult()
    {
        return null;
    }

    public void SetResult()
    {
    }

    public void SetType(String s)
    {
    }

    public void SetResultType(String s)
    {
    }

    public void SetParameterCount(int i)
    {
    }

    public void AddParam(SymbolTableEntry s)
    {
    }

    public void SetParameter()
    {
        this.Parameter = !Parameter;
    }

    public int GetParameterCount()
    {
        return -1;
    }

    public List<SymbolTableEntry> GetParameterInfo()
    {
        return null;
    }
}
