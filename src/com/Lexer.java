package com;

import java.io.IOException;

//This class implements the lexer, it tokenizes input characters from a CharStream
public class Lexer
{

    //had to add \r\n since my machine is windows and test files provided use crlf
    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                    ".,;:<>/*[]+-=()}{\\t " + "\r\n";
    //maximum lengths for identifiers and constants respectively
    private static final int IDMAX = 64;
    private static final int NUMMAX = 512;
    //global token and previous token for tracking current and previous tokens lexed.
    Token token;
    Token prevToken;
    //Custom class to maintain an input stream and stack at once
    CharStream cStream;
    //String to act as buffer, storing read id info of current token
    private static String id = "";
    //variable to make sure whitespace/nonalphanumeric bounds identifiers
    //private static boolean whitepass;

    /*
    public static String getValidChars() {
        return VALID_CHARS;
    }
    */
    public Lexer()
    {
        token = new Token("empty");
        prevToken = new Token("start");
    }

    private Token GetPrevToken()
    {
        return prevToken;
    }

    private void SetCurToken(Token t)
    {
        token = t;
    }

    private static Token GetCurToken(Token t)
    {
        return t;
    }

    public static void main(String[] args) throws IOException, LexicalError
    {
        //String fileloc = "D:/Programs/Master/VascalCompiler/src/com/lextexttest.txt";
        //Will adapt this later to require less typing
        String workingDir = System.getProperty("user.dir");
        Lexer lx = new Lexer();
        //String fileloc = workingDir + "/com/" + args[0];
        String fileloc = workingDir + "/src/com/LanguageResources/" + "ultcorrected.txt";
        lx.cStream = new CharStream(fileloc);
        lx.cStream.SetWhitepass(true);
        //old approach of storing tokens in accessible data structure
        //ArrayList<Token> tokens = new ArrayList<Token>(1);
        while (!(lx.GetPrevToken().GetType().equals("ENDOFFILE")))
        {
            //must clear the buffer string
            id = "";
            lx.cStream.SetWhitepass(false);
            Token curToken = lx.GetNextToken();
            lx.prevToken = curToken;
            //checks if the token has anything: edge case of only comment in file
            if (!curToken.GetType().equals("empty"))
            {
                curToken.PrintToken();
                //tokens.add(curToken);
            }
        }
    }

    //currently unimplemented, but will be a helper function to allow user input to specify file
    public static String OpenFile()
    {
        return "Write this";
    }

    //checks the conditions for determining Uminus and Uplus against addops
    private static Boolean unarycheck(Token t)
    {
        return t.GetType().equals("RIGHTPAREN") || t.GetType().equals("RPAREN") ||
                t.GetType().equals("RIGHTBRACKET") || t.GetType().equals("RBRACKET") ||
                t.GetType().equals("IDENTIFIER") ||
                t.GetType().equals("CONSTANT");
    }

    //helper to check if a character is valid
    private boolean ValidCheck(char c)
    {
        //System.out.println(c);
        //System.out.println(VALID_CHARS.indexOf(c));
        return !(VALID_CHARS.indexOf(c) < 0);
    }


    //input an int i that is tracked in main
    Token GetNextToken() throws IOException, LexicalError
    {
        Token loctoken = this.token;
        this.prevToken = this.token;
        //cStream.buffer.clear();
        //token.clear();
        char cur = cStream.GetChar();
        //System.out.println(cur);
        //System.out.println(Integer.toString((int)cur));


        //LOOPING BUG HERE: fixed, but remember in case of later bugs
        while (cStream.WhiteCheck(cur) || cur == '{')
        {
            cStream.PassWhite(cur);
            // whitepass = true;
            cur = cStream.GetChar();
        }

        //must check EOF first, otherwise else hits
        //checks if at end of file, and must have whitespace bounding
        if ((cStream.Empty()) && (cur == '!'))
        {
            loctoken.SetType("ENDOFFILE");
            return loctoken;
        }
/*
        if(!ValidCheck(cur)){
            System.out.println(cur);
        }
  */
        //if not valid
        if (!ValidCheck(cur))
        {
            throw LexicalError.ErrorMsg(1, cStream.GetLinerr(), cStream.GetCharerr());
        }

        if (Character.isLetter(cur))
        {
            loctoken = ReadIdentifier(cur);
        } else if (Character.isDigit(cur))
        {
            loctoken = ReadNumber(cur);
        } else
        {
            loctoken = ReadSymbol(cur);
        }
        //whitepass = false;
        return loctoken;
    }

