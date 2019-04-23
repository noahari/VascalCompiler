# Vascal Compiler

##Version 1.0
This version now does not print stack traces and other backend information 
so that the output TVI code may be directly piped into a file for ease of
running a compiled program. This TVI code is built to run on the Vassar Interpreter.

To run this file:

- Pull the files from github maintaining the directory structure

- cd into src/com and javac *.java

- cd back out to src and run com.Parser

- The default test file is now ult.txt (ultimate.vas). 
Like previous versions, all you need to do to add test files 
is add them to the directory src/com/LanguageResources and type their name in as 
arguments to running com.Parser 
(e.g. "java com.Parser simple.txt" would Parse a file called simple.txt in the LanguageResources folder).

- This can now be run in intelliJ without changing code pointing to paths.

*NOTE 1: TVI code may differ from other compilations due to implementation of modulus. 
The resulting TVI code is still equally effective. The additional lines are equivalent moves,
and any differences in register access and outp/print follow those differences. This causes
an increase in memory allocation and extraneous moves, so assuming there is time this may 
be optimized out in future versions.*

*NOTE 2: Sometimes when moving between OS or systems, critical spaces in the grammar get optimized away.
The compiler demands epsilon be represented as exactly ":=  ", so simply adding a special character and editing the
RHS table may fix this. Causes an array out of bounds exception at start of execution*

## Changelog:

###Version 1.0
>Implemented additional versions of generate to fix errors when we can just pass hard values

>Implemented getSTEPrefix, getParamPrefix, and getSTEAddress

>Added tracking for local memory as localstore, as well as tracking for parameters in procs and funcs

>Implemented Actions 5, 11, 15, 16, 17, 19, 20, 21, 35, 36, 37, 49, 50, 51, 51WRITE, 51READ, 52, 54

>Revised Action 53

### Version 0.75
>Implemented EType to track whether the expression being evaluated is arithmetic or relational

>Implemented merge, makeList, and an Overloaded new version of backpatch

>Implemented Semantic Actions 22, 24, 25, 26, 27, 28, 29, 32, 33, 34, 38, 39, 47, 53, 54

>Revised Semantic Actions 30, 31, 41, 42, 43, 44, 45, 46, 48

>Past Bug Fixes: 
* >Generate function, op4f beng reserved was setting op3f = op3 instead of op4 = op4


### Version 0.6
>Implemented Quadruple & Quadruples data structures to hold generated TVI stub code

>Implemented functionality to generate TVI stub code corresponding to parsed Vascal Code

>Added a constant symbol table and a global store to track allocs

>Implemented Table Lookup, Backpatching, and Type Checking

>Implemented Semantic Actions 31, 31, 40, 41, 42, 43, 44, 45, 46, 48, 55 and 56.

>Past Bug Fixes:
* >Action 9 now generates stub code

>Possible Issues:
* >On testfile 2-6_ns, it produces the same code as listed in the 2-6_ns.tvi file
but that is different from the stub code provided in the .vas file.
* >Current error catching for SemanticActionError and SymbolTableError is a little terse
and not always as helpful as it should be.

### Version 0.3
>Implemented Symbol Table and Semantic Action frameworks with error catching for both.

>Implemented Semantic Actions 1, 2, 3, 4, 6, 7, 9, and 13.

>Past Bug Fixes:
* >Now runs in intelliJ or command line environments 
without changing lines of code or recompiling

### Version 0.2.5
>Implemented Parser in an abstracted, generalized method that can read an input Grammar
and Parse Table, and use it to properly parse an input file. Since it reads these from a file,
the Parser can simply be directed to read files that construct a different language and then 
serve as a working parser for that language.

>Error handling that tracks down the line of the error and reports what kind of error,
 i.e Unexpected SEMICOLON following <identifier-list> at line 36. Gives line of error,
 as well as read Token that caused the error and the top of the parse stack at the moment
 of error.


>Lexer bug fixes: 
* >Caught edge case where some real constant tokens where the character
after that token would be consumed and ignored. 
* >Fixed overly static Lexer, and used dynamic
references to fix a Parsing bug where progressive lexing produced error in the Unary vs Addop
distinction.

### Version 0.1
>Implemented Lexer with handling for Keywords, Identifiers, Numbers, and Symbols.

>Checks for valid characters; 
 valid construction of comments, constants, and identifiers; 
 ensures whitespace surrounding identifiers;
 checks if constant or identifier exceeds max allowed length; 
 and throws custom errors if any of these conditions are not met
 
 >Properly handles '..' and scientific notation edge cases, as well as malformed constants
 where each are interrupted with an unexpected character (e.g. 5. , 5.3e. , 5.3ea, 5.a)
 
 >Currently, this is too static. I ran out of time to rework this but I am aware and
 this will be improved upon in subsequent versions.
 
 
