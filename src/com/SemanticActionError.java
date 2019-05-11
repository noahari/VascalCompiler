package com;

public class SemanticActionError extends CompilerError
{
    public SemanticActionError(String s)
    {
        super(s);
    }

    public static SemanticActionError ErrorMsg(int ecode, Token t) throws SemanticActionError
    {
        switch (ecode)
        {
            case 1:
                throw new SemanticActionError("Undeclared Variable Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 2:
                throw new SemanticActionError("Type Mismatch Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 3:
                throw new SemanticActionError("Bad Parameter Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 4:
                throw new SemanticActionError("EType Mismatch \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 5:
                throw new SemanticActionError("ID is not an Array \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 6:
                throw new SemanticActionError("Invalid Procedure Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 7:
                throw new SemanticActionError("Invaild number of Parameters Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
            case 8:
                throw new SemanticActionError("Invaild Function Error \n Token caught: " + t.GetType() + ", " + t.GetVal());
        }
        return null;
    }

}
