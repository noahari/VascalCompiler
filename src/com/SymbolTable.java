package com;

import java.util.HashMap;

public class SymbolTable
{

    private HashMap<String, SymbolTableEntry> symTable;

    public SymbolTable(int s)
    {
        symTable = new HashMap<String, SymbolTableEntry>();
    }

    SymbolTable()
    {
        symTable = new HashMap<String, SymbolTableEntry>();
    }


    SymbolTableEntry Search(String index)
    {
        return symTable.getOrDefault(index, null);
    }

    void Insert(String index, SymbolTableEntry mapping) throws SymbolTableError
    {
        if (!symTable.containsKey(index))
        {
            symTable.put(index, mapping);
        } else
        {
            System.out.println(index);
            throw SymbolTableError.ErrorMsg(1, index, mapping);
        }
    }

    int GetSize()
    {
        return symTable.size();
    }

    SymbolTableEntry LookUp(Token t)
    {
        String index = t.GetVal().toUpperCase();
        if (symTable.containsKey(index))
        {
            return symTable.get(index);
        } else
        {
            return null;
        }
    }

    SymbolTableEntry LookUp(String s)
    {
        if (symTable.containsKey(s))
        {
            return symTable.get(s);
        } else
        {
            return null;
        }
    }

    void dumpTable()
    {
        System.out.println("-----------------------------------------");
        System.out.println("TABLE DUMP:");
        System.out.println("-----------------------------------------");
        for (String name : symTable.keySet())
        {
            System.out.println("Name: " + name + ", Contents: " + symTable.get(name));
        }

        System.out.println("-----------------------------------------");
    }


}
