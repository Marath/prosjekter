package no.uio.ifi.cflat.syntax;

/*
 * module Syntax
 */

import no.uio.ifi.cflat.cflat.Cflat;
import no.uio.ifi.cflat.code.Code;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;
import no.uio.ifi.cflat.scanner.Scanner;
import no.uio.ifi.cflat.scanner.Token;
import static no.uio.ifi.cflat.scanner.Token.*;
import no.uio.ifi.cflat.types.*;

/*
 * Creates a syntax tree by parsing; 
 * prints the parse tree (if requested);
 * checks it;
 * generates executable code. 
 */
public class Syntax {
    static DeclList library;
    static Program program;

    public static void init() {
	//-- Must be changed in part 1:
	System.out.println("Syntax initiating");
    }

    public static void finish() {
	//-- Must be changed in part 1:
	System.out.println("Syntax finished");
    }

    public static void checkProgram() {
	program.check(library);
    }

    public static void genCode() {
	program.genCode(null);
    }

    public static void parseProgram() {
	program = new Program();
	program.parse();
    }

    public static void printProgram() {
	//System.out.println("\nin Syntax.printProgram");
	program.printTree();
    }

    static void error(SyntaxUnit use, String message) {
	Error.error(use.lineNum, message);
    }
}


/*
 * Master class for all syntactic units.
 * (This class is not mentioned in the syntax diagrams.)
 */
abstract class SyntaxUnit {
    int lineNum;

    SyntaxUnit() {
	lineNum = Scanner.curLine;
    }

    abstract void check(DeclList curDecls);
    abstract void genCode(FuncDecl curFunc);
    abstract void parse();
    abstract void printTree();
}


/*
 * A <program>
 */
class Program extends SyntaxUnit {
    DeclList progDecls = new GlobalDeclList();
	
    @Override void check(DeclList curDecls) {
	progDecls.check(curDecls);

	if (! Cflat.noLink) {
	    // Check that 'main' has been declared properly:
	    //-- Must be changed in part 2:
	}
    }
		
    @Override void genCode(FuncDecl curFunc) {
	progDecls.genCode(null);
    }

    @Override void parse() {
	Log.enterParser("<program>");

	progDecls.parse();
	if (Scanner.curToken != eofToken)
	    Error.expected("A declaration");

	Log.leaveParser("</program>");
    }

    @Override void printTree() {
	//System.out.println("in Program.printTree");
	progDecls.printTree();
    }
}


/*
 * A declaration list.
 * (This class is not mentioned in the syntax diagrams.)
 */

abstract class DeclList extends SyntaxUnit {
    Declaration firstDecl = null;
    DeclList outerScope;
    Declaration lastDecl = null;

    DeclList () {
	//-- Must be changed in part 1:
    }

    @Override void check(DeclList curDecls) {
	outerScope = curDecls;

	Declaration dx = firstDecl;
	while (dx != null) {
	    dx.check(this);  dx = dx.nextDecl;
	}
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//System.out.println("in DeclList.printTree");
	Declaration tmp = firstDecl;

	while(tmp != null) {
	    tmp.printTree();
	    if(tmp.nextDecl != null) {
		Log.wTreeLn("");
	    }
	    tmp = tmp.nextDecl;
	}
    }

    void addDecl(Declaration d) {
	//-- Must be changed in part 1:	

	if(firstDecl == null) {
	    firstDecl = d;
	    lastDecl = firstDecl;
	} else {
	    lastDecl.nextDecl = d;
	    lastDecl = d;
	}
    }

    int dataSize() {
	Declaration dx = firstDecl;
	int res = 0;

	while (dx != null) {
	    res += dx.declSize();  dx = dx.nextDecl;
	}
	return res;
    }

    Declaration findDecl(String name, SyntaxUnit usedIn) {
	//-- Must be changed in part 2:
	return null;
    }
}


/*
 * A list of global declarations. 
 * (This class is not mentioned in the syntax diagrams.)
 */
class GlobalDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	while (Token.isTypeName(Scanner.curToken)) {
	    if (Scanner.nextToken == nameToken) {
		if (Scanner.nextNextToken == leftParToken) {
		    FuncDecl fd = new FuncDecl(Scanner.nextName);
		    fd.parse();
		    addDecl(fd);
		} else if (Scanner.nextNextToken == leftBracketToken) {
		    GlobalArrayDecl gad = new GlobalArrayDecl(Scanner.nextName);
		    gad.parse();
		    addDecl(gad);
		} else {
		    //-- Must be changed in part 1:
		    GlobalSimpleVarDecl gsvd = new GlobalSimpleVarDecl(Scanner.nextName);
		    gsvd.parse();
		    addDecl(gsvd);
		}
	    } else {
		Error.expected("A declaration");
	    }
	}
    }

    // @Override void printTree() {

    //     Log.indentTree();
    //  	//System.out.println("in ParamDeclList.printTree");
    //  	Declaration tmp = firstDecl;

    //  	while(tmp != null) {
    //  	    tmp.printTree();
    //  	    // if(tmp.nextDecl != null) {
    //  	    // 	Log.wTree(", ");
    //  	    // }
    //  	    tmp = tmp.nextDecl;
    //  	}
    // 	Log.outdentTree();
    //  }
}


