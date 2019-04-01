package com;


public class VariableEntry extends SymbolTableEntry{

    private String name;
    private int address;
    private String type;


    public VariableEntry(String name, int add, String type){
        this.name = name;
        this.address = add;
        this.type = type;
    }

    //constructor for generic case
    public VariableEntry(){}

    public boolean isVariable(){
        return true;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
