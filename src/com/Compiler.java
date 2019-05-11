package com;

import java.io.IOException;

//Wrapper class to hold all components of the compiler and compile
class Compiler
{
    //Compiles a file based on the passed args
    static void Compile(String[] args) throws IOException, CompilerError
    {
        Lexer luthor = new Lexer();
        Parser peter = new Parser();
        if (peter.RunningFromIntelliJ())
        {
            peter.fileloc = System.getProperty("user.dir") + "/src/com/LanguageResources/";
        } else
        {
            peter.fileloc = System.getProperty("user.dir") + "/com/LanguageResources/";
        }
        if (args.length == 0)
        {
            peter.fileloc += "ult.txt";
            peter.Parse(luthor);
        } else
        {
            if (!(args[0].equals("-t")))
            {
                peter.fileloc += args[0];
            }
            //sloppy implementation, but for now this is the only flag and it will not
            // be difficult to generalize flag reading later
            if (args[args.length - 1].equals("-t"))
            {
                if (args.length == 1)
                {
                    peter.fileloc += "ult.txt";
                }
                peter.ParseTrace(luthor);
            } else
            {
                peter.Parse(luthor);
            }
        }

    }
}