/*
 * A list of local declarations. 
 * (This class is not mentioned in the syntax diagrams.)
 */
class LocalDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	while(Token.isTypeName(Scanner.curToken)) {
	    if (Scanner.nextToken == nameToken) {
		if (Scanner.nextNextToken == semicolonToken) {
		    /*local var decl*/
		    
		    VarDecl vd = new LocalSimpleVarDecl(Scanner.nextName);
		    vd.parse();
		    addDecl(vd);
		    // LocalSimpleVarDecl lsv = new LocalSimpleVarDecl(Scanner.nextName);
		    // lsv.parse();
		    // addDecl(lsv);
		} 
		else if (Scanner.nextNextToken == leftBracketToken) {
		    /*local array decl*/

		    VarDecl vd = new LocalArrayDecl(Scanner.nextName);
		    vd.parse();
		    addDecl(vd);
		    // LocalArrayDecl lad = new LocalArrayDecl(Scanner.nextName);
		    // lad.parse();
		    // addDecl(lad);
		} 
		else {
		    /*error not a var decl*/
		    Error.expected("A semicolonToken or leftBracketToken");
		}
	    }
	    else {
		/*expected name token*/
		Error.expected("A nameToken");
	    }
	    // might not need to add a skip part here, because curToken is 
	    // updated in vd.parse
	    // Scanner.readNext();
	    // Scanner.skip(nameToken); // we've already parsed the nameToken, move on
	}
    }
    @Override void printTree() {

	//	System.out.println("in LocalDeclList.printTree");
    	Declaration tmp = firstDecl;

    	while(tmp != null) {
    	    tmp.printTree();
    	    if(tmp.nextDecl == null) {
    	    	Log.wTreeLn("");
    	    }
    	    tmp = tmp.nextDecl;
    	}
    }
}


/*
 * A list of parameter declarations. 
 * (This class is not mentioned in the syntax diagrams.)
 */
class ParamDeclList extends DeclList {
    //ParamDecl pd = null;
    VarDecl vd = null;

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	// once we reach ')' we know that the param decl is over
	while (Scanner.curToken != rightParToken) {	    
	    if(Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }
	    
	    // pd = new ParamDecl(Scanner.nextName);
	    // pd.parse();
	    // addDecl(pd);
	    //System.out.println("\nin paramlist name: " + Scanner.nextName);

	    vd = new ParamDecl(Scanner.nextName);
	    vd.parse();
	    addDecl(vd);
	    // System.out.println("\nin paramlist curToken: " + Scanner.curToken);
	    
	    // might not need to add a skip part here, because curToken is 
	    // updated in pd.parse
	    
	    //Scanner.readNext(); // on to the next param, if any
	    //Scanner.skip(nameToken); // we've allready parsed the nameToken, move on
	}
    }
    
    @Override void printTree() {

    	//System.out.println("in ParamDeclList.printTree");
    	Declaration tmp = firstDecl;

    	while(tmp != null) {
    	    tmp.printTree();
    	    if(tmp.nextDecl != null) {
    		Log.wTree(", ");
    	    }
    	    tmp = tmp.nextDecl;
    	}
    }
}


/*
 * Any kind of declaration.
 * (This class is not mentioned in the syntax diagrams.)
 */
abstract class Declaration extends SyntaxUnit {
    String name, assemblerName;
    Type type; 
    boolean visible = false;
    Declaration nextDecl = null;

    Declaration(String n) {
	name = n;
    }

    abstract int declSize();

    /**
     * checkWhetherArray: Utility method to check whether this Declaration is
     * really an array. The compiler must check that a name is used properly;
     * for instance, using an array name a in "a()" or in "x=a;" is illegal.
     * This is handled in the following way:
     * <ul>
     * <li> When a name a is found in a setting which implies that should be an
     *      array (i.e., in a construct like "a["), the parser will first 
     *      search for a's declaration d.
     * <li> The parser will call d.checkWhetherArray(this).
     * <li> Every sub-class of Declaration will implement a checkWhetherArray.
     *      If the declaration is indeed an array, checkWhetherArray will do
     *      nothing, but if it is not, the method will give an error message.
     * </ul>
     * Examples
     * <dl>
     *  <dt>GlobalArrayDecl.checkWhetherArray(...)</dt>
     *  <dd>will do nothing, as everything is all right.</dd>
     *  <dt>FuncDecl.checkWhetherArray(...)</dt>
     *  <dd>will give an error message.</dd>
     * </dl>
     */
    abstract void checkWhetherArray(SyntaxUnit use);

    /**
     * checkWhetherFunction: Utility method to check whether this Declaration
     * is really a function.
     * 
     * @param nParamsUsed Number of parameters used in the actual call.
     *                    (The method will give an error message if the
     *                    function was used with too many or too few parameters.)
     * @param use From where is the check performed?
     * @see   checkWhetherArray
     */
    abstract void checkWhetherFunction(int nParamsUsed, SyntaxUnit use);

