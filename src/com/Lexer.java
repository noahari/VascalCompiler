package com;

import java.io.IOException;
import java.util.*;

public class Lexer {

    //had to add \r\n since my machine is windows and test files provided use crlf
    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                    ".,;:<>/*[]+-=()}{\\t " + "\r\n";
    //maximum lengths for identifiers and constants respectively
    private static final int IDMAX = 64;
    private static final int NUMMAX = 512;
    //global token and previous token for tracking current and previous tokens lexed.
    private static Token token = new Token("empty");
    private static Token prevToken = new Token("start");
    //Custom class to maintain an input stream and stack at once
    private static CharStream cStream;
    //String to act as buffer, storing read id info of current token
    private static String id = "";
    //variable to make sure whitespace/nonalphanumeric bounds identifiers
    //private static boolean whitepass;

    /*
    public static String getValidChars() {
        return VALID_CHARS;
    }
    */
    private static Token getPrevToken() {
        return prevToken;
    }

    public static void main(String[] args) throws IOException, LexicalError{
        //String fileloc = "D:/Programs/Master/VascalCompiler/src/com/lextexttest.txt";
        //Will adapt this later to require less typing
        String workingDir = System.getProperty("user.dir");
        Lexer lx = new Lexer();
        String fileloc = workingDir + "/com/" + args[0];
        //String fileloc = workingDir + "/com/" + "lextexttest.txt";
        cStream = new CharStream(fileloc);
        cStream.setWhitepass(true);
        //old approach of storing tokens in accessible data structure
        ArrayList<Token> tokens = new ArrayList<Token>(1);
        while(!(getPrevToken().getType().equals("ENDOFFILE"))){
            //must clear the buffer string
            id = "";
            cStream.setWhitepass(false);
            Token curToken = GetNextToken();
            prevToken = curToken;
            //checks if the token has anything: edge case of only comment in file
            if(!curToken.getType().equals("empty")) {
                curToken.printToken();
                //tokens.add(curToken);
            }
        }
    }


    //currently unimplemented, but will be a helper function to allow user input to specify file
    public static String openFile(){
        return "Write this";
    }

    //checks the conditions for determining Uminus and Uplus against addops
    private static Boolean unarycheck(Token t){
        return t.getType().equals("RIGHTPAREN") ||
                t.getType().equals("RIGHTBRACKET") ||
                t.getType().equals("IDENTIFIER") ||
                t.getType().equals("CONSTANT");
    }

    //helper to check if a character is valid
    private static boolean validCheck(char c){
        //System.out.println(c);
        //System.out.println(VALID_CHARS.indexOf(c));
        return !(VALID_CHARS.indexOf(c) < 0);
    }


    //input an int i that is tracked in main
    private static Token GetNextToken() throws IOException, LexicalError{
        Token loctoken = token;
        //cStream.buffer.clear();
        //token.clear();
        char cur = cStream.getChar();
        //System.out.println(cur);
        //System.out.println(Integer.toString((int)cur));


        //LOOPING BUG HERE: fixed, but remember in case of later bugs
        while (cStream.whitecheck(cur) || cur == '{') {
            cStream.passWhite(cur);
           // whitepass = true;
            cur = cStream.getChar();
        }

        //must check EOF first, otherwise else hits
        //checks if at end of file, and must have whitespace bounding
        if ((cStream.empty()) && (cur == '!')) {
            loctoken.setType("ENDOFFILE");
            return loctoken;
        }
/*
        if(!validCheck(cur)){
            System.out.println(cur);
        }
  */
        //if not valid
        if(!validCheck(cur)) {
            throw LexicalError.ErrorMsg(1, cStream.getLinerr(), cStream.getCharerr());
        }

        if (Character.isLetter(cur)) {
            loctoken = readIdentifier(cur);
        }

        else if (Character.isDigit(cur)) {
            loctoken = readNumber(cur);
        } else {
            loctoken = readSymbol(cur);
        }
        //whitepass = false;
        return loctoken;
    }

