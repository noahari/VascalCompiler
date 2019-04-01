package com;

public class ConstantEntry extends SymbolTableEntry {

    private String name, type;

    public ConstantEntry(String n, String t){
        this.name = n.toUpperCase();
        this.type = t.toUpperCase();
    }

    //with all the mention of searching for constants I felt that addding this may prove useful
    //delete later if unused
    boolean isConstant(){
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