    /**
     * checkWhetherSimpleVar: Utility method to check whether this
     * Declaration is really a simple variable.
     *
     * @see   checkWhetherArray
     */
    abstract void checkWhetherSimpleVar(SyntaxUnit use);
}


/*
 * A <var decl>
 */
abstract class VarDecl extends Declaration {
    VarDecl(String n) {
	super(n);
    }

    @Override int declSize() {
	return type.size();
    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
	Syntax.error(use, name + " is a variable and no function!");
    }
	
    @Override void printTree() {
	//System.out.println("in varDecl.printTree");
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
    }

    //-- Must be changed in part 1+2:
}


/*
 * A global array declaration
 */
class GlobalArrayDecl extends VarDecl {
    ArrayType at = null;

    GlobalArrayDecl(String n) {
	super(n);
	assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override void check(DeclList curDecls) {
	visible = true;
	if (((ArrayType)type).nElems < 0)
	    Syntax.error(this, "Arrays cannot have negative size!");
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	/* OK */
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	Syntax.error(use, name + " is an array and no simple variable!");
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
        Type tmp = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(leftBracketToken); // skip '['
	System.out.println("in Global array decl: curNum: " + Scanner.nextNextNum);
	at = new ArrayType(Scanner.curNum, tmp);
	Scanner.skip(numberToken); // skip numberToken
	Scanner.skip(rightBracketToken); // skip ']'
	Scanner.skip(semicolonToken); // skip ';'

	Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//System.out.println("in GlobalArrayDecl.printTree");
	//Log.indentTree();	
	Log.wTree(at.elemType.typeName() + " " + name + "[" + at.nElems + "]");
	Log.wTreeLn(";");
	//Log.outdentTree();
    }
}


/*
 * A global simple variable declaration
 */
class GlobalSimpleVarDecl extends VarDecl {
    GlobalSimpleVarDecl(String n) {
	super(n);
	assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	/* OK */
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
	type = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(semicolonToken); // skip ';'

	Log.leaveParser("</var decl>");
    }
    
    @Override void printTree() {
	//Log.indentTree();	
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
	//Log.outdentTree();
    }
}


/*
 * A local array declaration
 */
class LocalArrayDecl extends VarDecl {
    ArrayType at = null;
	
    LocalArrayDecl(String n) {
	super(n); 
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
        Type tmp = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(leftBracketToken); // skip '['	
	at = new ArrayType(Scanner.curNum, tmp);
	Scanner.skip(numberToken); // skip numberToken
	Scanner.skip(rightBracketToken); // skip ']'
	Scanner.skip(semicolonToken); // skip ';'
	
	Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//Log.indentTree();	
	Log.wTree(at.elemType.typeName() + " " + name + "[" + at.nElems + "]");
	Log.wTreeLn(";");
	//Log.outdentTree();
    }

}


/*
 * A local simple variable declaration
 */
class LocalSimpleVarDecl extends VarDecl {
    // LocalSimpleVarDecl nextLocalVar = null;
    
    LocalSimpleVarDecl(String n) {
	super(n); 
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
	type = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(semicolonToken); // skip ';'

	Log.leaveParser("</var decl>");
    }
    
    @Override void printTree() {
	//Log.indentTree();	
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
	//Log.outdentTree();
    }
}


/*
 * A <param decl>
 */
class ParamDecl extends VarDecl {
    int paramNum = 0;
    //ParamDecl nextParam = null;

    ParamDecl(String n) {
	super(n);
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<param decl>");

	//-- Must be changed in part 1:
	type = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name

	Log.leaveParser("</param decl>");
    }
    
    @Override void printTree() {
	Log.wTree(type.typeName() + " " + name);
    }
}


/*
 * A <func body>
 */
class FuncBody extends SyntaxUnit { // not sure if it should extend SyntaxUnit
    LocalDeclList ldl = null;
    StatmList sl = null;

    @Override void check(DeclList curDecls) {
    }

    @Override void genCode(FuncDecl curFunc) {
    }
    
    @Override void parse() {
	Log.enterParser("<func body>");

	// empty lists of statm/localdecl are accepted
	// and var decls must be made before any statements
	ldl = new LocalDeclList();
	ldl.parse();
	sl = new StatmList();
	sl.parse();

	Log.leaveParser("<func body>");
    }
    
    @Override void printTree() {
	Log.indentTree();
	ldl.printTree();
	sl.printTree();
	Log.outdentTree();
    }
}

/*
 * A <func decl>
 */
class FuncDecl extends Declaration {
    //-- Must be changed in part 1+2:
    ParamDeclList pdl = null;
    FuncBody fb = null;
	
    FuncDecl(String n) {
	// Used for user functions:

	super(n);
	assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
	//-- Must be changed in part 1:
	type = Types.getType(Scanner.curToken);
	// types er null for int fordi, Types.java sin init ikke oppretter intType
	//System.out.println("in funcDecl type = " + type);
    }

    @Override int declSize() {
	return 0;
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
	//-- Must be changed in part 2:
    }
	
    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	Code.genInstr("", ".globl", assemblerName, "");
	Code.genInstr(assemblerName, "pushl", "%ebp", "Start function "+name);
	Code.genInstr("", "movl", "%esp,%ebp", "");
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	Log.enterParser("<func decl>");	

