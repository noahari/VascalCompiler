package com;

import java.util.HashMap;

public class SymbolTable {

    //length of hashtable to be passed to constructor
    private int size;
    private HashMap<String, SymbolTableEntry> symTable;

    public SymbolTable(int s){
        this.size = s;
        symTable = new HashMap<String, SymbolTableEntry>();
    }

    public SymbolTable(){
        symTable = new HashMap<String, SymbolTableEntry>();
    }


    SymbolTableEntry search(String index){
        return symTable.getOrDefault(index, null);
    }

    void insert(String index, SymbolTableEntry mapping) throws SymbolTableError{
        if(!symTable.containsKey(index)){
            symTable.put(index, mapping);
        }
        else{
            System.out.println(index);
            throw SymbolTableError.ErrorMsg(1, index, mapping);
        }
    }

    int getSize(){
        return symTable.size();
    }

    SymbolTableEntry lookup(Token t){
        String index = t.getVal().toUpperCase();
        if(symTable.containsKey(index)){
            return symTable.get(index);
        }
        else{
            return null;
        }
    }
    SymbolTableEntry lookup(String s){
        String index = s;
        if(symTable.containsKey(index)){
            return symTable.get(index);
        }
        else{
            return null;
        }
    }

    void dumpTable(){
        System.out.println("-----------------------------------------");
        System.out.println("TABLE DUMP:");
        System.out.println("-----------------------------------------");
        for(String name : symTable.keySet()){
            System.out.println("Name: " + name + ", Contents: " + symTable.get(name));
        }

        System.out.println("-----------------------------------------");
    }


}
