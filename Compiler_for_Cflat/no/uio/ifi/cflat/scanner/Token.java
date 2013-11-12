package no.uio.ifi.cflat.scanner;

/*
 * class Token
 */

/*
 * The different kinds of tokens read by Scanner.
 */
public enum Token { 
    addToken, assignToken, 
    commaToken, 
    divideToken, doubleToken,
    elseToken, eofToken, equalToken, 
    forToken, 
    greaterEqualToken, greaterToken, 
    ifToken, intToken, 
    leftBracketToken, leftCurlToken, leftParToken, lessEqualToken, lessToken, 
    multiplyToken, 
    nameToken, notEqualToken, numberToken, 
    rightBracketToken, rightCurlToken, rightParToken, returnToken, 
    semicolonToken, subtractToken, 
	whileToken;

    /**
     * divide og multiplyToken brukt
     */
    public static boolean isFactorOperator(Token t) {
	if(t.equals(multiplyToken)) 
	    return true;
	else if (t.equals(divideToken)) 
	    return true;
	else
	    return false;
    }
    
    public static boolean isTermOperator(Token t) {
	//-- Must be changed in part 0:
	if(t.equals(addToken)) 
	    return true;
	else if(t.equals(subtractToken)) 
	    return true;
	else
	    return false;
    }

    public static boolean isRelOperator(Token t) {
	//-- Must be changed in part 0:
	if(t.equals(greaterToken)) // >
	    return true;
	else if(t.equals(greaterEqualToken)) // >=
	    return true;
	else if(t.equals(lessToken)) // <
	    return true;
	else if(t.equals(lessEqualToken)) // <=
	    return true;
	else if(t.equals(equalToken)) //==
	    return true;
	else if(t.equals(notEqualToken)) //!=
	    return true;
	else
	    return false;
    }

    /**
     * Kanskje sjekke videre
     */
    public static boolean isOperand(Token t) {
	// tall, variabel, funksjonkall, uttrykk
	if(t.equals(numberToken)) // 1 2 3
	    return true;
	else if(t.equals(nameToken)) // int -> i1 <---this one
	    return true;
	else if(t == leftParToken) // (
	    return true;
	else if(t == rightParToken) // )
	    return true;
	else if(t.equals(leftBracketToken)) // [
	    return true;
	else if(t.equals(rightBracketToken)) // ]
	    return true;
	else
	    return false;
    }

    public static boolean isTypeName(Token t) {
	// double og int
	if(t.equals(intToken)) 
	    return true;
	else if(t.equals(doubleToken)) 
	    return true;
	else
	    return false;
    }
}