    //read identifier
    private static Token readIdentifier(char nextChar) throws IOException, LexicalError{
        //letters
        Token loctoken;
        int len = 0;

        if(((prevToken.getType() == "INTCONSTANT") || (prevToken.getType() == "REALCONSTANT")) && (!cStream.getWhitepass())){
            System.out.println(nextChar);
            throw LexicalError.ErrorMsg(5,cStream.getLinerr(),cStream.getCharerr());
        }

        //checks for lexemes relevant to token IDENTIFIER
        while(Character.isLetter(nextChar) || Character.isDigit(nextChar)){
            id += Character.toUpperCase(nextChar);
            nextChar = Character.toUpperCase(cStream.getChar());
            len++;
            if(len >= IDMAX){
                throw LexicalError.ErrorMsg(4, cStream.getLinerr(), cStream.getCharerr());
            }
        }

        //pushback if not whitespace
        if(!cStream.whitecheck(nextChar)){
            cStream.pushback(nextChar);
        }
        //KEYWORD check
        //can be cleaned up with an enumeration or hashmap, but will leave as is for now since it works,
        //I'm out of time, and I'm scared to break it until our allotted clean up time.
        switch (id) {
            case "PROGRAM":
            case "BEGIN":
            case "END":
            case "VAR":
            case "FUNCTION":
            case "PROCEDURE":
            case "RESULT":
            case "INTEGER":
            case "REAL":
            case "ARRAY":
            case "OF":
            case "NOT":
            case "IF":
            case "THEN":
            case "ELSE":
            case "WHILE":
            case "DO":
                loctoken = new Token(id);
                return loctoken;
            case "OR":
                loctoken = new Token("ADDOP", "3");
                return loctoken;

            case "DIV":
                loctoken = new Token("MULOP", "3");
                return loctoken;
            case "MOD":
                loctoken = new Token("MULOP", "4");
                return loctoken;
            case "AND":
                loctoken = new Token("MULOP", "5");
                return loctoken;

            //Returns alphabetical identifier
            default:
                loctoken = new Token("IDENTIFIER", id);
                return loctoken;
        }
    }

    //needs to be able to take the id so we can return properly,
    //and not break up the numbers before and after the scientific notation
    private static String getSci(String ide) throws IOException, LexicalError{
        //e case handling needs lookahead
        char lookahead = cStream.getChar();
        //can see +- or number
        if(Character.isDigit(lookahead)){
            //store e, since this can't be handled outside of function
            //implicit e based on when getSci is called
            ide += 'E';
            //loop until whitespace, storing looked at chars
            while(Character.isDigit(lookahead)){
                ide += lookahead;
                lookahead = cStream.getChar();
            }
            //must pushback when done with lookahead
            cStream.pushback(lookahead);
        }
        else if(lookahead == '+' || lookahead == '-'){
            //must double lookahead to see if malformed or not. e could be anything leave it to the parser
            //save old lookahead so we can store it
            char oldlook = lookahead;
            lookahead = cStream.getChar();
            if(Character.isDigit(lookahead)){
                //store the implicit e and old lookahead
                ide += 'E';
                ide += oldlook;
                //loop fo all digits after E
                while(Character.isDigit(lookahead)){
                    ide += lookahead;
                    lookahead = cStream.getChar();
                }
            }
            else{
                throw LexicalError.ErrorMsg(2, cStream.getLinerr(), cStream.getCharerr());
            }
            //edge case, hopeful bug fix

                cStream.pushback(lookahead);
                cStream.setWhitepass(true);
                //cStream.pushback(oldlook);
                //cStream.pushback('E');
        }
        //If neither of those cases, e must not be part of numbers
        else{
            //must pushback lookahead AND THE IMPLICIT E
            cStream.pushback(lookahead);
            //cStream.pushback('E');
        }
        //save value construction to the global string
        //possibly redundant with current refactoring, but will leave until it causes bugs or
        //time to optimize away
        id = ide;
        //whitepass = true;
        return ide;
    }

    //lex a numerical token
    private static Token readNumber(char nextChar) throws IOException, LexicalError {

        Token loctoken = new Token();
        char lookahead;
        int len = 0;
        //transition from q0 and loop on state 1
        while(Character.isDigit(nextChar)){
            id += nextChar;
            len++;
            if(len > NUMMAX){
                throw LexicalError.ErrorMsg(2,cStream.getLinerr(),cStream.getCharerr());
            }
            nextChar = cStream.getChar();
        }

        //transition to accept
        if(nextChar != '.' && Character.toUpperCase(nextChar) != 'E'){
            loctoken.setType("INTCONSTANT");
            loctoken.setVal(id);
            cStream.pushback(nextChar);
            return loctoken;
        }


        switch(Character.toUpperCase(nextChar)){
            //most important case first
            //transition to state 2
            case '.':
                loctoken.setType("REALCONSTANT");
                lookahead = cStream.getChar();
                //if the lookahead sees another ., then its 2 separate tokens
                //transition to real accept state
                if(lookahead == '.'){
                    //two tokens, must push back
                    cStream.pushback(nextChar);
                    cStream.pushback(lookahead);
                    loctoken.setType("INTCONSTANT");
                    loctoken.setVal(id);
                    return loctoken;

                }
                //transition to state 5
                else if(Character.isDigit(lookahead)){
                    //if one token, then push back the lookahead
                    id += nextChar;
                    len++;
                    id += lookahead;
                    len++;
                    nextChar = cStream.getChar();
                    //error handling for malformed number: 1.a
                    if(!Character.isDigit(nextChar) && !(Character.toUpperCase(nextChar) == 'E') && !(nextChar == '!') && !cStream.whitecheck(nextChar)){
                        System.out.println(nextChar);
                        throw LexicalError.ErrorMsg(2, cStream.getLinerr(), cStream.getCharerr());
                    }
                    //add the other side of the . to the id
                    while (Character.isDigit(nextChar)) {
                        id += nextChar;
                        len++;
                        if(len > NUMMAX) throw LexicalError.ErrorMsg(2, cStream.getLinerr(), cStream.getCharerr());
                        nextChar = cStream.getChar();
                    }
                    //transition to state 3
                    //must make uppercase in anticipation of possible e
                    if(Character.toUpperCase(nextChar) == 'E'){
                        //transitions through states 4,7 and REAL accept
                        id = getSci(id);
                        loctoken.setType("REALCONSTANT");
                        loctoken.setVal(id);
                        //nextChar = cStream.getChar();
                        //System.out.println(nextChar);
                    }
                    //set the value of the token to the id when assembled
                    loctoken.setVal(id);
                }
                //currently empty else, saving commented code for debugging
                // in later stages. REMOVE DURING POLISH.
                else{
                    //cStream.pushback(lookahead);
                }
                break;
            //converted in switch conditional
            case 'E':
                //could move this is=nto a helper function
                id = getSci(id);
                loctoken.setType("REALCONSTANT");
                loctoken.setVal(id);
                break;
        }
        return loctoken;

    }

