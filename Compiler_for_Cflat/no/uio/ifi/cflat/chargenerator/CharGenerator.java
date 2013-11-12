package no.uio.ifi.cflat.chargenerator;

/*
 * module CharGenerator
 */

import java.io.*;
import no.uio.ifi.cflat.cflat.Cflat;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;

/*
 * Module for reading single characters.
 */
public class CharGenerator {
    public static char curC, nextC;
	
    private static LineNumberReader sourceFile = null;
    private static String sourceLine;
    private static int sourcePos;

    private static boolean firstTime;
    private static int wait;
	
    public static void init() {
	try {
	    sourceFile = new LineNumberReader(new FileReader(Cflat.sourceName));
	} catch (FileNotFoundException e) {
	    Error.error("Cannot read " + Cflat.sourceName + "!");
	}
	sourceLine = "";  sourcePos = 0;  curC = nextC = ' ';

	firstTime = true;
	wait = 1;
	
	readNext();  readNext();
    }
	
    public static void finish() {
	if (sourceFile != null) {
	    try {
		sourceFile.close();
	    } catch (IOException e) {
		Error.error("Could not close source file!");
	    }
	}
    }

    /**
     * Inneholder mulig feil
     */
    public static boolean isMoreToRead() {
	//-- Must be changed in part 0:
	/*
	  if(curC != (char) -1) {

	  return true;
	  }
	*/

	if(sourceLine != null) {
	    return true;
	}
	if(wait == 2) {
	    return false;
	}
	else {
	    wait = 2;
	    return true;
	}


	//	 System.out.println("in isMoreToRead: " + nextC);
	//	return false;

    }
	
    public static int curLineNum() {
	return (sourceFile == null ? 0 : sourceFile.getLineNumber());
    }

     private static void toLog(String sourceLine) {
	// adds a line to the log
	if(sourceLine == null) {
	    Log.noteSourceLine(curLineNum(), " ");
	}
	else {
	    Log.noteSourceLine(curLineNum(), sourceLine);
	}
    }
    
    private static boolean checkLine() {
	try {
	    if(sourceLine == null) {
		return false;
	    }
	    
	    if(sourceLine.length() == 0) {
		nextC = '\n';
		sourceLine = sourceFile.readLine();
		toLog(sourceLine);
		sourcePos = 0;

		return false;
	    }
	}
	catch(IOException ioe) {
	    System.err.println("caught an io exception: " + ioe.getMessage());
	    ioe.printStackTrace();
	}
	return true;
    }
    

    /**
     * A methode that checks if nextC == '#', if it does, skips this line by 
     * reading the next line. Then adds the new line to the log, resets the 
     * sourcePos variable and then checks if the new line is valid (i.e contains
     * more than '\n'). If it does, exit methode, else, read the first character
     * of the new line (and increase the sourcePos), and continue the test
     * until nextC =/= '#'.
     * 
     */
    private static void ignoreComment() {
	try {
	    while(nextC == '#') {
		sourceLine = sourceFile.readLine();
		if(sourceLine != null) {
		
		    toLog(sourceLine);
		    sourcePos = 0;
		
		    if(!checkLine()) {
			return; // line is not valid, exit function
		    }
		
		    // might need to check if sourceLine is null here
		    nextC = sourceLine.charAt(sourcePos);
		    sourcePos++;
		}
	    }
	}
	catch(IOException ioe) {
	    System.err.println("caught an io exception: " + ioe.getMessage());
	    ioe.printStackTrace();
	}
    }

	
    /**
     * Leser og setter curC og nextC
     * isMoreToRead tar seg av EOF
     *
     */
    public static void readNext() {
	curC = nextC;
	if (! isMoreToRead()) {
	    //nextC = (int) -1;
	    return;
	}
	
	try {
	    
	    if(firstTime) {
		sourceLine = sourceFile.readLine();
		toLog(sourceLine);
		firstTime = false;
	    }	    

	    if(sourcePos < sourceLine.length()) {
		if(!checkLine()) { // check i line is valid (i.e. doesn't contain only a newline)
		    return; // line is not valid, exit function
		}

		nextC = sourceLine.charAt(sourcePos);
		sourcePos++;
		
		ignoreComment();		
	    }
	    else { // we've reached the end of the current source line, read next line
		if(sourceLine != null) {
		    sourceLine = sourceFile.readLine(); // reads the next line
		    toLog(sourceLine); // adds current line to log
		    sourcePos = 0; // resets sourcePos counter
		    
		    // update it to ' ' so that we dont get duplicate chars		
		    nextC = ' ';
		}
	    }
	}
	catch(IOException ioe) {
	    System.err.println("caught an io exception: " + ioe.getMessage());
	    ioe.printStackTrace();
	}

    }
}