	Scanner.readNext(); // read past either int or double
	Scanner.skip(nameToken); // skip function name
	Scanner.skip(leftParToken); // skip left parenthesis
	
	// curToken is now the first param or rightParToken

	pdl = new ParamDeclList();
	pdl.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);	
	fb = new FuncBody();
	fb.parse();
	Scanner.skip(rightCurlToken);

	Log.leaveParser("</func decl>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//Log.indentTree();

	//System.out.println("in funcDecl.printTree");
	Log.wTree(type.typeName() + " " + name);
	Log.wTree(" ("); pdl.printTree(); Log.wTreeLn(")");
	Log.wTreeLn("{");
	fb.printTree();
	Log.wTreeLn("}");

	// Log.outdentTree();
    }
}


/*
 * A <statm list>. 
 */
class StatmList extends SyntaxUnit {
    //-- Must be changed in part 1:
    Statement firstStatm = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<statm list>");

	Statement lastStatm = null;
	while (Scanner.curToken != rightCurlToken) {
	    Log.enterParser("<statement>");
	    //-- Must be changed in part 1:

	    // int i = 1;
	    // System.out.println("\n" + (i++) + " curToken: " + Scanner.curToken);
	    // System.out.println((i++) + " curName: " + Scanner.curName);
	    // System.out.println((i++) + " nextToken: " + Scanner.nextToken);
	    // System.out.println((i++) + " nextName: " + Scanner.nextName);

	    Statement s = firstStatm.makeNewStatement();
	    s.parse();

	    if(firstStatm == null) {
	     	firstStatm = s;
	     	lastStatm = firstStatm;
	    } else {
	     	// System.err.println("DEBUG - Statmlist.parse(): lastStatm er: " 
	     	// 		   + (lastStatm==null ? "null" : "ikke null"));
	     	lastStatm.nextStatm = s;
		lastStatm = s;
	     	
	    }
	    
	    Log.leaveParser("</statement>");
	}

	Log.leaveParser("</statm list>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//Log.indentTree();

	//System.err.println("in statmlist.printTree");
	Statement tmp = firstStatm;
	while(tmp != null) {
	    tmp.printTree();
	    tmp = tmp.nextStatm;
	}

	//Log.outdentTree();	
    }
}


/*
 * A <statement>.
 */
abstract class Statement extends SyntaxUnit {
    Statement nextStatm = null;

    static Statement makeNewStatement() {
	if (Scanner.curToken==nameToken && 
	    Scanner.nextToken==leftParToken) {
	    return new CallStatm();
	} else if (Scanner.curToken == nameToken) {
	    return new AssignStatm();
	} else if (Scanner.curToken == forToken) {
	    return new ForStatm();
	} else if (Scanner.curToken == ifToken) {
	    return new IfStatm();
	} else if (Scanner.curToken == returnToken) {
	    return new ReturnStatm();
	} else if (Scanner.curToken == whileToken) {
	    return new WhileStatm();
	} else if (Scanner.curToken == semicolonToken) {
	    return new EmptyStatm();
	} else {
	    Error.expected("A statement");
	}
	return null;  // Just to keep the Java compiler happy. :-)
    }
}

/*
 * A call statement
 */
class CallStatm extends Statement {
    //-- Must be changed in part 1+2:
    FunctionCall fc = null;
    
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<call-statm>");
	
	/* 
	 *  putchar( found 
	 */
	// if test is redundant because its the same as the one in 
	// statement.makeNewStatement()
	//if(Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
	fc =  new FunctionCall();
	fc.parse();
	//}
	Scanner.skip(semicolonToken);

	//	Scanner.skip(semicolonToken);

	Log.leaveParser("</call-statm>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//	Log.indentTree();

	fc.printTree();
	Log.wTreeLn(";");
	
	//	Log.outdentTree();
    }
}


/*
 * An Assign statement
 */
class AssignStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression test = null;
    Variable v = null;
  
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	// -- Must be changed in part 1:
	Log.enterParser("<assign-statm>");
	Log.enterParser("<assignment>");
	
	v = new Variable();
	v.parse();
	System.err.println("1");
	
	Scanner.skip(assignToken);
	//--
	System.err.println("2");
	test = new Expression();
	test.parse();
	
	if(Scanner.curToken == semicolonToken)
	    Scanner.skip(semicolonToken);
	else if(Scanner.curToken == rightParToken)
	    Scanner.skip(rightParToken);
	else
	    System.err.println("AssignStatm says: hmm no unknown token");

	Log.leaveParser("</assignment>");
	Log.leaveParser("</assign-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//Log.indentTree();
	v.printTree();
	Log.wTree(" = ");
	test.printTree();
	Log.wTreeLn(";");
	//Log.outdentTree();
    }
}


/*
 * An <empty statm>.
 */
class EmptyStatm extends Statement {
    //-- Must be changed in part 1+2:

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	Log.enterParser("<empty-statm>");
	
	Scanner.skip(semicolonToken);

