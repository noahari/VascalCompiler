package com;

public class Token {
    private String type;
    private String val;


    Token(String type, String val){
        this.type = type;
        this.val = val;
    }

    Token(String type){
        this.type = type;
        this.val = null;
    }

    Token(){
        this.type = null;
        this.val = null;
    }

    void printToken(){
        if(this.val == null){
            System.out.println("['" + this.type + "', 'None']");
        }
        else{
            System.out.println("['" + this.type + "', '" + this.val + "']");
        }
    }

    String getType(){
        return this.type;
    }

    public String getVal(){
        return this.val;
    }

    void setType(String s){
        this.type = s;
    }

    void setVal(String s){
        this.val = s;
    }

    void clear(){
        this.type = null;
        this.val = null;
    }

    int tokenToInt(){
        return Integer.parseInt(val);
    }

}
