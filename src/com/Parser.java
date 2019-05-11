package com;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;


public class Parser
{
    private static Token curToken = new Token();
    private static String stackTop = "";
    private static Stack<String> parseStack = new Stack<>();
    private static Stack dumpStack = new Stack<>();
    String fileloc;
    //for use in intellij *INTELLIJ*
    //private static String fileloc = System.getProperty("user.dir") + "/src/com/LanguageResources/";

    //checks the cmpiler runtime environment for path issues
    boolean RunningFromIntelliJ()
    {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }

    //Parses a file as passed in by args
    void Parse(Lexer luthor) throws IOException, CompilerError
    {
        //initialize debug var
        int step = 1;
        //read in language resources
        ParseTable ptable = new ParseTable();
        RHSTable rhsTable = new RHSTable();
        rhsTable.ReadGrammar();
        ptable.ReadPtable();
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
        //System.out.println("Current Token: " + curToken.GetType()+" WITH THE VALUE: "+curToken.GetVal());
        //error checking in case null to prevent null pointer exception warning.
        if (parseStack.empty())
        {
            throw ParseError.ErrorMsg(1, "", curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
        }
        //X<-top of stack
        stackTop = parseStack.peek();
        while (!parseStack.peek().equals("$"))
        {
            //System.out.println(">>- "+step+" -<<");
            //System.out.Print("STACK::==> ");
            //dumpStack();

            //checks if semantic action
            if (stackTop.charAt(0) == '#')
            {
                //System.out.println("TAKING SEMANTIC ACTION " + stackTop);
                //System.out.println(luthor.prevToken.GetType() + ", " + luthor.prevToken.GetVal());
                sa.Execute(stackTop, luthor.prevToken);
                //System.out.println(sa.GetSemanticStack());
                parseStack.pop();
            }
            //checks if terminal
            else if (!(stackTop.charAt(0) == '<'))
            {
                //do something for token
                if (true)
                {
                    //System.out.println("POPPING "+parseStack.peek()+" WITH TOKEN "+curToken.GetType());
                    parseStack.pop();
                    luthor.prevToken = curToken;
                    //curToken.clear();
                    curToken = luthor.GetNextToken();
                    luthor.token = curToken;
                    //System.out.println("Current Token: " + curToken.GetType()+" WITH THE VALUE: "+curToken.GetVal());
                } else
                {
                    throw ParseError.ErrorMsg(1, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
                }
            }
            //if not terminal or semantic then must be nonterminal
            else
            {
                //-1 to handle absence of null
                int nontermkey = ptable.nonterminals.indexOf(stackTop) - 1;
                //System.out.println(nontermkey);
                int termkey = ptable.terminals.indexOf(curToken.GetType());
                //System.out.println(termkey);
                //System.out.println(curToken.GetType());
                //System.out.println(luthor.prevToken.GetType());
                int dcode = ptable.derivationcodes.get(termkey).get(nontermkey) - 1;
                if (dcode != 998)
                {
                    if (dcode < 0)
                    {
                        //System.out.println("EPSILON");
                        parseStack.pop();
                    } else
                    {
                        //System.out.println("POPPING: "+parseStack.peek());
                        parseStack.pop();
                        ArrayList<String> derivation = rhsTable.rhs.get(dcode);
                        if (derivation.get(0).equals("EPSILON"))
                        {
                            //parseStack.pop();
                        }
                        //System.out.Print(derivation);
                        else
                        {
                            for (int i = derivation.size() - 1; i >= 0; i--)
                            {
                                //epsilon here don't push please
                                parseStack.push(derivation.get(i));
                                //System.out.println("PUSHING: " + derivation.get(i));
                            }
                        }
                    }
                } else
                {
                    System.out.println("ERRONEUS TOKEN: " + curToken.GetType());
                    System.out.println("ERRONEUS STACKTOP: " + stackTop);
                    //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
                    throw ParseError.ErrorMsg(2, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
                }
            }
            stackTop = parseStack.peek();
            step++;
        }
        if (!(parseStack.peek().equals("$")))
        {
            //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
            throw ParseError.ErrorMsg(3, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
        }

        //System.out.println(">>- "+step+" -<<");
        //System.out.Print("STACK::==> ");
        //dumpStack();
        //System.out.println("POPPING "+parseStack.peek()+" WITH TOKEN "+curToken.GetType());
        //System.out.println("! ACCEPT !");
        WriteTVI(sa);
    }

    //prints out the parse stack
    private static void dumpStack()
    {
        dumpStack = (Stack) parseStack.clone();
        System.out.print("[");
        while (!dumpStack.empty())
        {
            System.out.print(dumpStack.peek());
            dumpStack.pop();
            if (!dumpStack.empty())
            {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    //writes out the current TVI code
    private static void WriteTVI(SemanticAction s)
    {
        s.GetQs().Print();
        //currently unimplemented, but this can be a helper function
        //to pipe just the generated TVI code to an output file
    }

    //Parses a file while printing out stack trace and debug information
    void ParseTrace(Lexer luthor) throws IOException, CompilerError
    {
        //initialize debug var
        int step = 1;
        //read in language resources
        ParseTable ptable = new ParseTable();
        RHSTable rhsTable = new RHSTable();
        rhsTable.ReadGrammar();
        ptable.ReadPtable();
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
        System.out.println("Current Token: " + curToken.GetType() + " WITH THE VALUE: " + curToken.GetVal());
        //error checking in case null to prevent null pointer exception warning.
        if (parseStack.empty())
        {
            throw ParseError.ErrorMsg(1, "", curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
        }
        //X<-top of stack
        stackTop = parseStack.peek();
        while (!parseStack.peek().equals("$"))
        {
            System.out.println(">>- " + step + " -<<");
            System.out.print("STACK::==> ");
            dumpStack();

            //checks if semantic action
            if (stackTop.charAt(0) == '#')
            {
                System.out.println("TAKING SEMANTIC ACTION " + stackTop);
                System.out.println(luthor.prevToken.GetType() + ", " + luthor.prevToken.GetVal());
                sa.Execute(stackTop, luthor.prevToken);
                System.out.println(sa.GetSemanticStack());
                parseStack.pop();
            }
            //checks if terminal
            else if (!(stackTop.charAt(0) == '<'))
            {
                //do something for token
                if (true)
                {
                    System.out.println("POPPING " + parseStack.peek() + " WITH TOKEN " + curToken.GetType());
                    parseStack.pop();
                    luthor.prevToken = curToken;
                    //curToken.clear();
                    curToken = luthor.GetNextToken();
                    luthor.token = curToken;
                    System.out.println("Current Token: " + curToken.GetType() + " WITH THE VALUE: " + curToken.GetVal());
                } else
                {
                    throw ParseError.ErrorMsg(1, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
                }
            }
            //if not terminal or semantic then must be nonterminal
            else
            {
                //-1 to handle absence of null
                int nontermkey = ptable.nonterminals.indexOf(stackTop) - 1;
                //System.out.println(nontermkey);
                int termkey = ptable.terminals.indexOf(curToken.GetType());
                //System.out.println(termkey);
                //System.out.println(curToken.GetType());
                //System.out.println(luthor.prevToken.GetType());
                int dcode = ptable.derivationcodes.get(termkey).get(nontermkey) - 1;
                if (dcode != 998)
                {
                    if (dcode < 0)
                    {
                        System.out.println("EPSILON");
                        parseStack.pop();
                    } else
                    {
                        System.out.println("POPPING: " + parseStack.peek());
                        parseStack.pop();
                        ArrayList<String> derivation = rhsTable.rhs.get(dcode);
                        if (derivation.get(0).equals("EPSILON"))
                        {
                            //parseStack.pop();
                        }
                        //System.out.Print(derivation);
                        else
                        {
                            for (int i = derivation.size() - 1; i >= 0; i--)
                            {
                                //epsilon here don't push please
                                parseStack.push(derivation.get(i));
                                System.out.println("PUSHING: " + derivation.get(i));
                            }
                        }
                    }
                } else
                {
                    System.out.println("ERRONEUS TOKEN: " + curToken.GetType());
                    System.out.println("ERRONEUS STACKTOP: " + stackTop);
                    //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
                    throw ParseError.ErrorMsg(2, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
                }
            }
            stackTop = parseStack.peek();
            step++;
        }
        if (!(parseStack.peek().equals("$")))
        {
            //can ignore the deref issue since a Lexer call would happen first, meaning the Lexical error would handle that case
            throw ParseError.ErrorMsg(3, stackTop, curToken.GetType(), Integer.toString(luthor.cStream.GetLinerr()));
        }

        System.out.println(">>- " + step + " -<<");
        System.out.print("STACK::==> ");
        dumpStack();
        System.out.println("POPPING " + parseStack.peek() + " WITH TOKEN " + curToken.GetType());
        System.out.println("! ACCEPT !");
        WriteTVI(sa);
    }


    //terminals
    // PROGRAM,BEGIN,IF,WHILE,ELSE,PROCEDURE,REAL,INTEGER,ARRAY
    //FI(A) -> first terminals of any sentential form reachable
    //

}
