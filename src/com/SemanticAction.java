package com;

import com.sun.java_cup.internal.runtime.Symbol;

import javax.swing.*;
import java.util.Objects;
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
    private Stack<Object> semanticStack;
    private SymbolTable gTable;
    private SymbolTable lTable;
    private SymbolTable cTable;
    private Quadruples Qs;
    private int tempVarUID = 0;

    public SemanticAction() throws CompilerError {
        modeFlag = true;
        envFlag = true;
        arrayFlag = false;
        globalMemory = 0;
        localMemory = 0;
        globalStore = 0;
        semanticStack = new Stack<>();
        gTable = new SymbolTable();
        lTable = new SymbolTable();
        cTable = new SymbolTable();
        Qs = new Quadruples();
        initgTable();
        gTable.dumpTable();
    }

    Quadruples getQs(){return Qs;}

    public void initgTable() throws SymbolTableError {
        gTable.insert("MAIN", new ProcedureEntry("MAIN", 0, null));
        gTable.insert("READ", new ProcedureEntry("READ", 0, null));
        gTable.insert("WRITE", new ProcedureEntry("WRITE", 0, null));
        //IO added in 9 when it matters
    }

    public void initlTable() {
    }

    public SymbolTableEntry lookupID(Token t) {
        SymbolTableEntry ste = lTable.lookup(t.getVal());
        if (ste == null) {
            ste = gTable.lookup(t.getVal());
        }
        return ste;
    }

    public SymbolTableEntry lookupID(String t){
        SymbolTableEntry ste = lTable.lookup(t);
        if (ste == null) {
            ste = gTable.lookup(t);
        }
        if(ste == null){
            ste = lookupConstant(t);
        }
        return ste;
    }

    public SymbolTableEntry lookupConstant(String t){return cTable.lookup(t);}

    public int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2) {
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

    public VariableEntry creat(String name, String type) throws SymbolTableError {
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

    public String getTempVar() {
        return "temp"+Integer.toString(tempVarUID);
    }

    public Boolean isReserved(String s) {
        if (s.equals("MAIN") || s.equals("_")) {
            return true;
        } else {
            return false;
        }
    }

    public Quadruple generate(String op1) {
        Quadruple tvi = new Quadruple(op1);
        Qs.addQuad(tvi);
        return tvi;
    }

    public Quadruple generate(String op1, String op2) throws SymbolTableError {
        SymbolTableEntry op2t = lookupID(op2);
        String op2f = "";
        if (isReserved(op2)) {
            //if it is reserved, don't bother looking, it is what it is
            op2f = op2;
        } else if (op2t != null) {
            op2f = getSTEPrefix(op2t) + Integer.toString(getSTEAddress(op2t));
        }
        Quadruple tvi = new Quadruple(op1, op2f);
        Qs.addQuad(tvi);
        return tvi;
    }

    public Quadruple generate(String op1, String op2, String op3) throws SymbolTableError {
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
        return tvi;
    }

    public Quadruple generate(String op1, String op2, String op3, String op4) throws SymbolTableError {
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
            op3f = op3;
        } else if (op4t != null) {
            op4f = getSTEPrefix(op4t) + Integer.toString(getSTEAddress(op4t));
        }
        Quadruple tvi = new Quadruple(op1, op2f, op3f, op4f);
        Qs.addQuad(tvi);
        return tvi;
    }

    //new separate generate overload for getSTEAddress
    public Quadruple generateAddress(String op1, String op2, String op3) throws SymbolTableError{
        Quadruple quad;
        String address = null;
        String prefix;

        if (lookupID(op3) != null){
            prefix = getSTEPrefix(lookupID(op3));
            address = prefix + Integer.toString(getSTEAddress(lookupID(op3)));
        }
        quad = new Quadruple(op1, op2, address);
        Qs.addQuad(quad);
        return quad;
    }

    //used for generate at the end-of-main
    public Quadruple generateEnd(String op1, String op2){
        Quadruple quad;
        quad = new Quadruple(op1, op2);
        Qs.addQuad(quad);
        return quad;
    }

    //Currently used for Action9, not sure if necessary
    //there may be a more elegant solution than overriding
    //for one specific scenario
    Quadruple generate9(String op1, String op2, String op3){
        Quadruple quad;
        quad = new Quadruple(op1, op2, op3);
        Qs.addQuad(quad);
        return quad;
    }

    void backpatch(int i, int x) {
        //set the field of ith Quad's second field to x
        Qs.setField(i, 1, Integer.toString(x));
    }

    int getSTEAddress(SymbolTableEntry ste) throws SymbolTableError {
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

    String getSTEPrefix(SymbolTableEntry ste) {
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

    public void Execute(String action, Token t) throws CompilerError {
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
            case "#6":
                Action6(t);
                break;
            case "#7":
                Action7(t);
                break;
            case "#9":
                Action9(t);
                break;
            case "#13":
                Action13(t);
                break;
            case "#30":
                Action30(t);
                break;
            case "#31":
                Action31(t);
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
            case "#48":
                Action48(t);
                break;
            case "#55":
                Action55(t);
                break;
            case "#56":
                Action56(t);
                break;
        }
    }

    void Action1(Token t) {
        modeFlag = true;
    }

    void Action2(Token t) {
        modeFlag = false;
    }

    void Action3(Token t) throws SymbolTableError {
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
            while (!semanticStack.empty() && (((Token) (semanticStack.peek())).getType().equals("IDENTIFIER")) && (semanticStack.peek() instanceof Token)) {
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
        gTable.dumpTable();
        arrayFlag = false;
    }

    //for "REAL" v "INT"
    void Action4(Token t) {
        semanticStack.push(t);
    }

    void Action6(Token t) {
        arrayFlag = true;
    }

    //for array member intconstants
    void Action7(Token t) {
        semanticStack.push(t);
    }

    void Action9(Token t) throws SymbolTableError {
        //pop the IDs
        //output
        Token id1 = (Token) semanticStack.pop();
        //input
        Token id2 = (Token) semanticStack.pop();
        //program name
        Token id3 = (Token) semanticStack.pop();

        System.out.println("id1: " + id1.getVal() + ", id2: " + id2.getVal() + ", id3: " + id3.getVal());
        System.out.println("id1: " + id1.getType() + ", id2: " + id2.getType() + ", id3: " + id3.getType());

        //add entry output:IODEVICE OUTPUT
        gTable.insert(id1.getVal(), new IODeviceEntry(id1.getVal()));
        //add entry input:IODEVICE INPUT
        gTable.insert(id2.getVal(), new IODeviceEntry(id2.getVal()));
        //add entry
        gTable.insert(id3.getVal(), new ProcedureEntry(id3.getVal(), 0, null));

        //dump table
        gTable.dumpTable();
        //generate stub code
        generate9("call", "main", "0");
        generate("exit");
        //insert = false
        modeFlag = false;
    }

    //for identifiers
    void Action13(Token t) {
        semanticStack.push(t);
    }

    //create more actions
    //Check to see if a variable has been declared
    void Action30(Token t) throws SemanticActionError {
        SymbolTableEntry id = lookupID(t);
        if (id == null) {
            throw SemanticActionError.ErrorMsg(1, t);
        }
        semanticStack.push(id);
    }

    //Put the value of variable ID2 in ID1 (i.e. Variable assignment.)
    void Action31(Token t) throws SemanticActionError, SymbolTableError{
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        // offset will be implemented in later actions
        SymbolTableEntry offset = null;
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
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
            } else {
                generate("stor", id2.getName(), offset.getName(), id1.getName());
            }
        }
    }

    // the token should be a sign (unary plus or minus)
    //Push unary plus/minus to stack
    void Action40(Token t){
        semanticStack.push(t);
    }

    //Apply unary plus/minus
    void Action41(Token t)throws SymbolTableError{
        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
        Token sign = (Token) semanticStack.pop();
        if (sign.getType().equals("UNARYMINUS")) {
            VariableEntry temp = creat(getTempVar(), id.getType());
            if (id.getType() == "INTEGER") {
                generate("uminus", id.getName(), temp.getName());
            } else {
                generate("fuminus", id.getName(), temp.getName());
            }
            semanticStack.push(temp);
        } else {
            semanticStack.push(id);
        }
    }

    // the token should be an operator
    //Push ADDOP operator (+, -, etc.) on to stack
    void Action42(Token t){
        semanticStack.push(t);
    }

    //Perform ADDOP based on OP popped from stack.
    void Action43(Token t)throws SymbolTableError{
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
    }

    // the token passed to the semantic actions should be an
    // operator
    // although this action is currently identical to
    // action 42, it will change in later phases
    //Push MULOP operator (*, /, etc.) on to stack.
    void Action44(Token t){
        semanticStack.push(t);
    }

    //Perform MULOP based on OP popped from stack
    void Action45(Token t) throws SymbolTableError, SemanticActionError{
        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
        Token operator = (Token) semanticStack.pop();
        String opcode = operator.getOpCode();
        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

        if (typeCheck(id1, id2) != 0 &&
                (operator.getOpCode().equals("MOD") || operator.getOpCode().equals("DIV"))){
            // MOD and DIV require integer operands
            throw SemanticActionError.ErrorMsg(3,t);
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
            }
            else if (opcode.equals("div")){
                VariableEntry temp1 = creat(getTempVar(), "REAL");
                VariableEntry temp2 = creat(getTempVar(), "REAL");
                VariableEntry temp3 = creat(getTempVar(), "REAL");
                generate("ltof", id1.getName(), temp1.getName());
                generate("ltof", id2.getName(), temp2.getName());
                generate("fdiv", temp1.getName(), temp2.getName(), temp3.getName());
                semanticStack.push(temp3);
            }
            else{
                VariableEntry temp = creat(getTempVar(), "INTEGER");
                generate(opcode, id1.getName(), id2.getName(), temp.getName());
                semanticStack.push(temp);
            }
        }
        else if (typeCheck(id1, id2) == 1) {
            VariableEntry temp = creat(getTempVar(), "REAL");
            generate("f" + opcode, id1.getName(), id2.getName(), temp.getName());
            semanticStack.push(temp);
        }
        else if ((typeCheck(id1, id2) == 2) || (typeCheck(id1, id2) == 3)){
            VariableEntry temp1 = creat(getTempVar(), "REAL");
            VariableEntry temp2 = creat(getTempVar(), "REAL");
            generate("ltof", id2.getName(), temp1.getName());
            generate("f" + opcode, id1.getName(), temp1.getName(), temp2.getName());
            semanticStack.push(temp2);
        }
    }

    //Look up value of variable or constant from SymbolTable
    void Action46(Token t)throws SemanticActionError, SymbolTableError{
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
    }

    //Array lookup.
    void Action48(Token t)throws SymbolTableError{
        // offset will be implemented in later actions
        SymbolTableEntry offset = null;
        if (offset != null) {
            SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
            VariableEntry temp = creat(getTempVar(), id.getType());
            generate("load", id.getName(), offset.getName(), temp.getName());
            semanticStack.push(temp);
        }
    }

    //Generate end-of-MAIN:: wrapper code
    void Action55(Token t) throws SymbolTableError{
        System.out.println("");
        Qs.print();
        System.out.println("");
        backpatch(globalStore, globalMemory);
        generateEnd("free", Integer.toString(globalMemory));
        generate("PROCEND");
    }

    //Generate start-of-MAIN:: wrapper code
    void Action56(Token t)throws SymbolTableError{
        generate("PROCBEGIN", "main");
        globalStore = Qs.getNextQuad();
        // the underscore as the second arguement in generate
        // is a placeholder that will be filled in later by backpatch
        generate("alloc", "_");
    }

}
