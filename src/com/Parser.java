package com;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {
    private static Token curToken = new Token();
    private static String stackTop = "";
    private static Stack<String> parseStack = new Stack<>();
    private static Stack dumpStack = new Stack<>();
    private static String fileloc;
    //for use in intellij *INTELLIJ*
    //private static String fileloc = System.getProperty("user.dir") + "/src/com/LanguageResources/";

    private static boolean runningFromIntelliJ(){
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }

    public static void main(String[] args) throws IOException,CompilerError{
        Lexer luthor = new Lexer();
        Parser peter = new Parser();
        if(runningFromIntelliJ()){
            fileloc = System.getProperty("user.dir") + "/src/com/LanguageResources/";
        }
        else{
            fileloc = System.getProperty("user.dir") + "/com/LanguageResources/";
        }
        if(args.length == 0){
            fileloc += "phase2-6_ns.txt";
        }
        else{
            fileloc += args[0];
        }
        peter.Parse(luthor);

    }

    public void Parse(Lexer luthor) throws IOException,CompilerError{
        //initialize debug var
        int step = 1;
        //read in language resources
        ParseTable ptable = new ParseTable();
        RHSTable rhsTable = new RHSTable();
        rhsTable.readGrammar();
        ptable.readPtable();
        SemanticAction sa = new SemanticAction();
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

            //checks if semantic action
            if(stackTop.charAt(0) == '#'){
                System.out.println("TAKING SEMANTIC ACTION " + stackTop);
                System.out.println(luthor.prevToken.getType() + ", " + luthor.prevToken.getVal());
                sa.Execute(stackTop, luthor.prevToken);
                parseStack.pop();
            }
            //checks if terminal
            else if(!(stackTop.charAt(0) == '<')){
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
            //if not terminal or semantic then must be nonterminal
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
        writeTVI(sa);
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

    static void writeTVI(SemanticAction s){
        s.getQs().print();
        //currently unimplemented, but this can be a helper function
        //to pipe just the generated TVI code to an output file
    }


    //terminals
    // PROGRAM,BEGIN,IF,WHILE,ELSE,PROCEDURE,REAL,INTEGER,ARRAY
    //FI(A) -> first terminals of any sentential form reachable
    //

}
