package com;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {
    private static Token curToken = new Token();
    private static String stackTop = "";
    private static Stack<String> parseStack = new Stack<>();
    private static Stack dumpStack = new Stack<>();
    private static String fileloc = System.getProperty("user.dir") + "/com/LanguageResources/";
    //for use in intellij *INTELLIJ*
    //private static String fileloc = System.getProperty("user.dir") + "/src/com/LanguageResources/ultcorrected.txt";

    public static void main(String[] args) throws IOException, LexicalError, ParseError{
        Lexer luthor = new Lexer();
        if(args.length == 0){
            fileloc += "ultcorrected.txt";
        }
        else{
            fileloc += args[0];
        }
        Parse(luthor);

    }


    public static void Parse(Lexer luthor) throws IOException,LexicalError,ParseError{
        //initialize debug var
        int step = 1;
        //read in language resources
        ParseTable ptable = new ParseTable();
        RHSTable rhsTable = new RHSTable();
        rhsTable.readGrammar();
        ptable.readPtable();
        //initiate lexer's cStream
        luthor.cStream = new CharStream(fileloc);
        //push eof
        parseStack.push("$");
        //push distinguished symbol
        parseStack.push("<Goal>");
        //token<-next token
        curToken = luthor.GetNextToken();
        luthor.token = curToken;
        System.out.println("Current Token: " + curToken.getType()+" WITH THE VALUE: "+curToken.getVal());
        //error checking in case null to prevent null pointer exception warning.
        if(parseStack.empty()){
            throw ParseError.ErrorMsg(1,"",curToken.getType(),Integer.toString(luthor.cStream.getLinerr()));
        }
        //X<-top of stack
        stackTop = parseStack.peek();
        while(!parseStack.peek().equals("$")){
            System.out.println(">>- "+step+" -<<");
            System.out.print("STACK::==> ");
            dumpStack();

            //checks if terminal
            if(!(stackTop.charAt(0) == '<')){
                //do something for token
                if(true){
                    System.out.println("POPPING "+parseStack.peek()+" WITH TOKEN "+curToken.getType());
                    parseStack.pop();
                    luthor.prevToken = curToken;
                    //curToken.clear();
                    curToken = luthor.GetNextToken();
                    luthor.token = curToken;
                    System.out.println("Current Token: " + curToken.getType()+" WITH THE VALUE: "+curToken.getVal());
                }
                else{
                    throw ParseError.ErrorMsg(1, stackTop, curToken.getType(),Integer.toString(luthor.cStream.getLinerr()));
                }
            }
            else{
                //-1 to handle absence of null
                int nontermkey = ptable.nonterminals.indexOf(stackTop) - 1;
                //System.out.println(nontermkey);
                int termkey = ptable.terminals.indexOf(curToken.getType());
                //System.out.println(termkey);
                //System.out.println(curToken.getType());
                //System.out.println(luthor.prevToken.getType());
                int dcode = ptable.derivationcodes.get(termkey).get(nontermkey) - 1;
                if(dcode != 998){
                    if(dcode < 0){
                        System.out.println("EPSILON");
                        parseStack.pop();
                    }
                    else{
                        System.out.println("POPPING: "+parseStack.peek());
                        parseStack.pop();
                        ArrayList<String> derivation = rhsTable.rhs.get(dcode);
                        if(derivation.get(0).equals("EPSILON")){
                            //parseStack.pop();
                        }
                        //System.out.print(derivation);
                        else{
                            for(int i = derivation.size() - 1; i >= 0; i--) {
                                //epsilon here don't push please
                                parseStack.push(derivation.get(i));
                                System.out.println("PUSHING: " + derivation.get(i));
                            }
                        }
                    }
                }
                else{
                    System.out.println("ERRONEUS TOKEN: "+curToken.getType());
                    System.out.println("ERRONEUS STACKTOP: "+stackTop);
                    //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
                    throw ParseError.ErrorMsg(2,stackTop, curToken.getType(), Integer.toString(luthor.cStream.getLinerr()));
                }
            }
            stackTop = parseStack.peek();
            step++;
        }
        if(!(parseStack.peek().equals("$"))){
            //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
            throw ParseError.ErrorMsg(3,stackTop,curToken.getType(),Integer.toString(luthor.cStream.getLinerr()));
        }

        System.out.println(">>- "+step+" -<<");
        System.out.print("STACK::==> ");
        dumpStack();
        System.out.println("POPPING "+parseStack.peek()+" WITH TOKEN "+curToken.getType());
        System.out.println("! ACCEPT !");
    }

    public static void dumpStack(){
        dumpStack = (Stack)parseStack.clone();
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


    //terminals
    // PROGRAM,BEGIN,IF,WHILE,ELSE,PROCEDURE,REAL,INTEGER,ARRAY
    //FI(A) -> first terminals of any sentential form reachable
    //

}