	Log.leaveParser("</empty-statm>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//-- Must be changed in part 1:
	Log.wTreeLn(";");
    }
}
	

/*
 * A <for-statm>.
 */
//-- Must be changed in part 1+2:
class ForStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression test = new Expression();
    StatmList body = new StatmList();
    ForControl f;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	// -- Must be changed in part 1:
	// Make same shit for else statm
	Log.enterParser("<for-statm>");
	
	Scanner.readNext();
	Scanner.skip(leftParToken);

	// ForControl skal kjores her-----------------TODO--------------------------------------
	
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	body.parse();
	Scanner.skip(rightCurlToken);

	Log.leaveParser("</for-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree("for (");  test.printTree();  Log.wTreeLn(") {");
	Log.indentTree();
	body.printTree();
	Log.outdentTree();
	Log.wTreeLn("}");
    }
}

class ForControl extends ForStatm {
    
    @Override void parse() {
	new AssignStatm().parse();
	Scanner.skip(semicolonToken);
	
	new Expression().parse();
	Scanner.skip(semicolonToken);
	
	new AssignStatm().parse();
    }     
}

/*
 * An <if-statm>.
 */
class IfStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression test = new Expression();
    StatmList body = new StatmList();
    ElsePart e = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	// Make same shit for else statm
	Log.enterParser("<if-statm>");
	
	Scanner.readNext();
	Scanner.skip(leftParToken);
	test.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	body.parse();
	Scanner.skip(rightCurlToken);
	
	if(Scanner.curToken == elseToken) {
	    e = new ElsePart();
	    e.parse();
	}


	Log.leaveParser("</if-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree("if (");  test.printTree();  Log.wTreeLn(") {");
	Log.indentTree();
	body.printTree();
	Log.outdentTree();
	Log.wTreeLn("}");
	
	if(e != null) {
	    e.printTree();
	}
    }   
} 
/* else part of an if */
class ElsePart extends IfStatm {
    

    @Override void parse() {
	Log.enterParser("<else-part>");
	
	Scanner.skip(elseToken);
	Scanner.skip(leftCurlToken);
	body.parse(); //statmList from super
	Scanner.skip(rightCurlToken);
	
	Log.leaveParser("</else-part>");
    }

    @Override void printTree() {
	Log.wTree("else");  test.printTree();  Log.wTreeLn(" {");
	Log.indentTree();
	body.printTree();
	Log.outdentTree();
	Log.wTreeLn("}");
    }
}


/*
 * A <return-statm>.
 */
//-- Must be changed in part 1+2:
class ReturnStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression test = new Expression();
    
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	Log.enterParser("<return-statm>");
	
	Scanner.readNext();
	test.parse();
	Scanner.skip(semicolonToken);

	Log.leaveParser("</return-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree("return ");  test.printTree();  Log.wTreeLn(";");
    }
}

/*
 * A <while-statm>.
 */
class WhileStatm extends Statement {
    Expression test = new Expression();
    StatmList body = new StatmList();

    @Override void check(DeclList curDecls) {
	test.check(curDecls);
	body.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
	String testLabel = Code.getLocalLabel(), 
	    endLabel  = Code.getLocalLabel();

	Code.genInstr(testLabel, "", "", "Start while-statement");
	test.genCode(curFunc);
	test.valType.genJumpIfZero(endLabel);
	body.genCode(curFunc);
	Code.genInstr("", "jmp", testLabel, "");
	Code.genInstr(endLabel, "", "", "End while-statement");
    }

    @Override void parse() {
	Log.enterParser("<while-statm>");
	
	Scanner.readNext();
	Scanner.skip(leftParToken);
	test.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	body.parse();
	Scanner.skip(rightCurlToken);

	Log.leaveParser("</while-statm>");
    }

    @Override void printTree() {
	Log.wTree("while (");  test.printTree();  Log.wTreeLn(") {");
	Log.indentTree();
	body.printTree();
	Log.outdentTree();
	Log.wTreeLn("}");
    }
}


//-- Must be changed in part 1+2:


/*
 * An <expression list>. ------------------------------------
 */

class ExprList extends SyntaxUnit {
    Expression firstExpr = null;
    Expression lastExpr = null;
    Expression e = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {

	Log.enterParser("<expr list>");
	
	/* run until: ) or ; */
	//while(Scanner.curToken != rightParToken || Scanner.curToken != semicolonToken) {
	
	while(Scanner.curToken != rightParToken) {
	    if(Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }
	    
	    e = new Expression();
	    e.parse();
	    
	    //	    Scanner.readNext();
	    if(firstExpr == null) {
	     	firstExpr = e;
	     	lastExpr = firstExpr;		
	    } else {
		lastExpr.nextExpr = e;
		lastExpr = e;
	    }
	}
	
	Log.leaveParser("</expr list>");

    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	//Log.indentTree();
	Expression tmp = firstExpr;
	while(tmp != null) {
	    tmp.printTree();
	    if(tmp.nextExpr != null) {
		Log.wTree(", ");
	    }
	    tmp = tmp.nextExpr;
	}
	//Log.outdentTree();
    }
    //-- Must be changed in part 1:
}


/*
 * An <expression>
 */
