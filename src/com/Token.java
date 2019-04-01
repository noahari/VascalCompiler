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

    String getOpCode(){
        String opCode = null;
        switch(this.type){
            case "ADDOP":
                switch(this.val){
                    case "1":
                        opCode = "add";
                        break;
                    case "2":
                        opCode = "sub";
                        break;
                }
                break;
            case "MULOP":
                switch(this.val){
                    case "1":
                        opCode = "mul";
                        break;
                    case "2":
                        opCode = "div";
                        break;
                    case "3":
                        opCode = "div";
                        break;
                    case "4":
                        opCode = "mod";
                }
                break;
            case "RELOP":
                switch(this.val){
                    case "1":
                        opCode = "beq";
                        break;
                    case "2":
                        opCode = "bne";
                        break;
                    case "3":
                        opCode = "blt";
                        break;
                    case "4":
                        opCode = "bgt";
                        break;
                    case "5":
                        opCode = "ble";
                        break;
                    case "6":
                        opCode = "bge";
                        break;
                }
                break;
        }
            return opCode;
    }

    void clear(){
        this.type = null;
        this.val = null;
    }

    int tokenToInt(){
        return Integer.parseInt(val);
    }

}