    //reads an identifier from the stream
    private Token ReadIdentifier(char nextChar) throws IOException, LexicalError
    {
        //letters
        Token loctoken;
        int len = 0;
        id = "";
        if (((this.prevToken.GetType().equals("INTCONSTANT")) || (this.prevToken.GetType().equals("REALCONSTANT"))) && (!cStream.GetWhitepass()))
        {
            System.out.println(nextChar);
            throw LexicalError.ErrorMsg(5, cStream.GetLinerr(), cStream.GetCharerr());
        }

        //checks for lexemes relevant to token IDENTIFIER
        while (Character.isLetter(nextChar) || Character.isDigit(nextChar))
        {
            id += Character.toUpperCase(nextChar);
            nextChar = Character.toUpperCase(cStream.GetChar());
            len++;
            if (len >= IDMAX)
            {
                throw LexicalError.ErrorMsg(4, cStream.GetLinerr(), cStream.GetCharerr());
            }
        }

        //Pushback if not whitespace
        if (!cStream.WhiteCheck(nextChar))
        {
            cStream.Pushback(nextChar);
        }
        //KEYWORD check
        //can be cleaned up with an enumeration or hashmap, but will leave as is for now since it works,
        //I'm out of time, and I'm scared to break it until our allotted clean up time.
        switch (id.toUpperCase())
        {
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
    private String GetSci(String ide) throws IOException, LexicalError
    {
        //e case handling needs lookahead
        char lookahead = cStream.GetChar();
        //can see +- or number
        if (Character.isDigit(lookahead))
        {
            //store e, since this can't be handled outside of function
            //implicit e based on when GetSci is called
            ide += 'E';
            //loop until whitespace, storing looked at chars
            while (Character.isDigit(lookahead))
            {
                ide += lookahead;
                lookahead = cStream.GetChar();
            }
            //must Pushback when done with lookahead
            cStream.Pushback(lookahead);
        } else if (lookahead == '+' || lookahead == '-')
        {
            //must double lookahead to see if malformed or not. e could be anything leave it to the parser
            //save old lookahead so we can store it
            char oldlook = lookahead;
            lookahead = cStream.GetChar();
            if (Character.isDigit(lookahead))
            {
                //store the implicit e and old lookahead
                ide += 'E';
                ide += oldlook;
                //loop fo all digits after E
                while (Character.isDigit(lookahead))
                {
                    ide += lookahead;
                    lookahead = cStream.GetChar();
                }
            } else
            {
                throw LexicalError.ErrorMsg(2, cStream.GetLinerr(), cStream.GetCharerr());
            }
            //edge case, hopeful bug fix

            cStream.Pushback(lookahead);
            cStream.SetWhitepass(true);
            //cStream.Pushback(oldlook);
            //cStream.Pushback('E');
        }
        //If neither of those cases, e must not be part of numbers
        else
        {
            //must Pushback lookahead AND THE IMPLICIT E
            cStream.Pushback(lookahead);
            //cStream.Pushback('E');
        }
        //save value construction to the global string
        //possibly redundant with current refactoring, but will leave until it causes bugs or
        //time to optimize away
        id = ide;
        //whitepass = true;
        return ide;
    }

    //lex a numerical token
    private Token ReadNumber(char nextChar) throws IOException, LexicalError
    {

        Token loctoken = new Token();
        char lookahead;
        int len = 0;
        id = "";
        //transition from q0 and loop on state 1
        while (Character.isDigit(nextChar))
        {
            id += nextChar;
            len++;
            if (len > NUMMAX)
            {
                throw LexicalError.ErrorMsg(2, cStream.GetLinerr(), cStream.GetCharerr());
            }
            nextChar = cStream.GetChar();
        }

        //transition to accept
        if (nextChar != '.' && Character.toUpperCase(nextChar) != 'E')
        {
            loctoken.SetType("INTCONSTANT");
            loctoken.SetVal(id);
            cStream.Pushback(nextChar);
            return loctoken;
        }


        switch (Character.toUpperCase(nextChar))
        {
            //most important case first
            //transition to state 2
            case '.':
                loctoken.SetType("REALCONSTANT");
                lookahead = cStream.GetChar();
                //if the lookahead sees another ., then its 2 separate tokens
                //transition to real accept state
                if (lookahead == '.')
                {
                    //two tokens, must push back
                    cStream.Pushback(nextChar);
                    cStream.Pushback(lookahead);
                    loctoken.SetType("INTCONSTANT");
                    loctoken.SetVal(id);
                    return loctoken;

                }
                //transition to state 5
                else if (Character.isDigit(lookahead))
                {
                    //if one token, then push back the lookahead
                    id += nextChar;
                    len++;
                    id += lookahead;
                    len++;
                    nextChar = cStream.GetChar();
                    //error handling for malformed number: 1.a
                    if (!Character.isDigit(nextChar) && !(Character.toUpperCase(nextChar) == 'E') && !(nextChar == '!') && !cStream.WhiteCheck(nextChar))
                    {
                        System.out.println(nextChar);
                        throw LexicalError.ErrorMsg(2, cStream.GetLinerr(), cStream.GetCharerr());
                    }
                    //add the other side of the . to the id
                    while (Character.isDigit(nextChar))
                    {
                        id += nextChar;
                        len++;
                        if (len > NUMMAX) throw LexicalError.ErrorMsg(2, cStream.GetLinerr(), cStream.GetCharerr());
                        nextChar = cStream.GetChar();
                    }
                    //transition to state 3
                    //must make uppercase in anticipation of possible e
                    if (Character.toUpperCase(nextChar) == 'E')
                    {
                        //transitions through states 4,7 and REAL accept
                        id = GetSci(id);
                        loctoken.SetType("REALCONSTANT");
                        loctoken.SetVal(id);
                        //nextChar = cStream.GetChar();
                        //System.out.println(nextChar);
                    }
                    //ADDDED TO FIX THE BUG in PARSETOWN USA
                    cStream.Pushback(nextChar);
                    //set the value of the token to the id when assembled
                    loctoken.SetVal(id);
                }
                //currently Empty else, saving commented code for debugging
                // in later stages. REMOVE DURING POLISH.
                else
                {
                    cStream.Pushback(lookahead);
                }
                break;
            //converted in switch conditional
            case 'E':
                //could move this is=nto a helper function
                id = GetSci(id);
                loctoken.SetType("REALCONSTANT");
                loctoken.SetVal(id);
                break;
        }
        return loctoken;

    }

    //lex a symbol into a token
    private Token ReadSymbol(char nextChar) throws IOException, LexicalError
    {

        char lookahead;
        Token loctoken = this.token;
        //comma
        if (nextChar == ',')
        {
            loctoken = new Token("COMMA");
            return loctoken;
        }
        //semicolon
        if (nextChar == ';')
        {
            loctoken = new Token("SEMICOLON");
            return loctoken;
        }
        //colon
        if (nextChar == ':')
        {
            //need lookahead for assignop
            lookahead = cStream.GetChar();
            if (lookahead == '=')
            {
                loctoken = new Token("ASSIGNOP");
                return loctoken;
            } else
            {
                //must Pushback if lookahead fail
                cStream.Pushback(lookahead);
                //possibly move this before lookahead check
                loctoken = new Token("COLON");
                return loctoken;
            }
        }
        //LEFTPAREN
        if (nextChar == '(')
        {
            loctoken = new Token("LPAREN");
            return loctoken;
        }
        //RIGHTPAREN
        if (nextChar == ')')
        {
            loctoken = new Token("RPAREN");
            return loctoken;
        }
        //LEFTBRACKET
        if (nextChar == '[')
        {
            loctoken = new Token("LBRACKET");
            return loctoken;
        }
        //RIGHTBRACKET
        if (nextChar == ']')
        {
            loctoken = new Token("RBRACKET");
            return loctoken;
        }

        //RELOP
        if (nextChar == '=')
        {
            loctoken = new Token("RELOP", "1");
            return loctoken;
        }
        if (nextChar == '<')
        {
            lookahead = cStream.GetChar();
            if (lookahead == '>')
            {
                loctoken = new Token("RELOP", "2");
                return loctoken;
            } else if (lookahead == '=')
            {
                loctoken = new Token("RELOP", "5");
                return loctoken;
            } else
            {
                //necessary Pushback
                cStream.Pushback(lookahead);
                //see above in assign op, may move this block
                loctoken = new Token("RELOP", "3");
                return loctoken;
            }
        }
        if (nextChar == '>')
        {
            lookahead = cStream.GetChar();
            if (lookahead == '=')
            {
                loctoken = new Token("RELOP", "6");
                return loctoken;
            } else
            {
                cStream.Pushback(lookahead);
                //assignop issue
                loctoken = new Token("RELOP", "4");
                return loctoken;
            }
        }

        if (nextChar == '.')
        {
            //need lookahead for ..
            lookahead = cStream.GetChar();
            if (lookahead == '.')
            {
                loctoken.SetType("DOUBLEDOT");
                //cStream.Pushback(cStream.GetChar());
            } else
            {
                loctoken.SetType("ENDMARKER");
                cStream.Pushback(lookahead);
            }
        }


        //MULOP
        if (nextChar == '*')
        {
            loctoken = new Token("MULOP", "1");
            return loctoken;
        } else if (nextChar == '/')
        {
            loctoken = new Token("MULOP", "2");
            return loctoken;
        }

        //ADDOP
        if (nextChar == '+')
        {
            if (unarycheck(this.prevToken))
            {
                loctoken = new Token("ADDOP", "1");
                return loctoken;
            }
            //Unaryplus
            else
            {
                loctoken = new Token("UNARYPLUS");
                return loctoken;
            }
        }

        if (nextChar == '-')
        {
            if (unarycheck(this.prevToken))
            {
                loctoken = new Token("ADDOP", "2");
                return loctoken;
            }

            //Unaryminus
            else
            {
                loctoken = new Token("UNARYMINUS");
                return loctoken;
            }
        } else
        {
            return loctoken;
            //add error handling
        }
    }
}