class Expression extends Operand {
    Expression nextExpr = null;
    Term firstTerm = new Term(), secondTerm = null;
    Operator relOp = null;
    boolean innerExpr = false;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<expression>");

	firstTerm.parse();
	if (Token.isRelOperator(Scanner.curToken)) {
	    relOp = new RelOperator(); 
	    relOp.parse();
	    secondTerm = new Term();
	    secondTerm.parse();
	}

	Log.leaveParser("</expression>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	if(relOp == null) {
	    firstTerm.printTree();
	}
	else {
	    firstTerm.printTree();
	    relOp.printTree();
	    secondTerm.printTree();
	}
    }
}

class TermOpr extends Opr {
    TermOpr nextOpr = null;
    String op;

    TermOpr(String o) {
	super(o);
	op = o;
    }
    
    public String getOpr() {
	return op;
    }

    public void setOpr(String newO) {
	op = newO;
    }
}
class FactorOpr extends Opr {
    //  FactorOpr nextOpr = null;
    String op;

    FactorOpr(String o) {
	super(o);
	op = o;
    }
    
    public String getOpr() {
	return op;
    }

    public void setOpr(String newO) {
	op = newO;
    }
}

class Opr {
    Opr nextOpr = null;
    String op;

    Opr(String o) {
	op = o;
    }
    
    public String getOpr() {
	return op;
    }

    public void setOpr(String newO) {
	op = newO;
    }
}

class OprList {
    Opr firstOpr;
    Opr lastOpr;
    OprList l;

    OprList() {
	firstOpr = null;
	lastOpr = null;
    }
    
    public void add(Opr t) {
	if(firstOpr == null) {
	    firstOpr = t;
	    lastOpr = firstOpr;		
	} else {
	    lastOpr.nextOpr = t;
	    lastOpr = t;
	}
    }

    public Opr getFirst() {
	Opr toReturn;
	if(firstOpr != null) {
	    toReturn = firstOpr;
	    firstOpr = firstOpr.nextOpr;
	    return toReturn;
	}
	return null;
    }
    
    public boolean checkFirst() {
	return firstOpr == null;
    }
}

/*
 * A <term>
 */
class Term extends SyntaxUnit { 
    //-- Must be changed in part 1+2:
    Factor firstFactor = null;
    Factor f = null;
    Factor lastFactor = null;
    Opr t; OprList tl = new OprList();

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	//-- Must be changed in part 1:

	Log.enterParser("<term>");
	
	if(!isTermOperator(Scanner.nextToken)) {
	    System.err.println("FROM TERM - nextToken is termOperator");
	    f = new Factor();
	    f.parse();
	    addFactor(f);
	}
	else {
	    System.err.println("FROM TERM we are now in else");
	    while(Scanner.curToken != semicolonToken) {
		f = new Factor();
		f.parse();
		
		if(Scanner.curToken == addToken) {
	
		    if(Scanner.nextToken != semicolonToken) {
			t = new TermOpr("+");
			tl.add(t);
		    }
		    Scanner.skip(addToken);
		    System.err.println("DEBUG FROM TERM, skipped addToken");
		} else if(Scanner.curToken == subtractToken) {
		    
		    if(Scanner.nextToken != semicolonToken) {
			t = new TermOpr("-");
			tl.add(t);
		    }
		    Scanner.skip(subtractToken);
		}
		addFactor(f);
		//	System.err.println("First ele in the list: " + tl.getFirst().getOpr());
		   
	    }
	}
	
	
	Log.leaveParser("</term>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1+2:
	// UNFINISHED, IS MORE COMPLICATED ACCORDING
	// TO SYNTAX DIAGRAM
	Factor tmp = firstFactor;
	while(tmp != null) {
	    tmp.printTree();
	    tmp = tmp.nextFactor;
	    
	    if(!tl.checkFirst()) {
		Log.wTree(tl.getFirst().getOpr());
	    }
	    
	}
    }
    
    void addFactor(Factor f) {
	if(firstFactor == null) {
	    firstFactor = f;
	    lastFactor = firstFactor;
 	   
	} else {
	    lastFactor.nextFactor = f;
	    lastFactor = f;	    
	}
    }
}


// class FactorOprList {
//     FactorOpr firstOpr;
//     FactorOpr lastOpr;
//     FactorOprList fl;

//     FactorOprList() {
// 	firstOpr = null;
// 	lastOpr = null;
//     }
    
//     public void add(FactorOpr t) {
// 	if(firstOpr == null) {
// 	    firstOpr = t;
// 	    lastOpr = firstOpr;		
// 	} else {
// 	    lastOpr.nextOpr = t;
// 	    lastOpr = t;
// 	}
//     }

//     public FactorOpr getFirst() {
// 	FactorOpr toReturn;
// 	if(firstOpr != null) {
// 	    toReturn = firstOpr;
// 	    firstOpr = firstOpr.nextOpr;
// 	    return toReturn;
// 	}
// 	return null;
//     }
    
//     public boolean checkFirst() {
// 	return firstOpr == null;
//     }
    
// }

