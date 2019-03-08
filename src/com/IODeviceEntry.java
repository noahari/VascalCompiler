package com;

public class IODeviceEntry extends SymbolTableEntry {

    private String name;

    public IODeviceEntry(String s){
        this.name = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
