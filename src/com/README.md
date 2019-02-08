#VascalCompiler

##Version 0.1: Lexer Implemented

This file can be run just by executing the main method in Lexer.java. It directly references
a file path hardcoded to be the test file for now.

###Changes:
>Implemented Lexer with handling for Keywords, Identifiers, Numbers, and Symbols.

>Checks for valid characters; 
 valid construction of comments, constants, and identifiers; 
 ensures whitespace surrounding identifiers;
 checks if constant or identifier exceeds max allowed length; 
 and throws custom errors if any of these conditions are not met
 
 >Properly handles '..' and scientific notation edge cases, as well as malformed constants
 where each are interrupted with an unexpected character (e.g. 5. , 5.3e. , 5.3ea, 5.a)