## Historical Version Notes

##Version 0.75
To run this file:

- Pull the files from github maintaining the directory structure

- cd into src/com and javac *.java

- cd back out to src and run com.Parser

-  The default test file is now phase3-1.txt (phase3-1.vas) since Array actions are not yet implemented, and now simple.txt
 has too many unimplemented actions that would cause errors. 
 Like previous versions, all you need to do to add test files 
is add them to the directory src/com/LanguageResources and type their name in as 
arguments to running com.Parser 
(e.g. "java com.Parser simple.txt" would Parse a file called simple.txt in the LanguageResources folder).

- This can now be run in intelliJ without changing code pointing to paths.

*NOTE: Sometimes when moving between OS or systems, critical spaces in the grammar get optimized away.
The compiler demands epsilon be represented as exactly ":=  ", so simply adding a special character and editing the
RHS table may fix this. Causes an array out of bounds exception at start of execution*

##Version 0.6: Quadruple implementation for TVI generation
To run this file:

- Pull the files from github maintaining the directory structure

- cd into src/com and javac *.java

- cd back out to src and run com.Parser

-  The default test file is now phase2-6.txt (phase2-6_ns.vas) since Array actions are not yet implemented, and now simple.txt
 has too many unimplemented actions that would cause errors. 
 Like previous versions, all you need to do to add test files 
is add them to the directory src/com/LanguageResources and type their name in as 
arguments to running com.Parser 
(e.g. "java com.Parser simple.txt" would Parse a file called simple.txt in the LanguageResources folder).

- This can now be run in intelliJ without changing code pointing to paths.

*NOTE: Sometimes when moving between OS or systems, critical spaces in the grammar get optimized away.
The compiler demands epsilon be represted as exactly ":=  ", so simply adding a special character and editing the
RHS table may fix this. Causes an array out of bounds exception at start of execution*

 
##Version 0.3: Initial Semantic Implementation
To run this file:

- Pull the files from github maintaining the directory structure

- cd into src/com and javac *.java

- cd back out to src and run com.Parser

-  The default test file is now simple.txt, since ultcorrected.txt involves 
too many unimplemented Semantic Actions. All you need to do to add test files 
is add them to the directory src/com/LanguageResources and type their name in as 
arguments to running com.Parser 
(e.g. "java com.Parser simple.txt" would Parse a file called simple.txt in the LanguageResources folder).

- This can now be run in intelliJ without changing code pointing to paths.

*NOTE: Sometimes when moving between OS or systems, critical spaces in the grammar get optimized away.
The compiler demands epsilon be represted as exactly ":=  ", so simply adding a special character and editing the
RHS table may fix this. Causes an array out of bounds exception at start of execution*
 

## Version 0.25: Parser Implemented with Lexer fixes

To run this file:

- Pull the files from github maintaining the directory structure

- cd into src/com and javac *.java

- cd back out to src and run com.Parser

-  The default test file is ultcorrected.txt. All you need to do to add test files is add them to the 
directory src/com/LanguageResources and type their name in as arguments to running com.Parser 
(e.g. "java com.Parser simple.txt" would Parse a file called simple.txt in the LanguageResources folder).

*Note: Due to path differences, if it is preferable to run in intellij as opposed to the
command line, the correct file paths are noted below comments containing the searchable phrase "" \*INTELLIJ\* "*

*In order to run in intellij, comment out the line above that demarcation, and de-comment the line
below that demarcation in Parser.java, RHSTable.java, and ParseTable.java. Then simply execute the
main method in Parser.java*
 
 ### Version 0.1: Lexer Implemented
 
 To run this file: 
 
 -Place all .java files and txt test files into a directory called "com"
 
 - Compile it
 
 - Move the working directory to the directory above the com directory
  
 - Ensure test files are in the same directory as the compiled classes 
  
 - Run Lexer.class with an argument giving it the name of the test file WITH file extension.
 
 -For example use command line argument: "java com.Lexer lextexttest.txt"
 
 
 The currently coded test file is a test file generated by a tool made by Jacob Schwartz. I included a handwritten testfile if it is not
 acceptable to use a randomly generated test file if that test file was generated by a tool I
 did not create.
 
 Here are the two arguments for the test files:
 
 ./lextexttest.txt

 
 ## Acknowledgements
 Parker: Thanks for being a very helpful coach during your hours 
 
 Jacob Schwartz: Thanks for generating a testfile 
 that is more expansive than one I could write by hand.