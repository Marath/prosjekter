package no.uio.ifi.cflat.scanner;

/*
 * module Scanner
 */

import no.uio.ifi.cflat.chargenerator.CharGenerator;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;
import static no.uio.ifi.cflat.scanner.Token.*;

/*
 * Module for forming characters into tokens.
 */
public class Scanner {
    public static Token curToken, nextToken, nextNextToken;
    public static String curName, nextName, nextNextName;
    public static int curNum, nextNum, nextNextNum;
    public static int curLine, nextLine, nextNextLine;
    
    public String toIgnore[] = {"/*", "*/"};
    public static Token commentToken; //token som kastes vekk
    static boolean commentBool = false;
    
	
    public static void init() {
	//-- Must be changed in part 0:
	//System.err.println("Scanner starting");
	readNext(); readNext(); readNext();
    }
    
    public static void finish() {
	//-- Must be changed in part 0:
	//System.err.println("Scanner has finished its job");
    }
	
    public static void readNext() {
	curToken = nextToken;  nextToken = nextNextToken;
	curName = nextName;  nextName = nextNextName;
	curNum = nextNum;  nextNum = nextNextNum;
	curLine = nextLine;  nextLine = nextNextLine;
	
	String dummy = "";
	int i = 1;
	Boolean negative = false;

	nextNextToken = null;
	while (nextNextToken == null) {
	    nextNextLine = CharGenerator.curLineNum();

	    ignoreWhiteSpace();
	    ignoreComment();
	    
	    while(CharGenerator.curC == '\n') CharGenerator.readNext();
	    
	    if (! CharGenerator.isMoreToRead()) {
		nextNextToken = eofToken; break;
		
		/** Symbolbehandling -----------*/
	    } else if ((CharGenerator.curC == '-' && !isNumber09(CharGenerator.nextC)) || isSymbol(CharGenerator.curC)/* || isSymbol(CharGenerator.nextC)*/) {
		if(CharGenerator.curC != ' ') {
		    
		    nextNextName = Character.toString(CharGenerator.curC);

		    //    System.err.println("nextNExtName in isSymbol ----------------: " + nextNextName);
		}

		//System.err.println("From symbols - curC: " + CharGenerator.curC);

		// if tester fra Uberfil'
		if(!isFeed(CharGenerator.curC)) {
		    if(!doubleSymbols(CharGenerator.curC, CharGenerator.nextC)) { //---------------
			switch(nextNextName) { 
			case "'": 
			    CharGenerator.readNext();
			    if(CharGenerator.curC == '\n') 
				Error.error(CharGenerator.curLineNum(), "Syntax error inside putchar");
			    nextNextToken = numberToken; 
			    nextNextNum = (int) CharGenerator.curC;
			    //    System.err.println("Testprint inside case ': --------- " + nextNextNum);
			    CharGenerator.readNext();
			    break;
			case ",": nextNextToken = commaToken; break; /** , ; */
			case ";": nextNextToken = semicolonToken; break;
				
			case "+": nextNextToken = addToken;break; /** +, -, /, * */
			case "-": 
			    if(isNumber09(CharGenerator.nextC)) { 
				negative = true; break;
			    } else {
				nextNextToken = subtractToken;break;
			    }
			case "/": nextNextToken = divideToken;break;
			case "*": nextNextToken = multiplyToken;break;
				
			case ">": nextNextToken = greaterToken;break; /** >, <, = */
			case "<": nextNextToken = lessToken;break;
			case "=": nextNextToken = assignToken;break;
				
			case "(": nextNextToken = leftParToken;break;   /** (, ), [, ], {, } */
			case ")": nextNextToken = rightParToken;break;
			case "[": nextNextToken = leftBracketToken;break;
			case "]": nextNextToken = rightBracketToken;break;
			case "{": nextNextToken = leftCurlToken;break;
			case "}": nextNextToken = rightCurlToken;break;
			default: System.err.println("Dafuq is this: " + nextNextName); break;
			}
			
			//	System.err.println("After switch 1 - nextNextName: " + nextNextName + " nextNextToken: " + nextNextToken);
		    } else {
			switch(CharGenerator.nextC) {
			case '=': nextNextName += '=';break;
			default: System.err.println("This message was printed out from the line 115 in Scanne.java");break;
			}
			
			switch(nextNextName) {
			case ">=": nextNextToken = greaterEqualToken; break;
			case "<=": nextNextToken = lessEqualToken; break;
			case "==": nextNextToken = equalToken; break;
			case "!=": nextNextToken = notEqualToken; break;
			}  
			//	System.err.println("After switch 2 - nextNextName: " + nextNextName + " nextNextToken: " +  nextNextToken);
			CharGenerator.readNext();
		    }
		}
	

		/** Bokstavbehandling */
	    } else if (isLetterAZ(CharGenerator.curC)) {
		//		System.err.println("CurC before reading while ------: " + CharGenerator.curC);
		//	while(!isFeed(CharGenerator.curC)) { /** Endret fra NextC til curC */

		dummy = "";

		// if(!isLetterAZ(CharGenerator.nextC)/* || !isNumber09(CharGenerator.nextC)*/) {
		nextNextName = Character.toString(CharGenerator.curC);
		//} 
		//else { 
		if(!isFeed(CharGenerator.nextC)) {//sjekke pa lovlig tegn
		    while(!isFeed(CharGenerator.curC)) {
			if(isSymbol(CharGenerator.nextC)) {
			    dummy += CharGenerator.curC;
			    break;
			}
			    
			if(dummy.equals("")) {
			    dummy = Character.toString(CharGenerator.curC);
			} else {
			    dummy += CharGenerator.curC;
			}
			    
			
			//	    nextNextName = dummy;
			CharGenerator.readNext();
			    
		    } // END lesewhile -------
		    nextNextName = dummy.trim();
		    //System.err.println("FROM SCANNER: dummy: " + dummy);
		}
		switch(nextNextName) { /** int, double, for, if, else, while, name->(default) */
		case "int": nextNextToken = intToken; break;
		case "double": nextNextToken = doubleToken; break;
		case "for": nextNextToken = forToken; break;
		case "if": nextNextToken = ifToken; break;
		case "else": nextNextToken = elseToken; break;
		case "while": nextNextToken = whileToken; break;
		case "return": nextNextToken = returnToken; break;
		    // case "exit": 
		    //      CharGenerator.readNext(); CharGenerator.readNext();
		    
		    //      nextNextToken = nameToken;
		    //      nextNextNum = (int) CharGenerator.curC;
		    
		    //      CharGenerator.readNext(); CharGenerator.readNext();
		    //      //	    System.err.println("Scanner sin nextNextName: " + nextNextName);
		    //     break;
		default: nextNextToken = nameToken; break;
		}
		    
		//	dummy = null;
		//System.err.println("After switch in isLetter - nextNextName: " + nextNextName + " nextNextToken: " + nextNextToken);
		
		/** tallbehandling ------ MÅ LEGGETIL STØTTE FOR MINUS*/
	    } else if ((CharGenerator.curC == '-' && isNumber09(CharGenerator.nextC)) || isNumber09(CharGenerator.curC)) {
		dummy = "";
		//System.err.println(CharGenerator.curC);
		if(CharGenerator.curC == '-') {
		    negative = true;

		}
		dummy += CharGenerator.curC;
		
		while(isNumber09(CharGenerator.nextC)) {
			
		    dummy += CharGenerator.nextC;
			
		    CharGenerator.readNext();
		}
		nextNextNum = Integer.parseInt(dummy.trim());
		if(negative == true) {
		    nextNextNum = nextNextNum * -1;
		}
		
		//System.err.println(nextNextNum + "------------------" + negative);
		nextNextToken = numberToken;
		negative = false;
		//		System.err.println("After switch - nextNextNum: " + nextNextNum + " nextNextToken: " + nextNextToken);
		/** kun for a ignore spaces pa slutten av linjer */
	    } else if(CharGenerator.curC == ' ') { 
		//	System.err.println("Found a space at the end of line: " + nextNextLine);

	    } else { 
		Error.error(nextNextLine,
			    "Illegal symbol: '" + CharGenerator.curC + "'!");
	    }
	    
	    CharGenerator.readNext();
	} // END uberwhile ------------
	
	Log.noteToken();
    }
    