    //lex a symbol into a token
    private static Token readSymbol(char nextChar) throws IOException, LexicalError {

        char lookahead;
        Token loctoken = token;
        //comma
        if (nextChar == ',') {
            loctoken = new Token("COMMA");
            return loctoken;
        }
        //semicolon
        if (nextChar == ';') {
            loctoken = new Token("SEMICOLON");
            return loctoken;
        }
        //colon
        if (nextChar == ':') {
            //need lookahead for assignop
            lookahead = cStream.getChar();
            if (lookahead == '=') {
                loctoken = new Token("ASSIGNOP");
                return loctoken;
            } else {
                //must pushback if lookahead fail
                cStream.pushback(lookahead);
                //possibly move this before lookahead check
                loctoken = new Token("COLON");
                return loctoken;
            }
        }
        //LEFTPAREN
        if (nextChar == '(') {
            loctoken = new Token("LEFTPAREN");
            return loctoken;
        }
        //RIGHTPAREN
        if (nextChar == ')') {
            loctoken = new Token("RIGHTPAREN");
            return loctoken;
        }
        //LEFTBRACKET
        if (nextChar == '[') {
            loctoken = new Token("LEFTBRACKET");
            return loctoken;
        }
        //RIGHTBRACKET
        if (nextChar == ']') {
            loctoken = new Token("RIGHTBRACKET");
            return loctoken;
        }

        //RELOP
        if (nextChar == '=') {
            loctoken = new Token("RELOP", "1");
            return loctoken;
        }
        if (nextChar == '<') {
            lookahead = cStream.getChar();
            if (lookahead == '>') {
                loctoken = new Token("RELOP", "2");
                return loctoken;
            }
            else if (lookahead == '=') {
                loctoken = new Token("RELOP", "5");
                return loctoken;
            }
            else{
                //necessary pushback
                cStream.pushback(lookahead);
                //see above in assign op, may move this block
                loctoken = new Token("RELOP", "3");
                return loctoken;
            }
        }
        if (nextChar == '>') {
                lookahead = cStream.getChar();
            if (lookahead == '=') {
                loctoken = new Token("RELOP", "6");
                return loctoken;
            } else {
                cStream.pushback(lookahead);
                //assignop issue
                loctoken = new Token("RELOP", "4");
                return loctoken;
            }
        }

        if(nextChar == '.'){
            //need lookahead for ..
            lookahead = cStream.getChar();
            if(lookahead == '.'){
                loctoken.setType("DOUBLEDOT");
                //cStream.pushback(cStream.getChar());
            }
            else{
                loctoken.setType("ENDMARKER");
                cStream.pushback(lookahead);
            }
        }


        //MULOP
        if (nextChar == '*') {
            loctoken = new Token("MULOP", "1");
            return loctoken;
        } else if (nextChar == '/') {
            loctoken = new Token("MULOP", "2");
            return loctoken;
        }

        //ADDOP
        if (nextChar == '+') {
            if (unarycheck(prevToken)) {
                loctoken = new Token("ADDOP", "1");
                return loctoken;
            }
            //Unaryplus
            else {
                loctoken = new Token("UNARYPLUS");
                return loctoken;
            }
        }

        if (nextChar == '-') {
            if (unarycheck(prevToken)) {
                loctoken = new Token("ADDOP", "2");
                return loctoken;
            }

            //Unaryplus
            else {
                loctoken = new Token("UNARYMINUS");
                return loctoken;
            }
        }
        else{
            return loctoken;
            //add error handling
        }
    }
}