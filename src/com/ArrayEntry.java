package com;

public class ArrayEntry extends SymbolTableEntry {

    private String name;
    private int address;
    private String type;
    private int upperBound;
    private int lowerBound;

    public ArrayEntry(String name, int address, String type, int upperBound, int lowerBound){
        this.name = name;
        this.address = address;
        this.type = type;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }


    boolean isArray(){
        return true;
    }

    //getters and setters, since this is a framework. delete unused members later if any
    //no getter/setter for iFlag since already inherited from super

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

}