    /**
     * !=, == ,>=, <=
     */
    private static boolean doubleSymbols(char curr, char next) {
	if(!isFeed(next)) {
	    switch(curr) {
	    case '!': break;
	    case '=': break;
	    case '>': break;
	    case '<': break;
	    default: return false;
	    }
	}
	if(curr == '!' && next != '=') {
	    return false;
	} else {
	    switch(next) {
	    case '<': return true;
	    case '>': return true;
	    case '=': return true;
	    }
	}
	return false;
    }
    
    private static void ignoreWhiteSpace() {
	while(CharGenerator.curC == '\n'  || CharGenerator.curC == '\t') {
	    CharGenerator.readNext();
	}
	ignoreComment();
    }
    
    private static void ignoreComment() {
	if(CharGenerator.curC == '/' && CharGenerator.nextC == '*') 
	    commentBool = true;
	
	while(commentBool) {
	    if(CharGenerator.isMoreToRead()) {
		CharGenerator.readNext();
		
		if(CharGenerator.curC == '/' && CharGenerator.nextC == '*') {
		    Error.error(CharGenerator.curLineNum(), "Inner comments are not allowed");
		}
		if(CharGenerator.curC == '*' && CharGenerator.nextC == '/') {
		    CharGenerator.readNext();
		    CharGenerator.readNext();
		    commentBool = false;
		    ignoreWhiteSpace();
		}
	    } else {
		Error.error("Never ending comment!");
	    }
	}
    }
    
    /**
     * Sjekke at curC er en bokstav eller ikke
     */
    private static boolean isSymbol(char c) {
	int ch = (int)c;
	//ASCII values of reserved characters
	if((ch >= 33 && ch <= 45) || (ch == 47) || (ch >= 58 && ch <= 62) || (ch >= 91 && ch <=93) || ch == 96 || (ch >= 123 && ch <=125)) 
	    return true;
	return false;
    }

    private static boolean isFeed(char c) {
	return(c == ' ' || c == '\n' || c == '\t' || c == -1);
    }
    
    private static boolean isLetterAZ(char c) {
	return (Character.isLowerCase(c) || Character.isUpperCase(c));
    }

    private static boolean isNumber09(char c) {
	return (c >= 48 && c <= 57);
    }
	
    public static void check(Token t) {
	if (curToken != t)
	    Error.expected("A " + t);
    }
	
    public static void check(Token t1, Token t2) {
	if (curToken != t1 && curToken != t2)
	    Error.expected("A " + t1 + " or a " + t2);
    }
	
    public static void skip(Token t) {
	check(t);  readNext();
    }
	
    public static void skip(Token t1, Token t2) {
	check(t1,t2);  readNext();
    }

}
