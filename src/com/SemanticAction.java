package com;

import javax.swing.*;
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
    private Stack<Object> semanticStack;
    private SymbolTable gTable;
    private SymbolTable lTable;

    public SemanticAction() throws CompilerError{
        modeFlag = true;
        envFlag = true;
        arrayFlag = false;
        globalMemory = 0;
        localMemory = 0;
        semanticStack = new Stack<>();
        gTable = new SymbolTable();
        lTable = new SymbolTable();
        initgTable();
        gTable.dumpTable();
    }

    public void initgTable()throws SymbolTableError{

        gTable.insert("MAIN", new ProcedureEntry("MAIN", 0, null));
        gTable.insert("READ", new ProcedureEntry("READ", 0, null));
        gTable.insert("WRITE", new ProcedureEntry("WRITE", 0, null));

    }

    public void initlTable(){}

    public void Execute(String action, Token t) throws  CompilerError{
        switch(action){
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
        }
    }

    void Action1(Token t){
        modeFlag = true;
    }
    void Action2(Token t){
        modeFlag = false;
    }
    void Action3(Token t)throws SymbolTableError{
        String tokenType = ((Token) semanticStack.pop()).getType();
        if(arrayFlag){
            int upperBound = ((Token)semanticStack.pop()).tokenToInt();
            int lowerBound = ((Token)semanticStack.pop()).tokenToInt();
            int memorySize = (upperBound - lowerBound) + 1;

            while(!(semanticStack.isEmpty()) && (((Token)(semanticStack.peek())).getType().equals("IDENTIFIER")) && (semanticStack.peek() instanceof Token)){
                Token tok = (Token)semanticStack.pop();
                ArrayEntry id = new ArrayEntry(tok.getVal(), 0, tokenType, upperBound, lowerBound);
                id.setType(tokenType);
                id.setUpperBound(upperBound);
                id.setLowerBound(lowerBound);

                if(envFlag){
                    id.setAddress(globalMemory);
                    gTable.insert(tok.getVal(),id);
                    globalMemory += memorySize;
                }
                else{
                    id.setAddress(localMemory);
                    lTable.insert(tok.getVal(),id);
                    localMemory += memorySize;

                }
            }
        }
        else{
            while(!semanticStack.empty() && (((Token)(semanticStack.peek())).getType().equals("IDENTIFIER")) && (semanticStack.peek() instanceof Token)){
                Token tok = (Token) semanticStack.pop();
                VariableEntry id = new VariableEntry(tok.getVal(), 0, null);
                id.setType(tokenType);

                if(envFlag){
                    id.setAddress(globalMemory);
                    //System.out.println(tok.getType() + tok.getVal() + id.getType() + id.getName());
                    gTable.insert(tok.getVal(),id);
                    globalMemory++;
                }
                else{
                    id.setAddress(localMemory);
                    lTable.insert(tok.getVal(),id);
                    localMemory++;
                }
            }
        }
        gTable.dumpTable();
        arrayFlag = false;
    }
    //for "REAL" v "INT"
    void Action4(Token t){
        semanticStack.push(t);
    }
    void Action6(Token t){
        arrayFlag = true;
    }
    //for array member intconstants
    void Action7(Token t){
        semanticStack.push(t);
    }
    void Action9(Token t)throws SymbolTableError{
        //pop the IDs
        //output
        Token id1 = (Token) semanticStack.pop();
        //input
        Token id2 = (Token) semanticStack.pop();
        //program name
        Token id3 = (Token) semanticStack.pop();

        System.out.println("id1: "+id1.getVal()+", id2: "+id2.getVal()+", id3: "+id3.getVal());
        System.out.println("id1: "+id1.getType()+", id2: "+id2.getType()+", id3: "+id3.getType());

        //add entry output:IODEVICE OUTPUT
        gTable.insert(id1.getVal(), new IODeviceEntry(id1.getVal()));
        //add entry input:IODEVICE INPUT
        gTable.insert(id2.getVal(), new IODeviceEntry(id2.getVal()));
        //add entry
        gTable.insert(id3.getVal(), new ProcedureEntry(id3.getVal(), 0, null));

        //dump table
        gTable.dumpTable();
        //insert = false
        modeFlag = false;
    }
    //for identifiers
    void Action13(Token t){
        semanticStack.push(t);
    }

}