class Factor extends SyntaxUnit {
    Operand firstOperand = null;
    Operand lastOperand = null;
    Factor nextFactor = null;
    Operand o = null;
    Opr fo; OprList fl = new OprList();
    //  TermOpr to; TermOprList F_tl = new TermOprList();

    @Override void check(DeclList curDecls) {

    }

    @Override void genCode(FuncDecl curFunc) {

    }

    @Override void parse() {
	
	Log.enterParser("<factor>");

	makeFactor();
	
	if(Scanner.curToken == addToken) {
	    fo = new TermOpr("+");
	    fl.add(fo);
	    Scanner.skip(addToken);
	} else if(Scanner.curToken == subtractToken) {
	    fo = new TermOpr("-");
	    fl.add(fo);
	    Scanner.skip(subtractToken);
	}
	
       	if(Scanner.curToken == multiplyToken) { 
	    if(Scanner.nextToken != semicolonToken) {
		fo = new FactorOpr("*");
		fl.add(fo);
	    }
	    Scanner.skip(multiplyToken);
	}
	else if(Scanner.curToken == divideToken) { 
	    if(Scanner.nextToken != semicolonToken) {
		fo = new FactorOpr("/");
		fl.add(fo);
	    }
	    Scanner.skip(divideToken);
	}

	// int i = 1;
	// System.out.println("in factor");
	// System.out.println("\n" + (i++) + " curToken: " + Scanner.curToken);
	// System.out.println((i++) + " nextToken: " + Scanner.nextToken);

	addOperand(o);
	// Log.enterParser("<factor>");
	
	// if(!isFactorOperator(Scanner.nextToken)) {
	//     makeFactor();
	
	// } else {
	// while(Scanner.curToken != semicolonToken) {
		
	//     if(Scanner.curToken == addToken) {
	//     	fo = new TermOpr("+");
	// 	fl.add(fo);
	//     	Scanner.skip(addToken);
		
	//     }

	//     if(Scanner.curToken == subtractToken) {
	//     	fo = new TermOpr("-");
	// 	fl.add(fo);
	//     	Scanner.skip(subtractToken);
	//     }
	    
	    
	//     makeFactor();
	//     System.err.println("From Factor: Scanner.curName: " + Scanner.curName + " the curToken: " + Scanner.curToken);
		
		    
		
	//     if(Scanner.curToken == multiplyToken) {
		    
	// 	if(Scanner.nextToken != semicolonToken) {
	// 	    fo = new FactorOpr("*");
	// 	    fl.add(fo);
	// 	}
	// 	Scanner.skip(multiplyToken);
		    
	//     } else if(Scanner.curToken == divideToken) {
		    
	// 	if(Scanner.nextToken != semicolonToken) {
	// 	    fo = new FactorOpr("/");
	// 	    fl.add(fo);
	// 	}
	// 	Scanner.skip(divideToken);
	//     }
	//     addOperand(o);
	//     //	System.err.println("First ele in the list: " + tl.getFirst().getOpr());
		   
	    
	// }
	// }

	Log.leaveParser("</factor>");
    }

    private void addOperand(Operand op) {
	if(firstOperand == null) {
	    firstOperand = op;
	    lastOperand = firstOperand;
 	   
	} else {
	    lastOperand.nextOperand = op;
	    lastOperand = op;	    
	}
    }

    private void makeFactor() {
	if(Scanner.curToken == numberToken) {
	    o = new Number(Scanner.curNum);
	    o.parse();
	}
	else if(Scanner.curToken == leftParToken) {
	    Scanner.skip(leftParToken);
	    o = new Expression();
	    o.parse();
	    Scanner.skip(rightParToken);
	}
	else if(Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
	    o = new FunctionCall();
	    o.parse();
	}
	else {
	    o = new Variable();
	    o.parse();
	} 
	
    }
    
    
    @Override void printTree() {
	//o.printTree(); // is more complicated
	Operand tmp = firstOperand;
	while(tmp != null) {
	    tmp.printTree();
	    tmp = tmp.nextOperand;
	    
	    if(!fl.checkFirst()) {
		Log.wTree(fl.getFirst().getOpr());
	    }
	    
	    
	}
    }  
}

//-- Must be changed in part 1+2:

/*
 * An <operator>
 */
abstract class Operator extends SyntaxUnit {
    Operator nextOp = null;
    Type opType;
    Token opToken;

    @Override void check(DeclList curDecls) {}
}


//-- Must be changed in part 1+2:


/*
 * A relational operator (==, !=, <, <=, > or >=).
 */

class RelOperator extends Operator {
    @Override void genCode(FuncDecl curFunc) {
	if (opType == Types.doubleType) {
	    Code.genInstr("", "fldl", "(%esp)", "");
	    Code.genInstr("", "addl", "$8,%esp", "");
	    Code.genInstr("", "fsubp", "", "");
	    Code.genInstr("", "fstps", Code.tmpLabel, "");
	    Code.genInstr("", "cmpl", "$0,"+Code.tmpLabel, "");
	} else {
	    Code.genInstr("", "popl", "%ecx", "");
	    Code.genInstr("", "cmpl", "%eax,%ecx", "");
	}
	Code.genInstr("", "movl", "$0,%eax", "");
	switch (opToken) {
	case equalToken:        
	    Code.genInstr("", "sete", "%al", "Test ==");  break;
	case notEqualToken:
	    Code.genInstr("", "setne", "%al", "Test !=");  break;
	case lessToken:
	    Code.genInstr("", "setl", "%al", "Test <");  break;
	case lessEqualToken:
	    Code.genInstr("", "setle", "%al", "Test <=");  break;
	case greaterToken:
	    Code.genInstr("", "setg", "%al", "Test >");  break;
	case greaterEqualToken:
	    Code.genInstr("", "setge", "%al", "Test >=");  break;
	}
    }

