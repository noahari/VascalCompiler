package com;

//A class to construct language valid tokens
class Token
{
    private String type;
    private String val;


    Token(String type, String val)
    {
        this.type = type;
        this.val = val;
    }

    Token(String type)
    {
        this.type = type;
        this.val = null;
    }

    Token()
    {
        this.type = null;
        this.val = null;
    }

    void PrintToken()
    {
        if (this.val == null)
        {
            System.out.println("['" + this.type + "', 'None']");
        } else
        {
            System.out.println("['" + this.type + "', '" + this.val + "']");
        }
    }

    String GetType()
    {
        return this.type;
    }

    String GetVal()
    {
        return this.val;
    }

    void SetType(String s)
    {
        this.type = s;
    }

    void SetVal(String s)
    {
        this.val = s;
    }

    //Helper function to determine the TVI opcode of a Token
    String GetOpCode()
    {
        String opCode = null;
        switch (this.type)
        {
            case "ADDOP":
                switch (this.val)
                {
                    case "1":
                        opCode = "add";
                        break;
                    case "2":
                        opCode = "sub";
                        break;
                }
                break;
            case "MULOP":
                switch (this.val)
                {
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
                switch (this.val)
                {
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

    //wipes a token clean
    void clear()
    {
        this.type = null;
        this.val = null;
    }

    //converts a token to an int
    int TokenToInt()
    {
        return Integer.parseInt(val);
    }

}
