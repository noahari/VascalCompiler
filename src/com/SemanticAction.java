package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SemanticAction
{

    //true = Insert mode, false = search mode
    private boolean modeFlag;
    //true = global env, false = local env
    private boolean envFlag;
    //true = array mode, false = simple mode
    private boolean arrayFlag;
    private int globalMemory;
    private int localMemory;
    private int globalStore;
    private int localStore;
    private Stack<Object> semanticStack;
    private Stack<Integer> paramCount;
    private Stack<List<SymbolTableEntry>> paramStack;
    private int nextParam;
    private SymbolTable gTable;
    private SymbolTable lTable;
    private SymbolTable cTable;
    private SymbolTableEntry currentFunction;
    private Quadruples Qs;
    private int tempVarUID = 0;

    Stack<Object> GetSemanticStack()
    {
        return semanticStack;
    }

    SemanticAction() throws CompilerError
    {
        modeFlag = true;
        envFlag = true;
        arrayFlag = false;
        globalMemory = 0;
        localMemory = 0;
        globalStore = 0;
        localStore = 0;
        semanticStack = new Stack<>();
        paramCount = new Stack<Integer>();
        paramStack = new Stack<List<SymbolTableEntry>>();
        gTable = new SymbolTable();
        lTable = new SymbolTable();
        cTable = new SymbolTable();
        Qs = new Quadruples();
        InitGlobalTable();
        //gTable.dumpTable();
    }

    Quadruples GetQs()
    {
        return Qs;
    }

    //initializes the global symbol table
    private void InitGlobalTable() throws SymbolTableError
    {
        gTable.Insert("MAIN", new ProcedureEntry("MAIN", 0, null));
        gTable.Insert("READ", new ProcedureEntry("READ", 0, null));
        gTable.Insert("WRITE", new ProcedureEntry("WRITE", 0, null));
        //IO added in 9 when it matters
    }

    //looks up a token in the symbol tables and returns the value
    private SymbolTableEntry LookUpID(Token t)
    {
        SymbolTableEntry ste = lTable.LookUp(t.GetVal());
        if (ste == null)
        {
            ste = gTable.LookUp(t.GetVal());
        }
        return ste;
    }

    //looks up a token in the symbol tables and returns the value
    private SymbolTableEntry LookUpID(String t)
    {
        SymbolTableEntry ste = lTable.LookUp(t);
        if (ste == null)
        {
            ste = gTable.LookUp(t);
        }
        if (ste == null)
        {
            ste = LookUpConstant(t);
        }
        return ste;
    }

    //looks up a token in the constant table
    private SymbolTableEntry LookUpConstant(String t)
    {
        return cTable.LookUp(t);
    }

    //Checks two given entries for their types to be compatible for semantic actions later
    private int TypeCheck(SymbolTableEntry id1, SymbolTableEntry id2)
    {
        String type1 = id1.GetType();
        String type2 = id2.GetType();

        //check cases where equal first
        if (type1.equals("REAL") || type1.equals("REALCONSTANT"))
        {
            //real, real
            if (type2.equals("REAL") || type2.equals("REALCONSTANT"))
            {
                return 1;
            }
            //real, int
            else if (type2.equals("INTEGER") || type2.equals("INTCONSTANT"))
            {
                return 2;
            }
        } else if (type1.equals("INTEGER") || type1.equals("INTCONSTANT"))
        {
            //int, real
            if (type2.equals("REAL") || type2.equals("REALCONSTANT"))
            {
                return 3;
            }
            //int, int
            else if (type2.equals("INTEGER") || type2.equals("INTCONSTANT"))
            {
                return 0;
            }
        }
        //fail case
        return -1;
    }

    //Creates entries in the symbol tables
    private VariableEntry Creat(String name, String type) throws SymbolTableError
    {
        VariableEntry ve = new VariableEntry();
        ve.SetType(type);
        ve.SetName(name);
        // store the address as negative to distinguish between
        // temporary variables
        if (envFlag)
        {
            ve.SetAddress(globalMemory);
            globalMemory++;
            gTable.Insert(ve.GetName(), ve);
        } else
        {
            ve.SetAddress(localMemory);
            localMemory++;
            lTable.Insert(ve.GetName(), ve);
        }
        tempVarUID++;
        return ve;
    }

    //generates the next temp var name
    private String GetTempVar()
    {
        return "temp" + Integer.toString(tempVarUID);
    }

    //checks if a string is reserved
    private Boolean IsReserved(String s)
    {
        return s.equals("main") || s.equals("_");
    }

    //BEGIN
    //These functions from now until the denoted end all generate TVI code
    private void Generate(String op1)
    {
        Quadruple tvi = new Quadruple(op1);
        Qs.AddQuad(tvi);
    }

    private void Generate(String op1, String op2) throws SymbolTableError
    {
        SymbolTableEntry op2t = LookUpID(op2);
        String op2f = "";
        if (IsReserved(op2))
        {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null)
        {
            //must add if case in event called on Parameter
            if (op1.equals("param"))
            {
                op2f = GetParamPrefix(op2t) + Integer.toString(GetSTEAddress(op2t));
            } else
            {
                op2f = GetSTEPrefix(op2t) + Integer.toString(GetSTEAddress(op2t));
            }
        }
        Quadruple tvi = new Quadruple(op1, op2f);
        Qs.AddQuad(tvi);
    }

    //These function generate explicitly written code
    private void GenerateHard(String op1, String op2)
    {
        Quadruple tvi = new Quadruple(op1, op2);
        Qs.AddQuad(tvi);
    }

    private void GenerateHard(String op1, String op2, String op3)
    {
        Quadruple tvi = new Quadruple(op1, op2, op3);
        Qs.AddQuad(tvi);
    }

    private void Generate(String op1, String op2, String op3) throws SymbolTableError
    {
        SymbolTableEntry op2t = LookUpID(op2);
        String op2f = "";
        SymbolTableEntry op3t = LookUpID(op3);
        String op3f = "";
        String prefix, address;
        if (IsReserved(op2))
        {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null)
        {
            prefix = GetSTEPrefix(op2t);
            address = Integer.toString(GetSTEAddress(op2t));
            op2f = prefix + address;
        }
        if (IsReserved(op3))
        {
            //if it is reserved, don't bother looking, it is what it is
            op3f = op3;
        } else if (op3t != null)
        {
            op3f = GetSTEPrefix(op3t) + Integer.toString(GetSTEAddress(op3t));
        }
        Quadruple tvi = new Quadruple(op1, op2f, op3f);
        Qs.AddQuad(tvi);
    }

    private void Generate(String op1, String op2, String op3, String op4) throws SymbolTableError
    {
        SymbolTableEntry op2t = LookUpID(op2);
        String op2f = "";
        SymbolTableEntry op3t = LookUpID(op3);
        String op3f = "";
        SymbolTableEntry op4t = LookUpID(op4);
        String op4f = "";
        if (IsReserved(op2))
        {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null)
        {
            op2f = GetSTEPrefix(op2t) + Integer.toString(GetSTEAddress(op2t));
        }
        if (IsReserved(op3))
        {
            //if it is reserved, don't bother looking, it is what it is
            op3f = op3;
        } else if (op3t != null)
        {
            op3f = GetSTEPrefix(op3t) + Integer.toString(GetSTEAddress(op3t));
        }
        if (IsReserved(op4))
        {
            //if it is reserved, don't bother looking, it is what it is
            op4f = op4;
        } else if (op4t != null)
        {
            op4f = GetSTEPrefix(op4t) + Integer.toString(GetSTEAddress(op4t));
        }
        Quadruple tvi = new Quadruple(op1, op2f, op3f, op4f);
        Qs.AddQuad(tvi);
    }

    //new separate Generate overload for GetSTEAddress
    private void GenerateAddress(String op1, String op2, String op3) throws SymbolTableError
    {
        Quadruple quad;
        String address = null;
        String prefix;

        if (LookUpID(op3) != null)
        {
            prefix = GetSTEPrefix(LookUpID(op3));
            address = prefix + Integer.toString(GetSTEAddress(LookUpID(op3)));
        }
        quad = new Quadruple(op1, op2, address);
        Qs.AddQuad(quad);
    }

    //used for Generate at the end-of-main
    private void GenerateEnd(String op1, String op2)
    {
        Quadruple quad;
        quad = new Quadruple(op1, op2);
        Qs.AddQuad(quad);
    }

    //Currently used for Action9, not sure if necessary
    //there may be a more elegant solution than overriding
    //for one specific scenario
    private void Generate9(String op1, String op2, String op3)
    {
        Quadruple quad;
        quad = new Quadruple(op1, op2, op3);
        Qs.AddQuad(quad);
    }

    //END GENERATES

    //Backpatches for allocation and calls
    private void Backpatch(int i, int x)
    {
        //set the field of ith Quad's second field to x
        Qs.SetField(i, 1, Integer.toString(x));
    }

    //Backpatches for allocation and calls
    private void Backpatch(List<Integer> list, int x)
    {
        for (Integer i : list)
        {
            if (Qs.GetField(i, 0).equals("goto"))
            {
                Qs.SetField(i, 1, Integer.toString(x));
            } else
            {
                Qs.SetField(i, 3, Integer.toString(x));
            }
        }
    }

    //Generates the prefix for a given entry depending on its scope
    private String GetParamPrefix(SymbolTableEntry param)
    {
        if (envFlag)
        {
            return "@_";
        }
        //local
        else
        {
            if (param.IsParameter())
            {
                return "%";
            } else
            {
                return "@%";
            }
        }
    }

    //gets the address in memory of a given entry
    private int GetSTEAddress(SymbolTableEntry ste) throws SymbolTableError
    {
        if (ste.IsArray() || ste.IsVariable())
        {
            // array entries and variable entries are
            // assigned address when they are initialized
            return ste.GetAddress();
        }
        if (ste.IsConstant())
        {
            // constants do not have an address, and a
            // temporary variable must be created to store it
            VariableEntry temp = Creat(GetTempVar(), ste.GetType());
            // move the constant into the temporary variable
            GenerateAddress("move", ste.GetName(), temp.GetName());
            // return the address of the temporary variable
            return temp.GetAddress();
        }
        //otherwise, error
        return -1;
    }

    //this can likely be simplified, edge cases have bloated the function
    private String GetSTEPrefix(SymbolTableEntry ste)
    {
        if (envFlag)
        {
            return "_";
        }
        //local
        else
        {
            SymbolTableEntry entry = lTable.LookUp(ste.GetName());
            // entry is a global variable?
            if (entry == null)
            {
                SymbolTableEntry cEntry = cTable.LookUp(ste.GetName());
                if (cEntry != null)
                {
                    //if found in cTable, is constant
                    return "%";
                }
                //otherwise it is global
                return "_";
            } else
            {
                if (ste.IsParameter())
                {
                    //parameter must be dereferenced
                    return "^%";
                }
                //ste is not a parameter
                else
                {
                    return "%";
                }
            }
        }
    }

    /*
    private String GetSTEPrefix(SymbolTableEntry ste) {
        if (envFlag) {
            return "_";
        } else {
            //local env
            SymbolTableEntry entry = lTable.LookUp(ste.GetName());
            if (entry == null) {
                //entry is a global var
                return "_";
            } else {
                return "%";
            }
        }
    }
    */

    //helper function to Generate a list
    private List<Integer> MakeList(int i)
    {
        List<Integer> list = new ArrayList<>();
        list.add(i);
        return list;
    }

    //Helper function to merge two lists
    private List<Integer> Merge(List<Integer> l1, List<Integer> l2)
    {
        l1.addAll(l2);
        return l1;
    }

    /*
        public void dumpStack(){
            Stack<Object> dumpStack = new Stack();
            dumpStack = (Stack<Object>) semanticStack.clone();
            System.out.Print("[");
            while(!dumpStack.Empty()){
                System.out.Print(dumpStack.peek());
                dumpStack.pop();
                if(!dumpStack.Empty()){
                    System.out.Print(",");
                }
            }
            System.out.println("]");
        }
    */

    //Executes a semantic action based on the string passed
    void Execute(String action, Token t) throws CompilerError
    {
        //dumpStack();
        switch (action)
        {
            case "#1":
                Action1(t);
                break;
            case "#2":
                Action2(t);
                break;
            case "#3":
                Action3(t);
                break;
            case "#4":
                Action4(t);
                break;
            case "#5":
                Action5(t);
                break;
            case "#6":
                Action6(t);
                break;
            case "#7":
                Action7(t);
                break;
            case "#9":
                Action9(t);
                break;
            case "#11":
                Action11(t);
                break;
            case "#13":
                Action13(t);
                break;
            case "#15":
                Action15(t);
                break;
            case "#16":
                Action16(t);
                break;
            case "#17":
                Action17(t);
                break;
            case "#19":
                Action19(t);
                break;
            case "#20":
                Action20(t);
                break;
            case "#21":
                Action21(t);
                break;
            case "#22":
                Action22(t);
                break;
            case "#24":
                Action24(t);
                break;
            case "#25":
                Action25(t);
                break;
            case "#26":
                Action26(t);
                break;
            case "#27":
                Action27(t);
                break;
            case "#28":
                Action28(t);
                break;
            case "#29":
                Action29(t);
                break;
            case "#30":
                Action30(t);
                break;
            case "#31":
                Action31(t);
                break;
            case "#32":
                Action32(t);
                break;
            case "#33":
                Action33(t);
                break;
            case "#34":
                Action34(t);
                break;
            case "#35":
                Action35(t);
                break;
            case "#36":
                Action36(t);
                break;
            case "#37":
                Action37(t);
                break;
            case "#38":
                Action38(t);
                break;
            case "#39":
                Action39(t);
                break;
            case "#40":
                Action40(t);
                break;
            case "#41":
                Action41(t);
                break;
            case "#42":
                Action42(t);
                break;
            case "#43":
                Action43(t);
                break;
            case "#44":
                Action44(t);
                break;
            case "#45":
                Action45(t);
                break;
            case "#46":
                Action46(t);
                break;
            case "#47":
                Action47(t);
                break;
            case "#48":
                Action48(t);
                break;
            case "#49":
                Action49(t);
                break;
            case "#50":
                Action50(t);
                break;
            case "#51":
                Action51(t);
                break;
            case "#51READ":
                Action51READ(t);
                break;
            case "#51WRITE":
                Action51WRITE(t);
                break;
            case "#52":
                Action52(t);
                break;
            case "#53":
                Action53(t);
                break;
            case "#54":
                Action54(t);
                break;
            case "#55":
                Action55(t);
                break;
            case "#56":
                Action56(t);
                break;
        }
    }

    //Sets mode to Insert
    private void Action1(Token t)
    {
        modeFlag = true;
    }

    //Sets mode to Search
    private void Action2(Token t)
    {
        modeFlag = false;
    }

    //Handles Identifiers
    private void Action3(Token t) throws SymbolTableError
    {
        String tokenType = ((Token) semanticStack.pop()).GetType();
        if (arrayFlag)
        {
            int upperBound = ((Token) semanticStack.pop()).TokenToInt();
            int lowerBound = ((Token) semanticStack.pop()).TokenToInt();
            int memorySize = (upperBound - lowerBound) + 1;

            while (!(semanticStack.isEmpty()) && (((Token) (semanticStack.peek())).GetType().equals("IDENTIFIER")) && (semanticStack.peek() instanceof Token))
            {
                Token tok = (Token) semanticStack.pop();
                ArrayEntry id = new ArrayEntry(tok.GetVal(), 0, tokenType, upperBound, lowerBound);
                id.SetType(tokenType);
                id.SetUpperBound(upperBound);
                id.SetLowerBound(lowerBound);

                if (envFlag)
                {
                    id.SetAddress(globalMemory);
                    gTable.Insert(tok.GetVal(), id);
                    globalMemory += memorySize;
                } else
                {
                    id.SetAddress(localMemory);
                    lTable.Insert(tok.GetVal(), id);
                    localMemory += memorySize;

                }
            }
        } else
        {
            while (!semanticStack.empty() && (semanticStack.peek() instanceof Token) && (((Token) (semanticStack.peek())).GetType().equals("IDENTIFIER")))
            {
                Token tok = (Token) semanticStack.pop();
                VariableEntry id = new VariableEntry(tok.GetVal(), 0, null);
                id.SetType(tokenType);

                if (envFlag)
                {
                    id.SetAddress(globalMemory);
                    //System.out.println(tok.GetType() + tok.GetVal() + id.GetType() + id.GetName());
                    gTable.Insert(tok.GetVal(), id);
                    globalMemory++;
                } else
                {
                    id.SetAddress(localMemory);
                    lTable.Insert(tok.GetVal(), id);
                    localMemory++;
                }
            }
        }
        //gTable.dumpTable();
        arrayFlag = false;
    }

    //for "REAL" v "INT"
    private void Action4(Token t)
    {
        semanticStack.push(t);
    }

    //Generate code for start of function.
    private void Action5(Token t) throws SymbolTableError
    {
        modeFlag = false;
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        GenerateHard("PROCBEGIN", id.GetName());
        localStore = Qs.GetNextQuad();
        Generate("alloc", "_");
    }

    //Notes we are working in an array
    private void Action6(Token t)
    {
        arrayFlag = true;
    }

    //for array member intconstants
    private void Action7(Token t)
    {
        semanticStack.push(t);
    }

    //Handles calling main
    private void Action9(Token t) throws SymbolTableError
    {
        //pop the IDs
        //output
        Token id1 = (Token) semanticStack.pop();
        //input
        Token id2 = (Token) semanticStack.pop();
        //program name
        Token id3 = (Token) semanticStack.pop();

        //System.out.println("id1: " + id1.GetVal() + ", id2: " + id2.GetVal() + ", id3: " + id3.GetVal());
        //System.out.println("id1: " + id1.GetType() + ", id2: " + id2.GetType() + ", id3: " + id3.GetType());

        //add entry output:IODEVICE OUTPUT
        gTable.Insert(id1.GetVal(), new IODeviceEntry(id1.GetVal()));
        //add entry input:IODEVICE INPUT
        gTable.Insert(id2.GetVal(), new IODeviceEntry(id2.GetVal()));
        //add entry
        gTable.Insert(id3.GetVal(), new ProcedureEntry(id3.GetVal(), 0, null));

        //dump table
        //gTable.dumpTable();
        //Generate stub code
        Generate9("call", "main", "0");
        Generate("exit");
        //Insert = false
        modeFlag = false;
    }

    //Generate code for end of function.
    private void Action11(Token t) throws SymbolTableError
    {
        envFlag = true;
        // delete the local symbol table
        lTable = new SymbolTable();
        currentFunction = null;
        Backpatch(localStore, localMemory);
        GenerateHard("free", Integer.toString(localMemory));
        Generate("PROCEND");
    }

    //for identifiers
    private void Action13(Token t)
    {
        semanticStack.push(t);
    }

    //Store result of function.
    private void Action15(Token t) throws SemanticActionError, SymbolTableError
    {
        // create a variable to store the result of the function
        VariableEntry result = Creat(t.GetVal() + "_RESULT", "INTEGER");
        // set the result tag of the variable entry class
        result.SetResult();
        // create a new function entry with name from the token
        // from the parser and the result variable just created
        SymbolTableEntry id = new FunctionEntry(t.GetVal(), result);
        gTable.Insert(id.GetName(), id);
        envFlag = false;
        localMemory = 0;
        currentFunction = id;
        semanticStack.push(id);
    }

    //Set type of function and its result.
    private void Action16(Token t)
    {
        // this action sets the type of the function and its result
        Token type = (Token) semanticStack.pop();
        FunctionEntry id = (FunctionEntry) semanticStack.peek();
        id.SetType(type.GetType());
        // set the type of the result variable of id
        id.SetResultType(type.GetType());
        currentFunction = id;
    }

    //Create procedure in symbol table.
    private void Action17(Token t) throws SymbolTableError
    {
        // create a new procedure entry with the name of the token
        // from the parser
        SymbolTableEntry id = new ProcedureEntry(t.GetVal());
        gTable.Insert(id.GetName(), id);
        envFlag = false;
        localMemory = 0;
        currentFunction = id;
        semanticStack.push(id);
    }

    //Initialise count of formal parameters.
    private void Action19(Token t)
    {
        paramCount = new Stack<>();
        paramCount.push(0);
    }

    //Get number of parameters.
    private void Action20(Token t)
    {
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        int numParams = paramCount.pop();
        // id is a function entry or a procedure entry
        id.SetParameterCount(numParams);
    }

    //Create temporary variables to store parameter info.
    private void Action21(Token t) throws SymbolTableError
    {
        Token type = (Token) semanticStack.pop();

        // if array, then pop the upper and lower bounds
        int upperBound = -1;
        int lowerBound = -1;
        if (arrayFlag)
        {
            upperBound = ((Token) semanticStack.pop()).TokenToInt();
            lowerBound = ((Token) semanticStack.pop()).TokenToInt();
        }

        // the tokens on the stack, which represent parameters,
        // must be added from the bottom-most id to the top-most
        Stack<Token> parameters = new Stack<>();

        // as the ids are popped off the stack, push them onto to
        // the new stack to reverse the order
        while (semanticStack.peek() instanceof Token && ((Token) semanticStack.peek()).GetType().equals("IDENTIFIER"))
        {
            parameters.push((Token) semanticStack.pop());
        }

        while (!parameters.empty())
        {
            SymbolTableEntry var;
            Token param = parameters.pop();
            if (arrayFlag)
            {
                var = new ArrayEntry(param.GetVal(), localMemory, type.GetType(), upperBound, lowerBound);
            } else
            {
                var = new VariableEntry(param.GetVal(), localMemory, type.GetType());
            }
            var.SetParameter();
            lTable.Insert(var.GetName(), var);
            // current function is either a procedure or function entry
            currentFunction.AddParam(var);
            localMemory++;
            // increment the top of paramCount
            paramCount.push(paramCount.pop() + 1);
        }
        arrayFlag = false;
    }

    //Update branch destination for IF -> #t to next quad.
    private void Action22(Token t) throws SemanticActionError
    {
        EType eType = (EType) semanticStack.pop();
        if (eType != EType.RELATIONAL)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        Backpatch(ETrue, Qs.GetNextQuad());
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    //Store line number of beginning of loop.
    private void Action24(Token t)
    {
        int beginLoop = Qs.GetNextQuad();
        semanticStack.push(beginLoop);
    }

    //Initialisation for a WHILE loop.
    private void Action25(Token t) throws SemanticActionError
    {
        //dumpStack();
        //Qs.Print();
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.RELATIONAL)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        Backpatch(ETrue, Qs.GetNextQuad());
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    //Write code at end of WHILE loop.
    private void Action26(Token t) throws SymbolTableError
    {
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        // beginLoop is pushed onto the stack in action 24
        int beginLoop = (int) semanticStack.pop();
        GenerateHard("goto", Integer.toString(beginLoop));
        Backpatch(EFalse, Qs.GetNextQuad());
    }

    //Sets up ELSE case.
    private void Action27(Token t) throws SymbolTableError
    {
        List<Integer> skipElse = MakeList(Qs.GetNextQuad());
        Generate("goto", "_");
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        Backpatch(EFalse, Qs.GetNextQuad());
        semanticStack.push(skipElse);
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    //End of ELSE stmt.
    private void Action28(Token t)
    {
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        // skipElse is pushed onto the stack in action 27
        List<Integer> skipElse = (List<Integer>) semanticStack.pop();
        Backpatch(skipElse, Qs.GetNextQuad());
    }

    //End of IF without ELSE.
    private void Action29(Token t)
    {
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        Backpatch(EFalse, Qs.GetNextQuad());
    }

    //create more actions
    //Check to see if a variable has been declared
    private void Action30(Token t) throws SemanticActionError
    {
        SymbolTableEntry id = LookUpID(t);
        if (id == null)
        {
            throw SemanticActionError.ErrorMsg(1, t);
        }
        semanticStack.push(id);
        semanticStack.push(EType.ARITHMETIC);
    }

    //Put the value of variable ID2 in ID1 (i.e. Variable assignment.)
    private void Action31(Token t) throws SemanticActionError, SymbolTableError
    {
        EType etype = (EType) semanticStack.pop();

        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
        //System.out.println(id2.GetName() + " value: " + lTable.LookUp(id2.GetName()));
        int typeVal = TypeCheck(id1, id2);

        if (typeVal == 3)
        {
            Qs.Print();
            throw SemanticActionError.ErrorMsg(2, t);
        }
        if (typeVal == 2)
        {
            VariableEntry temp = Creat(GetTempVar(), "REAL");
            Generate("ltof", id2.GetName(), temp.GetName());
            if (offset == null)
            {
                Generate("move", temp.GetName()
                        , id1.GetName());
            } else
            {
                Generate("stor", temp.GetName(), offset.GetName(), id1.GetName());
            }
        } else
        {
            if (offset == null)
            {
                Generate("move", id2.GetName(), id1.GetName());
            } else
            {
                Generate("stor", id2.GetName(), offset.GetName(), id1.GetName());
            }
        }
    }

    //Ensure TOS is an array & typecheck.
    private void Action32(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(4, t);
        }
        if (!id.IsArray())
        {
            throw SemanticActionError.ErrorMsg(5, t);
        }
    }

    //Calculate memory offset for array element.
    private void Action33(Token t) throws SemanticActionError, SymbolTableError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (!(id.GetType().equals("INTEGER")))
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        ArrayEntry array = (ArrayEntry) semanticStack.peek();
        VariableEntry temp1 = Creat(GetTempVar(), "INTEGER");
        VariableEntry temp2 = Creat(GetTempVar(), "INTEGER");
        Generate("move", Integer.toString(array.GetLowerBound()), temp1.GetName());
        Generate("sub", id.GetName(), temp1.GetName(), temp2.GetName());
        semanticStack.push(temp2);
    }

    //Determines Function or procedure.
    private void Action34(Token t) throws CompilerError
    {
        //cannot cast variableEntry to EType
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (id.IsFunction())
        {
            semanticStack.push(etype);
            Execute("#52", t);
        } else
        {
            semanticStack.push(null);
        }
    }

    //Set up to call a procedure.
    private void Action35(Token t)
    {
        EType etype = (EType) semanticStack.pop();
        // id is a procedure entry
        ProcedureEntry id = (ProcedureEntry) semanticStack.peek();
        semanticStack.push(etype);
        paramCount.push(0);
        paramStack.push(id.GetParameterInfo());
    }

    //Generate code to call a procedure.
    private void Action36(Token t) throws SemanticActionError, SymbolTableError
    {
        EType etype = (EType) semanticStack.pop();
        ProcedureEntry id = (ProcedureEntry) semanticStack.pop();
        if (id.GetParameterCount() != 0)
        {
            throw SemanticActionError.ErrorMsg(7, t);
        }
        Generate("call", id.GetName(), "0");
    }

    //Consume actual parameters in a list of parameters.
    private void Action37(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }

        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (!id.IsVariable() && !id.IsConstant() && !id.IsFunctionResult() && !id.IsArray())
        {
            throw SemanticActionError.ErrorMsg(3, t);
        }

        // increment the top of paramCount
        paramCount.push(paramCount.pop() + 1);

        // find the name of the procedure/function on the bottom of the stack
        Stack parameters = new Stack();
        while (!(semanticStack.peek() instanceof FunctionEntry || semanticStack.peek() instanceof ProcedureEntry))
        {
            parameters.push(semanticStack.pop());
        }
        // funcId is a procedure or function entry
        SymbolTableEntry funcId = (SymbolTableEntry) semanticStack.peek();
        while (!parameters.empty())
        {
            semanticStack.push(parameters.pop());
        }

        if (!(funcId.GetName().equals("READ") || funcId.GetName().equals("WRITE")))
        {
            if (paramCount.peek() > funcId.GetParameterCount())
            {
                throw SemanticActionError.ErrorMsg(7, t);
            }
            SymbolTableEntry param = paramStack.peek().get(nextParam);
            if (!(id.GetType().equals(param.GetType())))
            {
                throw SemanticActionError.ErrorMsg(6, t);
            }
            if (param.IsArray())
            {
                if ((((ArrayEntry) id).GetLowerBound() != ((ArrayEntry) param).GetLowerBound()) ||
                        (((ArrayEntry) id).GetUpperBound() != ((ArrayEntry) param).GetUpperBound()))
                {
                    throw SemanticActionError.ErrorMsg(6, t);
                }
            }
            nextParam++;
        }
    }

    //Ensure arithmetic operation & push.
    private void Action38(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        // token should be an operator
        semanticStack.push(t);
    }

    //Change to relational & add ET/F as required.
    private void Action39(Token t) throws SemanticActionError, SymbolTableError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        Token operator = (Token) semanticStack.pop();
        // the operator must be replaced with the proper TVI code which
        // jump if the condition is me
        // ex. the token representing "<" should be replaced with "blt"
        String opcode = operator.GetOpCode();
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
        int type = TypeCheck(id1, id2);
        if (type == 2)
        {
            VariableEntry temp = Creat(GetTempVar(), "REAL");
            Generate("ltof", id2.GetName(), temp.GetName());
            Generate(opcode, id1.GetName(), temp.GetName(), "_");
        } else if (type == 3)
        {
            VariableEntry temp = Creat(GetTempVar(), "REAL");
            Generate("ltof", id1.GetName(), temp.GetName());
            Generate(opcode, temp.GetName(), id2.GetName(), "_");
        } else
        {
            Generate(opcode, id1.GetName(), id2.GetName(), "_");
        }
        Generate("goto", "_");
        List<Integer> ETrue = MakeList(Qs.GetNextQuad() - 2);
        List<Integer> EFalse = MakeList(Qs.GetNextQuad() - 1);
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
        semanticStack.push(EType.RELATIONAL);
    }

    // the token should be a sign (unary plus or minus)
    //Push unary plus/minus to stack
    private void Action40(Token t)
    {
        semanticStack.push(t);
    }

    //Apply unary plus/minus
    private void Action41(Token t) throws SymbolTableError, SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();

        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }

        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        Token sign = (Token) semanticStack.pop();
        if (sign.GetType().equals("UNARYMINUS"))
        {
            VariableEntry temp = Creat(GetTempVar(), id.GetType());
            if (id.GetType().equals("INTEGER"))
            {
                Generate("uminus", id.GetName(), temp.GetName());
            } else
            {
                Generate("fuminus", id.GetName(), temp.GetName());
            }
            semanticStack.push(temp);
        } else
        {
            semanticStack.push(id);
        }
        semanticStack.push(EType.ARITHMETIC);
    }

    // the token should be an operator
    //Push ADDOP operator (+, -, etc.) on to stack
    private void Action42(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();

        if (t.GetType().equals("ADDOP") && t.GetVal().equals("3"))
        {
            if (etype != EType.RELATIONAL)
            {
                throw SemanticActionError.ErrorMsg(2, t);
            }
            // the top of the stack should be a list of integers
            List<Integer> EFalse = (List<Integer>) semanticStack.peek();
            Backpatch(EFalse, Qs.GetNextQuad());
        } else
        {
            if (etype != EType.ARITHMETIC)
            {
                throw SemanticActionError.ErrorMsg(2, t);
            }
        }
        semanticStack.push(t);
    }

    //Perform ADDOP based on OP popped from stack.
    private void Action43(Token t) throws SymbolTableError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL)
        {
            List<Integer> E2False = (List<Integer>) semanticStack.pop();
            List<Integer> E2True = (List<Integer>) semanticStack.pop();
            Token operator = (Token) semanticStack.pop();
            List<Integer> E1False = (List<Integer>) semanticStack.pop();
            List<Integer> E1True = (List<Integer>) semanticStack.pop();

            List<Integer> ETrue = Merge(E1True, E2True);
            List<Integer> EFalse = E2False;
            semanticStack.push(ETrue);
            semanticStack.push(EFalse);
            semanticStack.push(EType.RELATIONAL);
        }
        //currently only two ETypes, if more added, make else if(etype == EType.Arithmetic)
        else
        {
            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
            // this is one place where the operator from action 42 is popped
            Token operator = (Token) semanticStack.pop();
            // get the TVI opcode associated with the operator token
            // ex. for a token representing addition, opcode would be "add"
            String opcode = operator.GetOpCode();
            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

            if (TypeCheck(id1, id2) == 0)
            {
                VariableEntry temp = Creat(GetTempVar(), "INTEGER");
                Generate(opcode, id1.GetName(), id2.GetName(), temp.GetName());
                semanticStack.push(temp);
            } else if (TypeCheck(id1, id2) == 1)
            {
                VariableEntry temp = Creat(GetTempVar(), "REAL");
                Generate("f" + opcode, id1.GetName(), id2.GetName(), temp.GetName());
                semanticStack.push(temp);
            } else if (TypeCheck(id1, id2) == 2)
            {
                VariableEntry temp1 = Creat(GetTempVar(), "REAL");
                VariableEntry temp2 = Creat(GetTempVar(), "REAL");
                Generate("ltof", id2.GetName(), temp1.GetName());
                Generate("f" + opcode, id1.GetName(), temp1.GetName(), temp2.GetName());
                semanticStack.push(temp2);
            } else if (TypeCheck(id1, id2) == 3)
            {
                VariableEntry temp1 = Creat(GetTempVar(), "REAL");
                VariableEntry temp2 = Creat(GetTempVar(), "REAL");
                Generate("ltof", id2.GetName(), temp1.GetName());
                Generate("f" + opcode, id1.GetName(), temp1.GetName(), temp2.GetName());
                semanticStack.push(temp2);
            }
            semanticStack.push(EType.ARITHMETIC);
        }
    }

    // the token passed to the semantic actions should be an
    // operator
    // although this action is currently identical to
    // action 42, it will change in later phases
    //Push MULOP operator (*, /, etc.) on to stack.
    private void Action44(Token t)
    {
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL)
        {
            List<Integer> EFalse = (List<Integer>) semanticStack.pop();
            List<Integer> ETrue = (List<Integer>) semanticStack.pop();
            if (t.GetType().equals("MULOP") && t.GetVal().equals("5"))
            {
                Backpatch(ETrue, Qs.GetNextQuad());
            }
            semanticStack.push(ETrue);
            semanticStack.push(EFalse);
        }
        semanticStack.push(t);
    }

    //Perform MULOP based on OP popped from stack
    //Handles AND
    private void Action45(Token t) throws SymbolTableError, SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL)
        {
            List<Integer> E2False = (List<Integer>) semanticStack.pop();
            List<Integer> E2True = (List<Integer>) semanticStack.pop();
            Token operator = (Token) semanticStack.pop();

            if (operator.GetType().equals("MULOP") && operator.GetVal().equals("5"))
            {
                List<Integer> E1False = (List<Integer>) semanticStack.pop();
                List<Integer> E1True = (List<Integer>) semanticStack.pop();

                List<Integer> ETrue = E2True;
                List<Integer> EFalse = Merge(E1False, E2False);
                semanticStack.push(ETrue);
                semanticStack.push(EFalse);
                semanticStack.push(EType.RELATIONAL);
            }
        }
        //Assumes only 2 Etypes, assuming Arithmetic
        else
        {
            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
            Token operator = (Token) semanticStack.pop();
            String opcode = operator.GetOpCode();
            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

            if (TypeCheck(id1, id2) != 0 &&
                    (operator.GetOpCode().equals("MOD") || operator.GetOpCode().equals("DIV")))
            {
                // MOD and DIV require integer operands
                throw SemanticActionError.ErrorMsg(3, t);
            }

            if (TypeCheck(id1, id2) == 0)
            {
                if (opcode.equals("mod"))
                {
                    VariableEntry temp1 = Creat(GetTempVar(), "INTEGER");
                    VariableEntry temp2 = Creat(GetTempVar(), "INTEGER");
                    VariableEntry temp3 = Creat(GetTempVar(), "INTEGER");
                    Generate("div", id1.GetName(), id2.GetName(), temp1.GetName());
                    Generate("mul", id2.GetName(), temp1.GetName(), temp2.GetName());
                    Generate("sub", id1.GetName(), temp2.GetName(), temp3.GetName());
                    semanticStack.push(temp3);
                } else if (opcode.equals("div") && operator.GetVal().equals("2"))
                {
                    VariableEntry temp1 = Creat(GetTempVar(), "REAL");
                    VariableEntry temp2 = Creat(GetTempVar(), "REAL");
                    VariableEntry temp3 = Creat(GetTempVar(), "REAL");
                    Generate("ltof", id1.GetName(), temp1.GetName());
                    Generate("ltof", id2.GetName(), temp2.GetName());
                    Generate("fdiv", temp1.GetName(), temp2.GetName(), temp3.GetName());
                    semanticStack.push(temp3);
                } else
                {
                    VariableEntry temp = Creat(GetTempVar(), "INTEGER");
                    Generate(opcode, id1.GetName(), id2.GetName(), temp.GetName());
                    semanticStack.push(temp);
                }
            } else if (TypeCheck(id1, id2) == 1)
            {
                VariableEntry temp = Creat(GetTempVar(), "REAL");
                Generate("f" + opcode, id1.GetName(), id2.GetName(), temp.GetName());
                semanticStack.push(temp);
            } else if ((TypeCheck(id1, id2) == 2) || (TypeCheck(id1, id2) == 3))
            {
                VariableEntry temp1 = Creat(GetTempVar(), "REAL");
                VariableEntry temp2 = Creat(GetTempVar(), "REAL");
                Generate("ltof", id2.GetName(), temp1.GetName());
                Generate("f" + opcode, id1.GetName(), temp1.GetName(), temp2.GetName());
                semanticStack.push(temp2);
            }
            semanticStack.push(EType.ARITHMETIC);
        }
        //semanticStack.push(EType.ARITHMETIC);
    }

    //Look up value of variable or constant from SymbolTable
    private void Action46(Token t) throws SemanticActionError, SymbolTableError
    {
        if (t.GetType().equals("IDENTIFIER"))
        {
            // look for the token in the global or local symbol
            // table, as appropriate
            SymbolTableEntry id = LookUpID(t.GetVal());
            // if token is not found
            if (id == null)
            {
                throw SemanticActionError.ErrorMsg(1, t);
            }
            semanticStack.push(id);
        } else if (t.GetType().equals("INTCONSTANT") ||
                t.GetType().equals("REALCONSTANT"))
        {
            // look for the token in the constant symbol table
            SymbolTableEntry id = LookUpConstant(t.GetVal());
            // if token is not found
            if (id == null)
            {
                if (t.GetType().equals("INTCONSTANT"))
                {
                    id = new ConstantEntry(t.GetVal(), "INTEGER");
                } else if (t.GetType().equals("REALCONSTANT"))
                {
                    id = new ConstantEntry(t.GetVal(), "REAL");
                }
                cTable.Insert(t.GetVal(), id);
            }
            semanticStack.push(id);
        }
        semanticStack.push(EType.ARITHMETIC);
        //dumpStack();
    }

    //Handles Reserved word NOT
    private void Action47(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.RELATIONAL)
        {
            throw SemanticActionError.ErrorMsg(1, t);
        }

        // swap ETrue and EFalse on the stack
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        semanticStack.push(EFalse);
        semanticStack.push(ETrue);
        semanticStack.push(EType.RELATIONAL);
    }

    //Array LookUp.
    private void Action48(Token t) throws CompilerError
    {
        SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
        if (offset != null)
        {
            if (offset.IsFunction())
            {
                //call action 52 with the token from the parser
                Execute("#52", t);
            } else
            {
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                VariableEntry temp = Creat(GetTempVar(), id.GetType());
                Generate("load", id.GetName(), offset.GetName(), temp.GetName());
                semanticStack.push(temp);
            }
        }
        semanticStack.push(EType.ARITHMETIC);
    }

    //Ensure this is a function & get parameter data.
    private void Action49(Token t) throws SemanticActionError
    {
        // get etype and id but do not change the stack
        EType etype = (EType) semanticStack.pop();
        // id should be a function
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        semanticStack.push(etype);

        if (etype != EType.ARITHMETIC)
        {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        if (!id.IsFunction())
        {
            throw SemanticActionError.ErrorMsg(8, t);
        }
        paramCount.push(0);
        paramStack.push(id.GetParameterInfo());
    }

    //Generate code to assign memory for function parameters & call function.
    private void Action50(Token t) throws SemanticActionError, SymbolTableError
    {
        // the parameters must be generated from the bottom-most to
        // the top-most
        Stack<SymbolTableEntry> parameters = new Stack<>();
        // for each parameter on the stack
        //have to check to be sure it is a SymbolTableEntry in case it is READ WRITE or other reserved Strings
        while (semanticStack.peek() instanceof SymbolTableEntry
                && (((SymbolTableEntry) semanticStack.peek()).IsArray()
                || (((SymbolTableEntry) semanticStack.peek()).IsConstant()
                || ((SymbolTableEntry) semanticStack.peek()).IsVariable())))
        {
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        // Generate code for each of the parameters
        while (!parameters.empty())
        {
            // this is one place where you will use GetParamPrefix()
            Generate("param", parameters.pop().GetName());
            localMemory++;
        }

        EType etype = (EType) semanticStack.pop();
        FunctionEntry id = (FunctionEntry) semanticStack.pop();
        int numParams = paramCount.pop();
        if (numParams > id.GetParameterCount())
        {
            throw SemanticActionError.ErrorMsg(7, t);
        }
        GenerateHard("call", id.GetName(), Integer.toString(numParams));
        paramStack.pop();
        nextParam = 0;

        VariableEntry temp = Creat(GetTempVar(), id.GetResult().GetType());
        Generate("move", id.GetResult().GetName(), temp.GetName());
        semanticStack.push(temp);
        semanticStack.push(EType.ARITHMETIC);
    }

    //Generate code to assign memory for procedure parameters & call function.
    private void Action51(Token t) throws CompilerError
    {
        // get all of the parameters on the stack
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while ((semanticStack.peek() instanceof SymbolTableEntry)
                && (((SymbolTableEntry) semanticStack.peek()).IsArray()
                || ((SymbolTableEntry) semanticStack.peek()).IsConstant()
                || ((SymbolTableEntry) semanticStack.peek()).IsVariable()))
        {
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        EType etype = (EType) semanticStack.pop();
        ProcedureEntry id = (ProcedureEntry) semanticStack.pop();

        if (id.GetName().equals("READ") || id.GetName().equals("WRITE"))
        {
            // replace everything on the stack and call 51WRITE
            semanticStack.push(id);
            semanticStack.push(etype);
            while (!parameters.empty())
            {
                semanticStack.push(parameters.pop());
            }
            if (id.GetName().equals("READ"))
            {
                Execute("#51READ", t);
            }
            // id is WRITE
            else
            {
                Execute("#51WRITE", t);
            }
        }
        //else write
        else
        {
            int numParams = paramCount.pop();
            if (numParams != id.GetParameterCount())
            {
                throw SemanticActionError.ErrorMsg(7, t);
            }

            while (!parameters.empty())
            {
                // this is one place where you will use GetParamPrefix()
                Generate("param", parameters.pop().GetName());
                localMemory++;
            }
            GenerateHard("call", id.GetName(), Integer.toString(numParams));
            paramStack.pop();
            nextParam = 0;
        }
    }

    //Read input from user.
    private void Action51READ(Token t) throws SymbolTableError
    {
        // for every parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while (semanticStack.peek() instanceof SymbolTableEntry && ((SymbolTableEntry) semanticStack.peek()).IsVariable())
        {
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        while (!parameters.empty())
        {
            SymbolTableEntry id = parameters.pop();
            if (id.GetType().equals("REAL"))
            {
                Generate("finp", id.GetName());
            } else
            {
                Generate("inp", id.GetName());
            }
        }
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        paramCount.pop();
    }

    //Display variable name and contents.
    private void Action51WRITE(Token t) throws SymbolTableError
    {
        // for each parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while ((semanticStack.peek() instanceof SymbolTableEntry)
                && (((SymbolTableEntry) semanticStack.peek()).IsConstant()
                || (((SymbolTableEntry) semanticStack.peek()).IsVariable())))
        {
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        while (!parameters.empty())
        {
            SymbolTableEntry id = parameters.pop();
            if (id.IsConstant())
            {
                if (id.GetType().equals("REAL"))
                {
                    Generate("foutp", id.GetName());
                } else
                { // id.GetType() == INTEGER
                    Generate("outp", id.GetName());
                }
            } else
            { // id is a variable entry
                GenerateHard("Print", "\"" + id.GetName() + " = \"");
                if (id.GetType().equals("REAL"))
                {
                    Generate("foutp", id.GetName());
                } else
                { // id.GetType() == INTEGER
                    Generate("outp", id.GetName());
                }
            }
            Generate("newl");
        }
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        paramCount.pop();
    }

    //Case for function with no parameters.
    private void Action52(Token t) throws SymbolTableError, SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (!id.IsFunction())
        {
            throw SemanticActionError.ErrorMsg(8, t);
        }
        if (id.GetParameterCount() > 0)
        {
            throw SemanticActionError.ErrorMsg(7, t);
        }
        Generate("call", id.GetName(), "0");
        VariableEntry temp = Creat(GetTempVar(), id.GetType());
        Generate("move", id.GetResult().GetName(), temp.GetName());
        semanticStack.push(temp);
        semanticStack.push(null);
    }

    //Look up variable or function result.
    private void Action53(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (id.IsFunction())
        {
            if (id != currentFunction)
            {
                throw SemanticActionError.ErrorMsg(6, t);
            }
            semanticStack.push(id.GetResult());
            semanticStack.push(EType.ARITHMETIC);
        } else
        {
            semanticStack.push(id);
            semanticStack.push(etype);
        }
    }

    //Confirm STMT is a procedure call.
    private void Action54(Token t) throws SemanticActionError
    {
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (!id.IsProcedure())
        {
            throw SemanticActionError.ErrorMsg(6, t);
        }
        semanticStack.push(etype);
    }

    //Generate end-of-MAIN:: wrapper code
    private void Action55(Token t) throws SymbolTableError
    {
        //These are traces now handled in a separate end of compilation function
        //System.out.println("");
        //Qs.Print();
        //System.out.println("");
        Backpatch(globalStore, globalMemory);
        GenerateEnd("free", Integer.toString(globalMemory));
        Generate("PROCEND");
    }

    //Generate start-of-MAIN:: wrapper code
    private void Action56(Token t) throws SymbolTableError
    {
        Generate("PROCBEGIN", "main");
        globalStore = Qs.GetNextQuad();
        //the underscore as the second argument in Generate
        //is a placeholder that will be filled in later by Backpatch
        Generate("alloc", "_");
    }

}