    @Override void parse() {
	Log.enterParser("<rel operator>");

	opToken = Scanner.curToken;
	Scanner.readNext();

	Log.leaveParser("</rel operator>");
    }

    @Override void printTree() {
	String op = "?";
	switch (opToken) {
	case equalToken:        op = "==";  break;
	case notEqualToken:     op = "!=";  break;
	case lessToken:         op = "<";   break;
	case lessEqualToken:    op = "<=";  break;
	case greaterToken:      op = ">";   break;
	case greaterEqualToken: op = ">=";  break;
	}
	Log.wTree(" " + op + " ");
    }
}


/*
 * An <operand>
 */
abstract class Operand extends SyntaxUnit {
    Operand nextOperand = null;
    Type valType;
}


/*
 * A <function call>.
 */
class FunctionCall extends Operand {
    //-- Must be changed in part 1+2:
    ExprList e = new ExprList();
    String name;
    
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {
	Log.enterParser("<function call>");
	
	name = Scanner.curName;
	Scanner.skip(nameToken);
	Scanner.skip(leftParToken);
	e.parse();
	Scanner.skip(rightParToken);
	
	Log.leaveParser("</function call>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree(name);
	Log.wTree("(");

	e.printTree();

	Log.wTree(")");
    }
    //-- Must be changed in part 1+2:
}


/*
 * A <number>.
 */
class Number extends Operand {
    int numVal;

    Number() {}
    
    Number(int n) {
	numVal = n;
    }

    Number(double n) {
	n = (double)numVal;
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
    }
	
    @Override void genCode(FuncDecl curFunc) {
	Code.genInstr("", "movl", "$"+numVal+",%eax", ""+numVal); 
    }
    
    @Override void parse() {
	Log.enterParser("<operand>");
	Log.enterParser("<number>");
	
	//Scanner.readNext();
	
	//System.err.println("Number class: curToken: " + Scanner.curToken + " curName: " + Scanner.curName);
	
	// if(Scanner.curToken == numberToken) {
	if(Scanner.curToken == subtractToken) {
	    // skip '-' infront of a number
	    Scanner.skip(subtractToken); 
	}
	else if(Scanner.curToken == commaToken) {
	    Scanner.skip(commaToken);
	}
	
	numVal = Scanner.curNum;
	Scanner.skip(numberToken);

	if(Scanner.curToken == commaToken) {
	    Scanner.skip(commaToken);
	}
	//Scanner.skip(rightParToken);
	//Scanner.skip(semicolonToken);
	// } else {
	//     System.err.println("Dafuq are u giving me a " + Scanner.curToken + " instead of a numberToken for");
	// }

	Log.leaveParser("</number>");
	Log.leaveParser("</operand>");
    }

    @Override void printTree() {
	Log.wTree("" + numVal);
    }
    
    public int getINum() {
	return numVal;
    }
    public double getDNum() {
	return (double)numVal;
    }
    public void setNum(int n) {
	numVal = n;
    }
    public void setNum(double d) {
	d = (double)numVal;
    }
}


/*
 * A <variable>.
 */

class Variable extends Operand {
    String varName;
    VarDecl declRef = null;
    Expression index = null;

    @Override void check(DeclList curDecls) {
	Declaration d = curDecls.findDecl(varName,this);
	if (index == null) {
	    d.checkWhetherSimpleVar(this);
	    valType = d.type;
	} else {
	    d.checkWhetherArray(this);
	    index.check(curDecls);
	    index.valType.checkType(lineNum, Types.intType, "Array index");
	    valType = ((ArrayType)d.type).elemType;
	}
	declRef = (VarDecl)d;
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
    }

    @Override void parse() {

	Log.enterParser("<operand>");
	Log.enterParser("<variable>");
	
	varName = Scanner.curName;
	//	System.err.println("DEBUG - variable: Scanner.curName: " + Scanner.curName);
	System.err.println("Variable about to skip nameToken");
	Scanner.skip(nameToken);
	System.err.println("Variable nameToken skipped");
	
	if(Scanner.curToken == leftBracketToken) {
	    Scanner.skip(leftBracketToken);
	    index = new Expression();
	    index.parse();
	    Scanner.skip(rightBracketToken);
	}

	Log.leaveParser("</variable>");
	Log.leaveParser("</operand>");
	//-- Must be changed in part 1:
    
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree(varName);
	if(index != null) {
	    Log.wTree("[");
	    index.printTree();
	    Log.wTree("]");
	}
    }
}
