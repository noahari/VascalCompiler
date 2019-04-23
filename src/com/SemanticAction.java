package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SemanticAction {

    //true = insert mode, false = search mode
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

    public Stack<Object> getSemanticStack(){
        return semanticStack;
    }

    SemanticAction() throws CompilerError {
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
        initgTable();
        //gTable.dumpTable();
    }

    Quadruples getQs(){return Qs;}

    private void initgTable() throws SymbolTableError {
        gTable.insert("MAIN", new ProcedureEntry("MAIN", 0, null));
        gTable.insert("READ", new ProcedureEntry("READ", 0, null));
        gTable.insert("WRITE", new ProcedureEntry("WRITE", 0, null));
        //IO added in 9 when it matters
    }

    public void initlTable() {
    }

    private SymbolTableEntry lookupID(Token t) {
        SymbolTableEntry ste = lTable.lookup(t.getVal());
        if (ste == null) {
            ste = gTable.lookup(t.getVal());
        }
        return ste;
    }

    private SymbolTableEntry lookupID(String t){
        SymbolTableEntry ste = lTable.lookup(t);
        if (ste == null) {
            ste = gTable.lookup(t);
        }
        if(ste == null){
            ste = lookupConstant(t);
        }
        return ste;
    }

    private SymbolTableEntry lookupConstant(String t){return cTable.lookup(t);}

    private int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2) {
        String type1 = id1.getType();
        String type2 = id2.getType();

        //check cases where equal first
        if (type1.equals("REAL") || type1.equals("REALCONSTANT")) {
            //real, real
            if (type2.equals("REAL") || type2.equals("REALCONSTANT")) {
                return 1;
            }
            //real, int
            else if(type2.equals("INTEGER") || type2.equals("INTCONSTANT")){
                return 2;
            }
        }
        else if(type1.equals("INTEGER") || type1.equals("INTCONSTANT")){
            //int, real
            if (type2.equals("REAL") || type2.equals("REALCONSTANT")) {
                return 3;
            }
            //int, int
            else if(type2.equals("INTEGER") || type2.equals("INTCONSTANT")){
                return 0;
            }
        }
        //fail case
        return -1;
    }

    private VariableEntry creat(String name, String type) throws SymbolTableError {
        VariableEntry ve = new VariableEntry();
        ve.setType(type);
        ve.setName(name);
        // store the address as negative to distinguish between
        // temporary variables
        if (envFlag) {
            ve.setAddress(globalMemory);
            globalMemory++;
            gTable.insert(ve.getName(), ve);
        }
        else {
            ve.setAddress(localMemory);
            localMemory++;
            lTable.insert(ve.getName(), ve);
        }
        tempVarUID++;
        return ve;
    }

    private String getTempVar() {
        return "temp"+Integer.toString(tempVarUID);
    }

    private Boolean isReserved(String s) {return s.equals("main") || s.equals("_");}

    private void generate(String op1) {
        Quadruple tvi = new Quadruple(op1);
        Qs.addQuad(tvi);
    }

    private void generate(String op1, String op2) throws SymbolTableError {
        SymbolTableEntry op2t = lookupID(op2);
        String op2f = "";
        if (isReserved(op2)) {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null) {
            //must add if case in event called on Parameter
            if(op1.equals("param")){op2f = getParamPrefix(op2t) + Integer.toString(getSTEAddress(op2t));}
            else{op2f = getSTEPrefix(op2t) + Integer.toString(getSTEAddress(op2t));}
        }
        Quadruple tvi = new Quadruple(op1, op2f);
        Qs.addQuad(tvi);
    }

    private void generateHard(String op1, String op2){
        Quadruple tvi = new Quadruple(op1, op2);
        Qs.addQuad(tvi);
    }

    private void generateHard(String op1, String op2, String op3){
        Quadruple tvi = new Quadruple(op1, op2, op3);
        Qs.addQuad(tvi);
    }

    private void generate(String op1, String op2, String op3) throws SymbolTableError {
        SymbolTableEntry op2t = lookupID(op2);
        String op2f = "";
        SymbolTableEntry op3t = lookupID(op3);
        String op3f = "";
        String prefix,address;
        if (isReserved(op2)) {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null) {
            prefix = getSTEPrefix(op2t);
            address = Integer.toString(getSTEAddress(op2t));
            op2f = prefix + address;
        }
        if (isReserved(op3)) {
            //if it is reserved, don't bother looking, it is what it is
            op3f = op3;
        } else if (op3t != null) {
            op3f = getSTEPrefix(op3t) + Integer.toString(getSTEAddress(op3t));
        }
        Quadruple tvi = new Quadruple(op1, op2f, op3f);
        Qs.addQuad(tvi);
    }

    private void generate(String op1, String op2, String op3, String op4) throws SymbolTableError {
        SymbolTableEntry op2t = lookupID(op2);
        String op2f = "";
        SymbolTableEntry op3t = lookupID(op3);
        String op3f = "";
        SymbolTableEntry op4t = lookupID(op4);
        String op4f = "";
        if (isReserved(op2)) {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null) {
            op2f = getSTEPrefix(op2t) + Integer.toString(getSTEAddress(op2t));
        }
        if (isReserved(op3)) {
            //if it is reserved, don't bother looking, it is what it is
            op3f = op3;
        } else if (op3t != null) {
            op3f = getSTEPrefix(op3t) + Integer.toString(getSTEAddress(op3t));
        }
        if (isReserved(op4)) {
            //if it is reserved, don't bother looking, it is what it is
            op4f = op4;
        } else if (op4t != null) {
            op4f = getSTEPrefix(op4t) + Integer.toString(getSTEAddress(op4t));
        }
        Quadruple tvi = new Quadruple(op1, op2f, op3f, op4f);
        Qs.addQuad(tvi);
    }

    //new separate generate overload for getSTEAddress
    private void generateAddress(String op1, String op2, String op3) throws SymbolTableError{
        Quadruple quad;
        String address = null;
        String prefix;

        if (lookupID(op3) != null){
            prefix = getSTEPrefix(lookupID(op3));
            address = prefix + Integer.toString(getSTEAddress(lookupID(op3)));
        }
        quad = new Quadruple(op1, op2, address);
        Qs.addQuad(quad);
    }

    //used for generate at the end-of-main
    private void generateEnd(String op1, String op2){
        Quadruple quad;
        quad = new Quadruple(op1, op2);
        Qs.addQuad(quad);
    }

    //Currently used for Action9, not sure if necessary
    //there may be a more elegant solution than overriding
    //for one specific scenario
    private void generate9(String op1, String op2, String op3){
        Quadruple quad;
        quad = new Quadruple(op1, op2, op3);
        Qs.addQuad(quad);
    }

    private void backpatch(int i, int x) {
        //set the field of ith Quad's second field to x
        Qs.setField(i, 1, Integer.toString(x));
    }

    private void backpatch(List<Integer> list, int x){
          for(Integer i : list) {
              if (Qs.getField(i, 0).equals("goto")) {
                  Qs.setField(i, 1, Integer.toString(x));
              } else {
                  Qs.setField(i, 3, Integer.toString(x));
              }
          }
    }

    private String getParamPrefix(SymbolTableEntry param) {
        if (envFlag){
            return "@_";
        }
        //local
        else{
            if (param.isParameter()){
                return "%";
            }
            else{
                return "@%";
            }
        }
    }

    private int getSTEAddress(SymbolTableEntry ste) throws SymbolTableError {
        if (ste.isArray() || ste.isVariable()) {
            // array entries and variable entries are
            // assigned address when they are initialized
            return ste.getAddress();
        }
        if (ste.isConstant()) {
            // constants do not have an address, and a
            // temporary variable must be created to store it
            VariableEntry temp = creat(getTempVar(), ste.getType());
            // move the constant into the temporary variable
            generateAddress("move", ste.getName(), temp.getName());
            // return the address of the temporary variable
            return temp.getAddress();
        }
        //otherwise, error
        return -1;
    }

    //this can likely be simplified, edge cases have bloated the function
    private String getSTEPrefix(SymbolTableEntry ste) {
        if (envFlag){
            return "_";
        }
        //local
        else{
            SymbolTableEntry entry = lTable.lookup(ste.getName());
            // entry is a global variable?
            if (entry == null){
                SymbolTableEntry cEntry = cTable.lookup(ste.getName());
                if(cEntry != null){
                    //if found in cTable, is constant
                    return "%";
                }
                //otherwise it is global
                return "_";
            }
            else {
                if (ste.isParameter()){
                    //parameter must be dereferenced
                    return "^%";
                }
                //ste is not a parameter
                else{
                    return "%";
                }
            }
        }
    }

    /*
    private String getSTEPrefix(SymbolTableEntry ste) {
        if (envFlag) {
            return "_";
        } else {
            //local env
            SymbolTableEntry entry = lTable.lookup(ste.getName());
            if (entry == null) {
                //entry is a global var
                return "_";
            } else {
                return "%";
            }
        }
    }
    */

    private List<Integer> makeList(int i){
        List<Integer> list = new ArrayList<Integer>();
        list.add(i);
        return list;
    }

    private List<Integer> merge(List<Integer> l1, List<Integer> l2){
        l1.addAll(l2);
        return l1;
    }

    public void dumpStack(){
        Stack<Object> dumpStack = new Stack();
        dumpStack = (Stack<Object>) semanticStack.clone();
        System.out.print("[");
        while(!dumpStack.empty()){
            System.out.print(dumpStack.peek());
            dumpStack.pop();
            if(!dumpStack.empty()){
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    void Execute(String action, Token t) throws CompilerError {
        //dumpStack();
        switch (action) {
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

    private void Action1(Token t) {
        modeFlag = true;
    }

    private void Action2(Token t) {
        modeFlag = false;
    }

    private void Action3(Token t) throws SymbolTableError {
        String tokenType = ((Token) semanticStack.pop()).getType();
        if (arrayFlag) {
            int upperBound = ((Token) semanticStack.pop()).tokenToInt();
            int lowerBound = ((Token) semanticStack.pop()).tokenToInt();
            int memorySize = (upperBound - lowerBound) + 1;

            while (!(semanticStack.isEmpty()) && (((Token) (semanticStack.peek())).getType().equals("IDENTIFIER")) && (semanticStack.peek() instanceof Token)) {
                Token tok = (Token) semanticStack.pop();
                ArrayEntry id = new ArrayEntry(tok.getVal(), 0, tokenType, upperBound, lowerBound);
                id.setType(tokenType);
                id.setUpperBound(upperBound);
                id.setLowerBound(lowerBound);

                if (envFlag) {
                    id.setAddress(globalMemory);
                    gTable.insert(tok.getVal(), id);
                    globalMemory += memorySize;
                } else {
                    id.setAddress(localMemory);
                    lTable.insert(tok.getVal(), id);
                    localMemory += memorySize;

                }
            }
        } else {
            while (!semanticStack.empty() && (semanticStack.peek() instanceof Token) && (((Token) (semanticStack.peek())).getType().equals("IDENTIFIER"))) {
                Token tok = (Token) semanticStack.pop();
                VariableEntry id = new VariableEntry(tok.getVal(), 0, null);
                id.setType(tokenType);

                if (envFlag) {
                    id.setAddress(globalMemory);
                    //System.out.println(tok.getType() + tok.getVal() + id.getType() + id.getName());
                    gTable.insert(tok.getVal(), id);
                    globalMemory++;
                } else {
                    id.setAddress(localMemory);
                    lTable.insert(tok.getVal(), id);
                    localMemory++;
                }
            }
        }
        //gTable.dumpTable();
        arrayFlag = false;
    }

    //for "REAL" v "INT"
    private void Action4(Token t) {
        semanticStack.push(t);
    }

    private void Action5(Token t)throws SymbolTableError{
        modeFlag = false;
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        generateHard("PROCBEGIN", id.getName());
        localStore = Qs.getNextQuad();
        generate("alloc", "_");
    }

    private void Action6(Token t) {
        arrayFlag = true;
    }

    //for array member intconstants
    private void Action7(Token t) {
        semanticStack.push(t);
    }

    private void Action9(Token t) throws SymbolTableError {
        //pop the IDs
        //output
        Token id1 = (Token) semanticStack.pop();
        //input
        Token id2 = (Token) semanticStack.pop();
        //program name
        Token id3 = (Token) semanticStack.pop();

        //System.out.println("id1: " + id1.getVal() + ", id2: " + id2.getVal() + ", id3: " + id3.getVal());
        //System.out.println("id1: " + id1.getType() + ", id2: " + id2.getType() + ", id3: " + id3.getType());

        //add entry output:IODEVICE OUTPUT
        gTable.insert(id1.getVal(), new IODeviceEntry(id1.getVal()));
        //add entry input:IODEVICE INPUT
        gTable.insert(id2.getVal(), new IODeviceEntry(id2.getVal()));
        //add entry
        gTable.insert(id3.getVal(), new ProcedureEntry(id3.getVal(), 0, null));

        //dump table
        //gTable.dumpTable();
        //generate stub code
        generate9("call", "main", "0");
        generate("exit");
        //insert = false
        modeFlag = false;
    }

    private void Action11(Token t)throws SymbolTableError{
        envFlag = true;
        // delete the local symbol table
        lTable = new SymbolTable();
        currentFunction = null;
        backpatch(localStore, localMemory);
        generateHard("free", Integer.toString(localMemory));
        generate("PROCEND");
    }

    //for identifiers
    private void Action13(Token t) {
        semanticStack.push(t);
    }

    private void Action15(Token t)throws SemanticActionError, SymbolTableError{
        // create a variable to store the result of the function
        VariableEntry result = creat(t.getVal() + "_RESULT","INTEGER");
        // set the result tag of the variable entry class
        result.setResult();
        // create a new function entry with name from the token
        // from the parser and the result variable just created
        SymbolTableEntry id = new FunctionEntry(t.getVal(), result);
        gTable.insert(id.getName(), id);
        envFlag = false;
        localMemory = 0;
        currentFunction = id;
        semanticStack.push(id);
    }

    private void Action16(Token t){
        // this action sets the type of the function and its result
        Token type = (Token) semanticStack.pop();
        FunctionEntry id = (FunctionEntry) semanticStack.peek();
        id.setType(type.getType());
        // set the type of the result variable of id
        id.setResultType(type.getType());
        currentFunction = id;
    }

    private void Action17(Token t)throws SymbolTableError{
        // create a new procedure entry with the name of the token
        // from the parser
        SymbolTableEntry id = new ProcedureEntry(t.getVal());
        gTable.insert(id.getName(), id);
        envFlag = false;
        localMemory = 0;
        currentFunction = id;
        semanticStack.push(id);
    }

    private void Action19(Token t){
        paramCount = new Stack<>();
        paramCount.push(0);
    }

    private void Action20(Token t){
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        int numParams = paramCount.pop();
        // id is a function entry or a procedure entry
        id.setParameterCount(numParams);
    }

    private void Action21(Token t)throws SymbolTableError{
        Token type = (Token) semanticStack.pop();

        // if array, then pop the upper and lower bounds
        int upperBound = -1;
        int lowerBound = -1;
        if (arrayFlag) {
            upperBound = ((Token)semanticStack.pop()).tokenToInt();
            lowerBound = ((Token)semanticStack.pop()).tokenToInt();
        }

        // the tokens on the stack, which represent parameters,
        // must be added from the bottom-most id to the top-most
        Stack<Token> parameters = new Stack<>();

        // as the ids are popped off the stack, push them onto to
        // the new stack to reverse the order
        while (semanticStack.peek() instanceof Token && ((Token) semanticStack.peek()).getType().equals("IDENTIFIER")) {
            parameters.push((Token)semanticStack.pop());
        }

        while (!parameters.empty()) {
            SymbolTableEntry var;
            Token param = parameters.pop();
            if (arrayFlag) {
                var = new ArrayEntry(param.getVal(), localMemory, type.getType(), upperBound, lowerBound);
            }
            else {
                var = new VariableEntry(param.getVal(), localMemory, type.getType());
            }
            var.setParameter();
            lTable.insert(var.getName(), var);
            // current function is either a procedure or function entry
            currentFunction.addParam(var);
            localMemory++;
            // increment the top of paramCount
            paramCount.push(paramCount.pop() + 1);
        }
        arrayFlag = false;
    }

    private void Action22(Token t)throws SemanticActionError{
        EType eType = (EType) semanticStack.pop();
        if(eType != EType.RELATIONAL){
            throw SemanticActionError.ErrorMsg(2,t);
        }
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        backpatch(ETrue, Qs.getNextQuad());
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    private void Action24(Token t){
        int beginLoop = Qs.getNextQuad();
        semanticStack.push(beginLoop);
    }

    private void Action25(Token t)throws SemanticActionError{
        //dumpStack();
        //Qs.print();
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.RELATIONAL) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        backpatch(ETrue, Qs.getNextQuad());
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    private void Action26(Token t) throws SymbolTableError {
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        // beginLoop is pushed onto the stack in action 24
        int beginLoop = (int) semanticStack.pop();
        generateHard("goto", Integer.toString(beginLoop));
        backpatch(EFalse, Qs.getNextQuad());
    }

    private void Action27(Token t) throws SymbolTableError {
        List<Integer> skipElse = makeList(Qs.getNextQuad());
        generate("goto", "_");
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        backpatch(EFalse, Qs.getNextQuad());
        semanticStack.push(skipElse);
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
    }

    private void Action28(Token t){
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        // skipElse is pushed onto the stack in action 27
        List<Integer> skipElse = (List<Integer>) semanticStack.pop();
        backpatch(skipElse, Qs.getNextQuad());
    }

    private void Action29(Token t){
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        backpatch(EFalse, Qs.getNextQuad());
    }

    //create more actions
    //Check to see if a variable has been declared
    private void Action30(Token t) throws SemanticActionError {
        SymbolTableEntry id = lookupID(t);
        if (id == null) {
            throw SemanticActionError.ErrorMsg(1, t);
        }
        semanticStack.push(id);
        semanticStack.push(EType.ARITHMETIC);
    }

    //Put the value of variable ID2 in ID1 (i.e. Variable assignment.)
    private void Action31(Token t) throws SemanticActionError, SymbolTableError{
        EType etype = (EType) semanticStack.pop();

        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2, t);
        }
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
        //System.out.println(id2.getName() + " value: " + lTable.lookup(id2.getName()));
        int typeVal = typeCheck(id1, id2);

        if (typeVal == 3) {
            Qs.print();
            throw SemanticActionError.ErrorMsg(2, t);
        }
        if (typeVal == 2) {
            VariableEntry temp = creat(getTempVar(), "REAL");
            generate("ltof", id2.getName(), temp.getName());
            if (offset == null){
                generate("move", temp.getName()
                        , id1.getName());
            }
            else{
                generate("stor", temp.getName(), offset.getName(), id1.getName());
            }
        } else {
            if (offset == null) {
                generate("move", id2.getName(), id1.getName());
            }
            else {
                generate("stor", id2.getName(), offset.getName(), id1.getName());
            }
        }
    }

    private void Action32(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(4,t);
        }
        if (!id.isArray()) {
            throw SemanticActionError.ErrorMsg(5,t);
        }
    }

    private void Action33(Token t)throws SemanticActionError,SymbolTableError{
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (!(id.getType().equals("INTEGER"))) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        ArrayEntry array = (ArrayEntry) semanticStack.peek();
        VariableEntry temp1 = creat(getTempVar(), "INTEGER");
        VariableEntry temp2 = creat(getTempVar(), "INTEGER");
        generate("move", Integer.toString(array.getLowerBound()), temp1.getName());
        generate("sub", id.getName(), temp1.getName(), temp2.getName());
        semanticStack.push(temp2);
    }

    private void Action34(Token t)throws CompilerError{
        //cannot cast variableEntry to EType
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (id.isFunction()) {
            semanticStack.push(etype);
            Execute("#52", t);
        }
        else {
            semanticStack.push(null);
        }
    }

    private void Action35(Token t){
        EType etype = (EType) semanticStack.pop();
        // id is a procedure entry
        ProcedureEntry id = (ProcedureEntry) semanticStack.peek();
        semanticStack.push(etype);
        paramCount.push(0);
        paramStack.push(id.getParameterInfo());
    }

    private void Action36(Token t) throws SemanticActionError, SymbolTableError {
        EType etype = (EType) semanticStack.pop();
        ProcedureEntry id = (ProcedureEntry) semanticStack.pop();
        if (id.getParameterCount() != 0) {
            throw SemanticActionError.ErrorMsg(7,t);
        }
        generate("call", id.getName(), "0");
    }

    private void Action37(Token t) throws SemanticActionError {
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2,t);
        }

        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (!id.isVariable() && !id.isConstant() && !id.isFunctionResult() && !id.isArray()) {
            throw SemanticActionError.ErrorMsg(3,t);
        }

        // increment the top of paramCount
        paramCount.push(paramCount.pop() + 1);

        // find the name of the procedure/function on the bottom of the stack
        Stack parameters = new Stack();
        while(!(semanticStack.peek() instanceof FunctionEntry || semanticStack.peek() instanceof ProcedureEntry)){
            parameters.push(semanticStack.pop());
        }
        // funcId is a procedure or function entry
        SymbolTableEntry funcId = (SymbolTableEntry) semanticStack.peek();
        while (!parameters.empty()) {
            semanticStack.push(parameters.pop());
        }

        if (!(funcId.getName().equals("READ") || funcId.getName().equals("WRITE"))) {
            if (paramCount.peek() > funcId.getParameterCount()) {
                throw SemanticActionError.ErrorMsg(7,t);
            }
            SymbolTableEntry param = paramStack.peek().get(nextParam);
            if (!(id.getType().equals(param.getType()))) {
                throw SemanticActionError.ErrorMsg(6,t);
            }
            if (param.isArray()) {
                if ((((ArrayEntry)id).getLowerBound() != ((ArrayEntry)param).getLowerBound()) ||
                        (((ArrayEntry)id).getUpperBound() != ((ArrayEntry)param).getUpperBound())) {
                    throw SemanticActionError.ErrorMsg(6,t);
                }
            }
            nextParam++;
        }
    }

    private void Action38(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        // token should be an operator
        semanticStack.push(t);
    }

    private void Action39(Token t)throws SemanticActionError,SymbolTableError{
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        Token operator = (Token) semanticStack.pop();
        // the operator must be replaced with the proper TVI code which
        // jump if the condition is me
        // ex. the token representing "<" should be replaced with "blt"
        String opcode = operator.getOpCode();
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
        int type = typeCheck(id1,id2);
        if (type == 2) {
            VariableEntry temp = creat(getTempVar(), "REAL");
            generate("ltof", id2.getName(), temp.getName());
            generate(opcode, id1.getName(), temp.getName(), "_");
        }
        else if (type == 3) {
            VariableEntry temp = creat(getTempVar(), "REAL");
            generate("ltof", id1.getName(), temp.getName());
            generate(opcode, temp.getName(), id2.getName(), "_");
        }
        else {
            generate(opcode, id1.getName(), id2.getName(), "_");
        }
        generate("goto", "_");
        List<Integer> ETrue = makeList(Qs.getNextQuad() - 2);
        List<Integer> EFalse = makeList(Qs.getNextQuad() - 1);
        semanticStack.push(ETrue);
        semanticStack.push(EFalse);
        semanticStack.push(EType.RELATIONAL);
    }

    // the token should be a sign (unary plus or minus)
    //Push unary plus/minus to stack
    private void Action40(Token t){
        semanticStack.push(t);
    }

    //Apply unary plus/minus
    private void Action41(Token t)throws SymbolTableError,SemanticActionError{
        EType etype = (EType) semanticStack.pop();

        if (etype != EType.ARITHMETIC){
            throw SemanticActionError.ErrorMsg(2,t);
        }

        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        Token sign = (Token) semanticStack.pop();
        if (sign.getType().equals("UNARYMINUS")) {
            VariableEntry temp = creat(getTempVar(), id.getType());
            if (id.getType().equals("INTEGER")) {
                generate("uminus", id.getName(), temp.getName());
            }
            else{
                generate("fuminus", id.getName(), temp.getName());
            }
            semanticStack.push(temp);
        }
        else{
            semanticStack.push(id);
        }
        semanticStack.push(EType.ARITHMETIC);
    }

    // the token should be an operator
    //Push ADDOP operator (+, -, etc.) on to stack
    private void Action42(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();

        if (t.getType().equals("ADDOP") && t.getVal().equals("3")){
            if (etype != EType.RELATIONAL){
                throw SemanticActionError.ErrorMsg(2,t);
            }
            // the top of the stack should be a list of integers
            List<Integer> EFalse = (List<Integer>) semanticStack.peek();
            backpatch(EFalse, Qs.getNextQuad());
        }
        else{
            if (etype != EType.ARITHMETIC){
                throw SemanticActionError.ErrorMsg(2,t);
            }
        }
        semanticStack.push(t);
    }

    //Perform ADDOP based on OP popped from stack.
    private void Action43(Token t)throws SymbolTableError{
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL){
            List<Integer> E2False = (List<Integer>) semanticStack.pop();
            List<Integer> E2True = (List<Integer>)semanticStack.pop();
            Token operator = (Token) semanticStack.pop();
            List<Integer> E1False = (List<Integer>) semanticStack.pop();
            List<Integer> E1True = (List<Integer>) semanticStack.pop();

            List<Integer> ETrue = merge(E1True, E2True);
            List<Integer> EFalse = E2False;
            semanticStack.push(ETrue);
            semanticStack.push(EFalse);
            semanticStack.push(EType.RELATIONAL);
        }
        //currently only two ETypes, if more added, make else if(etype == EType.Arithmetic)
        else{
            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
            // this is one place where the operator from action 42 is popped
            Token operator = (Token)semanticStack.pop();
            // get the TVI opcode associated with the operator token
            // ex. for a token representing addition, opcode would be "add"
            String opcode = operator.getOpCode();
            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

            if (typeCheck(id1, id2) == 0){
                VariableEntry temp = creat(getTempVar(), "INTEGER");
                generate(opcode, id1.getName(), id2.getName(), temp.getName());
                semanticStack.push(temp);
            }
            else if (typeCheck(id1, id2) == 1){
                VariableEntry temp = creat(getTempVar(), "REAL");
                generate("f" + opcode, id1.getName(), id2.getName(), temp.getName());
                semanticStack.push(temp);
            }
            else if (typeCheck(id1, id2) == 2){
                VariableEntry temp1 = creat(getTempVar(), "REAL");
                VariableEntry temp2 = creat(getTempVar(), "REAL");
                generate("ltof", id2.getName(), temp1.getName());
                generate("f" + opcode, id1.getName(), temp1.getName(), temp2.getName());
                semanticStack.push(temp2);
            }
            else if (typeCheck(id1, id2) == 3){
                VariableEntry temp1 = creat(getTempVar(), "REAL");
                VariableEntry temp2 = creat(getTempVar(), "REAL");
                generate("ltof", id2.getName(), temp1.getName());
                generate("f" + opcode, id1.getName(), temp1.getName(), temp2.getName());
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
    private void Action44(Token t){
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL) {
            List<Integer> EFalse = (List<Integer>) semanticStack.pop();
            List<Integer> ETrue = (List<Integer>) semanticStack.pop();
            if (t.getType().equals("MULOP") && t.getVal().equals("5")) {
                backpatch(ETrue, Qs.getNextQuad());
            }
            semanticStack.push(ETrue);
            semanticStack.push(EFalse);
        }
        semanticStack.push(t);
    }

    //Perform MULOP based on OP popped from stack
    //Handles AND
    private void Action45(Token t) throws SymbolTableError, SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        if (etype == EType.RELATIONAL) {
            List<Integer> E2False = (List<Integer>) semanticStack.pop();
            List<Integer> E2True = (List<Integer>) semanticStack.pop();
            Token operator = (Token) semanticStack.pop();

            if (operator.getType().equals("MULOP") && operator.getVal().equals("5")) {
                List<Integer> E1False = (List<Integer>) semanticStack.pop();
                List<Integer> E1True = (List<Integer>) semanticStack.pop();

                List<Integer> ETrue = E2True;
                List<Integer> EFalse = merge(E1False, E2False);
                semanticStack.push(ETrue);
                semanticStack.push(EFalse);
                semanticStack.push(EType.RELATIONAL);
            }
        }
        //Assumes only 2 Etypes, assuming Arithmetic
        else {
            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
            Token operator = (Token) semanticStack.pop();
            String opcode = operator.getOpCode();
            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

            if (typeCheck(id1, id2) != 0 &&
                    (operator.getOpCode().equals("MOD") || operator.getOpCode().equals("DIV"))) {
                // MOD and DIV require integer operands
                throw SemanticActionError.ErrorMsg(3, t);
            }

            if (typeCheck(id1, id2) == 0) {
                if (opcode.equals("mod")) {
                    VariableEntry temp1 = creat(getTempVar(), "INTEGER");
                    VariableEntry temp2 = creat(getTempVar(), "INTEGER");
                    VariableEntry temp3 = creat(getTempVar(), "INTEGER");
                    generate("div", id1.getName(), id2.getName(), temp1.getName());
                    generate("mul", id2.getName(), temp1.getName(), temp2.getName());
                    generate("sub", id1.getName(), temp2.getName(), temp3.getName());
                    semanticStack.push(temp3);
                } else if (opcode.equals("div") && operator.getVal().equals("2")) {
                    VariableEntry temp1 = creat(getTempVar(), "REAL");
                    VariableEntry temp2 = creat(getTempVar(), "REAL");
                    VariableEntry temp3 = creat(getTempVar(), "REAL");
                    generate("ltof", id1.getName(), temp1.getName());
                    generate("ltof", id2.getName(), temp2.getName());
                    generate("fdiv", temp1.getName(), temp2.getName(), temp3.getName());
                    semanticStack.push(temp3);
                } else {
                    VariableEntry temp = creat(getTempVar(), "INTEGER");
                    generate(opcode, id1.getName(), id2.getName(), temp.getName());
                    semanticStack.push(temp);
                }
            } else if (typeCheck(id1, id2) == 1) {
                VariableEntry temp = creat(getTempVar(), "REAL");
                generate("f" + opcode, id1.getName(), id2.getName(), temp.getName());
                semanticStack.push(temp);
            } else if ((typeCheck(id1, id2) == 2) || (typeCheck(id1, id2) == 3)) {
                VariableEntry temp1 = creat(getTempVar(), "REAL");
                VariableEntry temp2 = creat(getTempVar(), "REAL");
                generate("ltof", id2.getName(), temp1.getName());
                generate("f" + opcode, id1.getName(), temp1.getName(), temp2.getName());
                semanticStack.push(temp2);
            }
            semanticStack.push(EType.ARITHMETIC);
        }
        //semanticStack.push(EType.ARITHMETIC);
    }

    //Look up value of variable or constant from SymbolTable
    private void Action46(Token t)throws SemanticActionError, SymbolTableError{
        if (t.getType().equals("IDENTIFIER")) {
            // look for the token in the global or local symbol
            // table, as appropriate
            SymbolTableEntry id = lookupID(t.getVal());
            // if token is not found
            if (id == null) {
                throw SemanticActionError.ErrorMsg(1,t);
            }
            semanticStack.push(id);
        }
        else if (t.getType().equals("INTCONSTANT") ||
                t.getType().equals("REALCONSTANT")){
            // look for the token in the constant symbol table
            SymbolTableEntry id = lookupConstant(t.getVal());
            // if token is not found
            if (id == null){
                if (t.getType().equals("INTCONSTANT")){
                    id = new ConstantEntry(t.getVal(), "INTEGER");
                }
                else if (t.getType().equals("REALCONSTANT")){
                    id = new ConstantEntry(t.getVal(), "REAL");
                }
                cTable.insert(t.getVal(), id);
            }
            semanticStack.push(id);
        }
        semanticStack.push(EType.ARITHMETIC);
        //dumpStack();
    }

    private void Action47(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        if (etype != EType.RELATIONAL) {
            throw SemanticActionError.ErrorMsg(1,t);
        }

        // swap ETrue and EFalse on the stack
        List<Integer> EFalse = (List<Integer>) semanticStack.pop();
        List<Integer> ETrue = (List<Integer>) semanticStack.pop();
        semanticStack.push(EFalse);
        semanticStack.push(ETrue);
        semanticStack.push(EType.RELATIONAL);
    }

    //Array lookup.
    private void Action48(Token t)throws CompilerError{
        SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
        if (offset != null) {
            if(offset.isFunction()){
                //call action 52 with the token from the parser
                Execute("#52", t);
            }
            else{
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                VariableEntry temp = creat(getTempVar(), id.getType());
                generate("load", id.getName(), offset.getName(), temp.getName());
                semanticStack.push(temp);
            }
        }
        semanticStack.push(EType.ARITHMETIC);
    }

    private void Action49(Token t) throws SemanticActionError {
        // get etype and id but do not change the stack
        EType etype = (EType) semanticStack.pop();
        // id should be a function
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        semanticStack.push(etype);

        if (etype != EType.ARITHMETIC) {
            throw SemanticActionError.ErrorMsg(2,t);
        }
        if (!id.isFunction()) {
            throw SemanticActionError.ErrorMsg(8,t);
        }
        paramCount.push(0);
        paramStack.push(id.getParameterInfo());
    }

    private void Action50(Token t) throws SemanticActionError, SymbolTableError {
        // the parameters must be generated from the bottom-most to
        // the top-most
        Stack<SymbolTableEntry> parameters = new Stack<>();
        // for each parameter on the stack
        //have to check to be sure it is a SymbolTableEntry in case it is READ WRITE or other reserved Strings
        while(semanticStack.peek() instanceof SymbolTableEntry
                && (((SymbolTableEntry) semanticStack.peek()).isArray()
                || (((SymbolTableEntry) semanticStack.peek()).isConstant()
                || ((SymbolTableEntry) semanticStack.peek()).isVariable()))){
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        // generate code for each of the parameters
        while (!parameters.empty()) {
            // this is one place where you will use getParamPrefix()
            generate("param", parameters.pop().getName());
            localMemory++;
        }

        EType etype = (EType) semanticStack.pop();
        FunctionEntry id = (FunctionEntry) semanticStack.pop();
        int numParams = paramCount.pop();
        if (numParams > id.getParameterCount()) {
            throw SemanticActionError.ErrorMsg(7,t);
        }
        generateHard("call", id.getName(), Integer.toString(numParams));
        paramStack.pop();
        nextParam = 0;

        VariableEntry temp = creat(getTempVar(), id.getResult().getType());
        generate("move", id.getResult().getName(), temp.getName());
        semanticStack.push(temp);
        semanticStack.push(EType.ARITHMETIC);
    }

    private void Action51(Token t) throws CompilerError {
        // get all of the parameters on the stack
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while ((semanticStack.peek() instanceof SymbolTableEntry)
                && (((SymbolTableEntry) semanticStack.peek()).isArray()
                || ((SymbolTableEntry) semanticStack.peek()).isConstant()
                || ((SymbolTableEntry) semanticStack.peek()).isVariable())){
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        EType etype = (EType) semanticStack.pop();
        ProcedureEntry id = (ProcedureEntry) semanticStack.pop();

        if (id.getName().equals("READ") || id.getName().equals("WRITE")) {
            // replace everything on the stack and call 51WRITE
            semanticStack.push(id);
            semanticStack.push(etype);
            while (!parameters.empty()) {
                semanticStack.push(parameters.pop());
            }
            if (id.getName().equals("READ")) {
                Execute("#51READ", t);
            }
            // id is WRITE
            else {
                Execute("#51WRITE", t);
            }
        }
        //else write
       else {
            int numParams = paramCount.pop();
            if (numParams != id.getParameterCount()) {
                throw SemanticActionError.ErrorMsg(7,t);
            }

            while(!parameters.empty()) {
                // this is one place where you will use getParamPrefix()
                generate("param", parameters.pop().getName());
                localMemory++;
            }
            generateHard("call", id.getName(), Integer.toString(numParams));
            paramStack.pop();
            nextParam = 0;
        }
    }

    private void Action51READ(Token t) throws SymbolTableError {
        // for every parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while (semanticStack.peek() instanceof SymbolTableEntry && ((SymbolTableEntry)semanticStack.peek()).isVariable()) {
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        while (!parameters.empty()) {
            SymbolTableEntry id = parameters.pop();
            if (id.getType().equals("REAL")) {
                generate("finp", id.getName());
            } else {
                generate("inp", id.getName());
            }
        }
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        paramCount.pop();
    }

    private void Action51WRITE(Token t) throws SymbolTableError {
        // for each parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        while ((semanticStack.peek() instanceof SymbolTableEntry)
                && (((SymbolTableEntry)semanticStack.peek()).isConstant()
                || (((SymbolTableEntry)semanticStack.peek()).isVariable()))){
            parameters.push((SymbolTableEntry) semanticStack.pop());
        }

        while (!parameters.empty()) {
            SymbolTableEntry id = parameters.pop();
            if (id.isConstant()) {
                if (id.getType().equals("REAL")) {
                    generate("foutp", id.getName());
                } else { // id.getType() == INTEGER
                    generate("outp", id.getName());
                }
            }
            else { // id is a variable entry
                generateHard("print", "\"" + id.getName() + " = \"");
                if (id.getType().equals("REAL")) {
                    generate("foutp", id.getName());
                } else { // id.getType() == INTEGER
                    generate("outp", id.getName());
                }
            }
            generate("newl");
        }
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        paramCount.pop();
    }

    private void Action52(Token t) throws SymbolTableError, SemanticActionError {
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (!id.isFunction()) {
            throw SemanticActionError.ErrorMsg(8,t);
        }
        if (id.getParameterCount() > 0) {
            throw SemanticActionError.ErrorMsg(7,t);
        }
        generate("call", id.getName(), "0");
        VariableEntry temp = creat(getTempVar(), id.getType());
        generate("move", id.getResult().getName(), temp.getName());
        semanticStack.push(temp);
        semanticStack.push(null);
    }

    private void Action53(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        if (id.isFunction()) {
           if (id != currentFunction) {
               throw SemanticActionError.ErrorMsg(6,t);
           }
           semanticStack.push(id.getResult());
           semanticStack.push(EType.ARITHMETIC);
        }
        else {
            semanticStack.push(id);
            semanticStack.push(etype);
        }
    }

    private void Action54(Token t)throws SemanticActionError{
        EType etype = (EType) semanticStack.pop();
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
        if (!id.isProcedure()) {
            throw SemanticActionError.ErrorMsg(6, t);
        }
        semanticStack.push(etype);
    }

    //Generate end-of-MAIN:: wrapper code
    private void Action55(Token t) throws SymbolTableError{
        //These are traces now handled in a separate end of compilation function
        //System.out.println("");
        //Qs.print();
        //System.out.println("");
        backpatch(globalStore, globalMemory);
        generateEnd("free", Integer.toString(globalMemory));
        generate("PROCEND");
    }

    //Generate start-of-MAIN:: wrapper code
    private void Action56(Token t)throws SymbolTableError{
        generate("PROCBEGIN", "main");
        globalStore = Qs.getNextQuad();
        //the underscore as the second argument in generate
        //is a placeholder that will be filled in later by backpatch
        generate("alloc", "_");
    }

}
