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
	//System.out.println("Syntax initiating");

	library = new GlobalDeclList();
	FuncDecl intExit = new FuncDecl("exit");
	intExit.type = Types.intType;
	intExit.lineNum = 0;
	intExit.visible = true;
	intExit.pdl = new ParamDeclList();
	intExit.pdl.vd = new ParamDecl("status");
	intExit.pdl.vd.type = Types.intType;
	intExit.pdl.addDecl(intExit.pdl.vd);
	// manually increase size because we dont call pdl.parse
	intExit.pdl.size = 1;

	FuncDecl intGetchar = new FuncDecl("getchar");
	intGetchar.type = Types.intType;
	intGetchar.lineNum = 0;
	intGetchar.visible = true;
	intGetchar.pdl = new ParamDeclList();

	FuncDecl doubleGetdouble = new FuncDecl("getdouble");
        doubleGetdouble.type = Types.doubleType;
	doubleGetdouble.lineNum = 0;
	doubleGetdouble.visible = true;
	doubleGetdouble.pdl = new ParamDeclList();

	FuncDecl intGetint = new FuncDecl("getint");
	intGetint.type = Types.intType;
	intGetint.lineNum = 0;
	intGetint.visible = true;
	intGetint.pdl = new ParamDeclList();

	FuncDecl intPutchar = new FuncDecl("putchar");
	intPutchar.type = Types.intType;
	intPutchar.lineNum = 0;
	intPutchar.visible = true;
	intPutchar.pdl = new ParamDeclList();
	intPutchar.pdl.vd = new ParamDecl("c");
	intPutchar.pdl.vd.type = Types.intType;
	intPutchar.pdl.addDecl(intPutchar.pdl.vd);
	intPutchar.pdl.size = 1;

	FuncDecl doublePutdouble = new FuncDecl("putdouble");
	doublePutdouble.type = Types.doubleType;
	doublePutdouble.lineNum = 0;
	doublePutdouble.visible = true;
	doublePutdouble.pdl = new ParamDeclList();
	doublePutdouble.pdl.vd = new ParamDecl("c");
	doublePutdouble.pdl.vd.type = Types.doubleType;
	doublePutdouble.pdl.addDecl(doublePutdouble.pdl.vd);
	doublePutdouble.pdl.size = 1;

	FuncDecl intPutint = new FuncDecl("putint");
	intPutint.type = Types.intType;
	intPutint.lineNum = 0;
	intPutint.visible = true;
	intPutint.pdl = new ParamDeclList();
	intPutint.pdl.vd = new ParamDecl("c");
	intPutint.pdl.vd.type = Types.intType;
	intPutint.pdl.addDecl(intPutint.pdl.vd);
	intPutint.pdl.size = 1;

	library.addDecl(intExit);
	library.addDecl(intGetchar);
	library.addDecl(doubleGetdouble);
	library.addDecl(intGetint);
	library.addDecl(intPutchar);
	library.addDecl(doublePutdouble);
	library.addDecl(intPutint);
    }

    public static void finish() {
	//-- Must be changed in part 1:
	//System.out.println("Syntax finished");
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
	    Declaration tmp = progDecls.firstDecl;
	    while (tmp != null) {
		if(tmp.name.equals("main")) {
		    tmp.type.checkType(tmp.lineNum, Types.intType, tmp.name);
		    Log.noteBinding(tmp.name, tmp.lineNum, tmp.lineNum);
		    return;
		}
		
	    	tmp = tmp.nextDecl;
	    }
	    Error.error("Name main is unknown!");
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
	    //System.out.println("FUCKOFF!");
	    dx.check(this);  dx = dx.nextDecl;
	}
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
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
	
	Declaration tmp = checkForDuplicate(d);
	if(tmp != null) {
	    Error.error(d.lineNum, d.name + " is already declared in line " + tmp.lineNum);
	}

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
	
	DeclList tmpDecl = this;
	while(tmpDecl != null) {
	    Declaration tmp = tmpDecl.firstDecl;
	    //System.out.println(tmp.name);
	    while(tmp != null) {
		//System.out.println("tmp." + tmp.name + ".equals(" + name + ")");
		if(tmp.name.equals(name) && tmp.visible) {
		    /*binding*/
		    Log.noteBinding(name, usedIn.lineNum, tmp.lineNum);
		    return tmp;
		}
		tmp = tmp.nextDecl;
	    }
	    tmpDecl = tmpDecl.outerScope;
	}
	
	Error.error(usedIn.lineNum, "cannot find declaration " + name);
	return null;
    }
    
    Declaration checkForDuplicate(Declaration d) {
	Declaration tmp = firstDecl;

	while(tmp != null) {
	    if(tmp.name.equals(d.name)) {
		return tmp;
	    }
	    tmp = tmp.nextDecl;
	}
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
	Declaration tmp = firstDecl;

	while(tmp != null) {
	    tmp.genCode(curFunc);
	    tmp = tmp.nextDecl;
	}
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
}


/*
 * A list of local declarations. 
 * (This class is not mentioned in the syntax diagrams.)
 */
class LocalDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	if(firstDecl == null) {
	    return; // if no decls, no genCode
	}

	int offset = 0;
    	Declaration tmp = firstDecl;

    	while(tmp != null) {
	    if(tmp instanceof LocalArrayDecl) {
		offset = offset + (((LocalArrayDecl)tmp).at.elemType.size() * ((LocalArrayDecl)tmp).at.nElems);
	    }
	    else {
		offset = offset + tmp.type.size();
	    }
	    tmp.assemblerName = "-" + offset + "(%ebp)";
	    //tmp.genCode(curFunc);
	    //Code.genInstr("", "subl", "$" + dataSize() + ", %esp", "Get " + dataSize() + " bytes local data space");
    	    tmp = tmp.nextDecl;
    	}
	
	if(dataSize() > 0) {
	    Code.genInstr("", "subl", "$" + dataSize() + ",%esp", "Get " + dataSize() + " bytes local data space");
	}
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
    int size;
    VarDecl vd = null;
    
    public ParamDeclList() {
	size = 0;
    }
    
    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:

	if(firstDecl == null) {
	    return; // if no decls, no genCode
	}
	
	int offset = 8;
    	Declaration tmp = firstDecl;
	
    	while(tmp != null) {
	    tmp.assemblerName = offset + "(%ebp)";
	    offset = offset + tmp.type.size();
    	    tmp = tmp.nextDecl;
    	}
	
	// if(dataSize() > 0) {
	//     Code.genInstr("", "subl", "$" + dataSize() + ", %esp", "Get " + dataSize() + " bytes local data space");
	// }
    }
    
    public int nParamsUsed() {
	return size;
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	// once we reach ')' we know that the param decl is over
	while (Scanner.curToken != rightParToken) {	    
	    if(Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }

	    vd = new ParamDecl(Scanner.nextName);
	    vd.parse();
	    addDecl(vd);
	    size++; // increase list size for each element add to list
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
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
    }

    //-- Must be changed in part 1+2:
    
    void genStoreArray(Type valType) {
	//System.out.println("in VarDecl.genStoreArray: type = " + type.typeName() + " and valType: " + valType.typeName());
	//System.out.println("\nin VarDecl.genStoreArray: assemblername " + assemblerName + "\tname " + name);
	if(type == Types.doubleType && valType == Types.doubleType) { // fa[ie] = fe
	    Code.genInstr("", "leal", assemblerName + ",%edx", "");
	    Code.genInstr("", "popl", "%ecx", "");
	    Code.genInstr("", "fstpl", "(%edx,%ecx,8)", name + "[...] =");
	}
	else if(type == Types.doubleType && valType == Types.intType) { // fa[ie1] = ie2
	    Code.genInstr("", "leal", assemblerName + ",%edx", "");
	    Code.genInstr("", "popl", "%ecx", "");
	    Code.genInstr("", "movl", "%eax," + Code.tmpLabel, "");
	    Code.genInstr("", "fildl", Code.tmpLabel, "  (double)");
	    Code.genInstr("", "fstpl", "(%edx,%ecx,8)", name + "[...] =");
	}
	else if(type == Types.intType && valType == Types.doubleType) { // ia[ie] = fe
	    Code.genInstr("", "leal", assemblerName + ",%edx", "");
	    Code.genInstr("", "popl", "%ecx", "");
	    Code.genInstr("", "fistpl", "(%edx,%ecx,4)", name + "[...] =");
	}
	else if(type == Types.intType && valType == Types.intType) { // ia[ie1] = ie2
	    Code.genInstr("", "leal", assemblerName + ",%edx", "");
	    Code.genInstr("", "popl", "%ecx", "");
	    Code.genInstr("", "movl", "%eax,(%edx,%ecx,4)", name + "[...] =");
	}
	else {
	    Error.panic("Decleration.genStoreArray");
	}
    }
    
    void genStore(Type valType) {
	//System.out.println("in VarDecl.genStore: type = " + type + " and valType: " + valType);
	if(type == Types.doubleType && valType == Types.doubleType) {
	    Code.genInstr("", "fstpl", assemblerName, name + " =");
	}
	else if(type == Types.doubleType && valType == Types.intType) {
	    Code.genInstr("", "movl", "%eax," + Code.tmpLabel, "");
	    Code.genInstr("", "fildl", Code.tmpLabel, "  (double)");
	    Code.genInstr("", "fstpl", assemblerName, name + " =");
	}
	else if(type == Types.intType && valType == Types.doubleType) {
	    Code.genInstr("", "fistpl", assemblerName, name + " = (int)");
	}
	else if(type == Types.intType && valType == Types.intType) {
	    Code.genInstr("", "movl", "%eax," + assemblerName, name + " =");
	}
	else {
	    Error.panic("Decleration.genStore");
	}
    }
}


/*
 * A global array declaration
 */
class GlobalArrayDecl extends VarDecl {
    ArrayType at = null;
    //Type type = null;

    GlobalArrayDecl(String n) {
	super(n);
	assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override void check(DeclList curDecls) {
	//System.out.println("\nin GlobalArrayDecl.check");
	visible = true;
	// if (((ArrayType)type).nElems < 0)
	//     Syntax.error(this, "Arrays cannot have negative size!");
	// had to change the original code to this
	if (at.nElems < 0) {
	    Syntax.error(this, "Arrays cannot have negative size!");
	}
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	/* OK */
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	Syntax.error(use, name + " is an array and no simple variable!");
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	// // perhaps add \n to the end for nicer format
	Code.genVar(assemblerName, true, at.size(), name + "[" + at.nElems + "];");
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
        //Type tmp = Types.getType(Scanner.curToken);
	type = Types.getType(Scanner.curToken);
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(leftBracketToken); // skip '['
	//at = new ArrayType(Scanner.curNum, tmp);
	at = new ArrayType(Scanner.curNum, type);
	Scanner.skip(numberToken); // skip numberToken
	Scanner.skip(rightBracketToken); // skip ']'
	Scanner.skip(semicolonToken); // skip ';'

	Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:	
	Log.wTree(at.elemType.typeName() + " " + name + "[" + at.nElems + "]");
	Log.wTreeLn(";");
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
	//System.out.println("\nin GlobalSimpleVarDecl.check");
	visible = true;
	/*noting else to do*/
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
	Syntax.error(use, name + " is a simple variable and no array!");
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	/* OK */
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	// perhaps add \n to the end for nicer format
	Code.genVar(assemblerName, true, declSize(), type.typeName() + " " + name + ";");
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
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
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
    
    // had to override var
    @Override int declSize() {
	return at.size();
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	//System.out.println("in LocalArrayDecl.check");
	visible = true;
	if (at.nElems < 0) {
	    Syntax.error(this, "Arrays cannot have negative size!");
	}
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
	/* OK */
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
	Syntax.error(use, name + " is an array and no simple variable!");
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	Code.genVar(assemblerName, false, at.size(), name + "[" + at.nElems + "];");
    }

    @Override void parse() {
	Log.enterParser("<var decl>");

	//-- Must be changed in part 1:
        //Type t = Types.getType(Scanner.curToken);
	type = Types.getType(Scanner.curToken);
	//System.out.println("\nin LocalArrayDecl.parse: " + type + " type name" + type.typeName());
	Scanner.readNext(); // read past int- or double-Token
	Scanner.skip(nameToken); // skip the name
	Scanner.skip(leftBracketToken); // skip '['	
	//at = new ArrayType(Scanner.curNum, t);
	at = new ArrayType(Scanner.curNum, type);
	Scanner.skip(numberToken); // skip numberToken
	Scanner.skip(rightBracketToken); // skip ']'
	Scanner.skip(semicolonToken); // skip ';'
	
	Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree(at.elemType.typeName() + " " + name + "[" + at.nElems + "]");
	Log.wTreeLn(";");
    }

}


/*
 * A local simple variable declaration
 */
class LocalSimpleVarDecl extends VarDecl {
    
    LocalSimpleVarDecl(String n) {
	super(n); 
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	//System.out.println("\nin LocalSimpleVarDecl.check");
	visible = true;
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
	Syntax.error(use, name + " is a simple variable and no array!");
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
	/* OK */
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	Code.genVar(assemblerName, false, declSize(), type.typeName() + " " + name + ";");
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
	Log.wTree(type.typeName() + " " + name);
	Log.wTreeLn(";");
    }
}


/*
 * A <param decl>
 */
class ParamDecl extends VarDecl {

    ParamDecl(String n) {
	super(n);
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	//System.out.println("\nin ParamDecl.check");
	visible = true;
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
	// params can not be arrays
	Syntax.error(use, name + " is a parameter declaration and can only be a variable, not an array!");
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
	/* OK */
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	/*nothing to do here */
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
	//System.out.println("\nin FuncBody.check");
	ldl.check(curDecls);
	sl.check(ldl);
    }

    @Override void genCode(FuncDecl curFunc) {
	// not sure if right
	ldl.genCode(curFunc);
	sl.genCode(curFunc);
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
	//System.out.println("\nin FuncDecl.check");
	//-- Must be changed in part 2:
	if(name.equals("main") && pdl.firstDecl != null) {
	    Syntax.error(this, "main should be declared without parameters");
	}
	else {
	    visible = true;
	    pdl.check(curDecls);
	    fb.check(pdl);
	}
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
	//-- Must be changed in part 2:
	Syntax.error(use, name + " is a function declaration and not an array!");
    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
	//-- Must be changed in part 2:
	//System.out.println(name + "\tnParam\t" + nParamsUsed + "\tnParams()\t" + pdl.nParamsUsed());
	if(nParamsUsed != pdl.nParamsUsed()) {
	    Syntax.error(use,"Function " + name + " is not declared properly");
	}
    }
	
    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
	//-- Must be changed in part 2:
	Syntax.error(use, name + " is a function declaration and not a simple variable!");
    }

    @Override void genCode(FuncDecl curFunc) {
	//System.err.println("\nin FuncDecl.genCode: name = " + name);
	Code.genInstr("", ".globl", assemblerName, "");
	Code.genInstr(assemblerName, "pushl", "%ebp", "Start function "+name);
	Code.genInstr("", "movl", "%esp,%ebp", "");
	//-- Must be changed in part 2:
	// pdl.genCode(curFunc); // params should already be on the stack
	// fb.genCode(curFunc);
	pdl.genCode(this);
	fb.genCode(this);
	Code.genInstr(".exit$" + assemblerName, "movl", "%ebp,%esp", "");
	Code.genInstr("", "popl", "%ebp", "");
	Code.genInstr("", "ret", "", "End function " + name);
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
	Log.wTree(type.typeName() + " " + name);
	Log.wTree(" ("); pdl.printTree(); Log.wTreeLn(")");
	Log.wTreeLn("{");
	fb.printTree();
	Log.wTreeLn("}");
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
	Statement tmp = firstStatm;
	while(tmp != null) {
	    tmp.check(curDecls);
	    tmp = tmp.nextStatm;
	}
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	Statement tmp = firstStatm;
	while(tmp != null) {
	    //System.out.println("\n" + tmp);
	    tmp.genCode(curFunc);
	    tmp = tmp.nextStatm;
	}
    }

    @Override void parse() {
	Log.enterParser("<statm list>");

	Statement lastStatm = null;
	while (Scanner.curToken != rightCurlToken) {
	    Log.enterParser("<statement>");
	    //-- Must be changed in part 1:
	    Statement s = Statement.makeNewStatement();
	    s.parse();

	    if(firstStatm == null) {
	     	firstStatm = s;
	     	lastStatm = firstStatm;
	    } else {
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
	fc.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	fc.genCode(curFunc);
	
    }

    @Override void parse() {
	Log.enterParser("<call-statm>");

	fc =  new FunctionCall();
	fc.parse();
	Scanner.skip(semicolonToken);

	Log.leaveParser("</call-statm>");
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	fc.printTree();
	Log.wTreeLn(";");
    }
}


/*
 * An Assign statement
 */
class AssignStatm extends Statement {
    //-- Must be changed in part 1+2:
    Assignment a = new Assignment();
  
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	a.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	a.genCode(curFunc);
    }

    @Override void parse() {
	// -- Must be changed in part 1:
	Log.enterParser("<assign-statm>");
       
	a.parse();
	Scanner.skip(semicolonToken);

	Log.leaveParser("</assign-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	a.printTree();
	Log.wTreeLn(";");
    }
}

/*
 * An Assignment
 */
class Assignment extends SyntaxUnit {
    Expression test = null;
    Variable v = null;
  
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	v.check(curDecls);
	test.check(curDecls);	
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:

	if(v.index != null) { // var is an array
	    //v.genCode(curFunc);
	    v.index.genCode(curFunc);
	    Code.genInstr("", "pushl", "%eax", "");
	    test.genCode(curFunc);
	    v.declRef.genStoreArray(test.valType); 
	}
	else { // var is not an array
	    test.genCode(curFunc);
	    v.declRef.genStore(test.valType);
	}   	
    }

    @Override void parse() {
	// -- Must be changed in part 1:
	Log.enterParser("<assignment>");
	
	v = new Variable();
	v.parse();
	Scanner.skip(assignToken);
	test = new Expression();
	test.parse();

	Log.leaveParser("</assignment>");	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	v.printTree();
	Log.wTree(" = ");
	test.printTree();
    }
}

/*
 * An <empty statm>.
 */
class EmptyStatm extends Statement {
  
    @Override void check(DeclList curDecls) {
	// Nothing to do here 
    }

    @Override void genCode(FuncDecl curFunc) {
	// Nothing to do here 
	//Code.genInstr("Empty", "", "", "This is an empty statement :-)");
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	Log.enterParser("<empty-statm>");
	
	Scanner.skip(semicolonToken);

	Log.leaveParser("</empty-statm>");
    }

    @Override void printTree() {
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
    StatmList body = new StatmList();
    ForControl fc = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	fc.check(curDecls);
	body.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
	//fc.genCode(curFunc);
	String testLabel = Code.getLocalLabel(), 
	    endLabel  = Code.getLocalLabel();
	
	fc.a.genCode(curFunc);
	Code.genInstr(testLabel, "", "", "Start for-statement");
	fc.e.genCode(curFunc);
	fc.e.valType.genJumpIfZero(endLabel);
	body.genCode(curFunc);
	fc.as.genCode(curFunc);
	Code.genInstr("", "jmp", testLabel, "");
	Code.genInstr(endLabel, "", "", "End for-statement");
    }

    @Override void parse() {
	// -- Must be changed in part 1:
	// Make same shit for else statm
	Log.enterParser("<for-statm>");
	
	Scanner.readNext();
	Scanner.skip(leftParToken);
	fc = new ForControl();
	fc.parse();	
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	body.parse();
	Scanner.skip(rightCurlToken);

	Log.leaveParser("</for-statm>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Log.wTree("for (");  fc.printTree();  Log.wTreeLn(") {");
	Log.indentTree();
	body.printTree();
	Log.outdentTree();
	Log.wTreeLn("}");
    }
}

class ForControl extends ForStatm {
    Assignment a = null;
    Expression e = null;
    Assignment as = null;
    
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	a.check(curDecls);
	e.check(curDecls);
	as.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
	// String testLabel = Code.getLocalLabel(), 
	//     endLabel  = Code.getLocalLabel();
	
	// a.genCode(curFunc);
	// Code.genInstr(testLabel, "", "", "Start for-statement");
	// e.genCode(curFunc);
	// as.genCode(curFunc);
	// e.valType.genJumpIfZero(endLabel);
	// Code.genInstr("", "jmp", testLabel, "");
	// Code.genInstr(endLabel, "", "", "End for-statement");
    }
    
    @Override void parse() {
	a = new Assignment();
	a.parse();
	Scanner.skip(semicolonToken);
	
	e = new Expression();
	e.parse();
	Scanner.skip(semicolonToken);
	
	as = new Assignment();
	as.parse();
    }   
    @Override void printTree() {
	//-- Must be changed in part 1:
	a.printTree();
	Log.wTree("; ");
	e.printTree();
	Log.wTree("; ");
	as.printTree();
    }
  
}

/*
 * An <if-statm>.
 */
class IfStatm extends Statement {
    //-- Must be changed in part 1+2:
    // en mulighet if-else peker
    Expression test = null;
    StatmList body = null;
    ElsePart e = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	test.check(curDecls);
	body.check(curDecls);
	if(e != null) {
	    e.check(curDecls);
	}
    }

    @Override void genCode(FuncDecl curFunc) {

	//test.genCode(curFunc);

	if(e == null) {
	    Code.genInstr("", "", "", "Start if-statement");
	    String lab = Code.getLocalLabel();
	    test.genCode(curFunc);
	    test.valType.genJumpIfZero(lab);
	    body.genCode(curFunc);
	    Code.genInstr(lab, "", "", "End if-statement");
	}
	else {
	    Code.genInstr("", "", "", "Start if-statement");
	    // String lab1 = Code.getLocalLabel();
	    // String lab2 = Code.getLocalLabel();
	    String lab2 = Code.getLocalLabel();
	    String lab1 = Code.getLocalLabel();
	    test.genCode(curFunc);
	    test.valType.genJumpIfZero(lab1);
	    body.genCode(curFunc);
	    Code.genInstr("", "jmp", lab2, "");
	    Code.genInstr(lab1, "", "", "  else-part");
	    e.genCode(curFunc);
	    Code.genInstr(lab2, "", "", "End if-statement");
	}

	//String lab1, lab2;
	//String ifLabel  = Code.getLocalLabel(); // lab 1
	//String lab2 = Code.getLocalLabel(); // lab 2

	// test.genCode(curFunc);
	// if(e != null) {
	//     test.valType.genJumpIfZero(elseLabel);
	// }
	// else {
	//     lab1 = Code.getLocalLabel();
	//     test.valType.genJumpIfZero(lab1);
	// }
	// body.genCode(curFunc);
	// //Code.genInstr("", "jmp", ifLabel, "");
	// if(e != null) {
	//     Code.genInstr("", "jmp", elseLabel, "");
	//     Code.genInstr(elseLabel, "", "", "Start else-part");
	//     e.genCode(curFunc);
	// }
    }

    @Override void parse() {
	//-- Must be changed in part 1:
	Log.enterParser("<if-statm>");
	
	Scanner.readNext();
	Scanner.skip(leftParToken);
	test = new Expression();
	test.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	body = new StatmList();
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
    StatmList body = null;
    
    @Override void check(DeclList curDecls) {
	body.check(curDecls);
    }
    
    @Override void genCode(FuncDecl curFunc) {
	// String elseLabel = Code.getLocalLabel();
	// Code.genInstr(elseLabel, "", "", "Start else-part");
	body.genCode(curFunc);
	//Code.genInstr("", "jmp", elseLabel, "");
	//Code.genInstr(elseLabel, "", "", "End else-part");
    }

    @Override void parse() {
	Log.enterParser("<else-part>");
	
	Scanner.skip(elseToken);
	Scanner.skip(leftCurlToken);
	body = new StatmList();
	body.parse();
	Scanner.skip(rightCurlToken);
	
	Log.leaveParser("</else-part>");
    }

    @Override void printTree() {
	Log.wTreeLn("else {");  
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
	test.check(curDecls);
	System.out.println("\nin return: " + curDecls);
	// if() {
	//     Syntax.error(this, "Return value should be " + ", not ");
	// }
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	test.genCode(curFunc);
	Code.genInstr("", "jmp", ".exit$" + curFunc.name, "Return-statement");
	// test.genCode(curFunc);
	// Code.genInstr("", "jmp", endLabel, "jump to exit");
	// Code.genInstr(testLabel, "", "", "End return-statement");
    }

    @Override void parse() {
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
 * An <expression list>.
 */

class ExprList extends SyntaxUnit {
    Expression firstExpr = null;
    Expression lastExpr = null;
    Expression e = null;
    int size;
    
    public ExprList() {
	size = 0;
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	Expression tmp = firstExpr;
	
	while(tmp != null) {
	    tmp.check(curDecls);
	    tmp = tmp.nextExpr;
	}
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	Expression tmp = firstExpr;
	int paramNum = 1;
	
	if(firstExpr != null) {
	    pushRecursive(firstExpr, paramNum, curFunc);
	    Code.genInstr("", "call", curFunc.name, "Call " + curFunc.name);
	    // only remove params if any params were used
	    Code.genInstr("", "addl", "$" + curFunc.pdl.dataSize() + ",%esp", "Remove parameters");
	}
	else {
	    Code.genInstr("", "call", curFunc.name, "Call " + curFunc.name);
	}
    }
    
    @Override void parse() {

	Log.enterParser("<expr list>");
	int exprNum = 0;
	while(Scanner.curToken != rightParToken) {
	    
	    e = new Expression();
	    e.parse();
	    
	    if(firstExpr == null) {
	     	firstExpr = e;
	     	lastExpr = firstExpr;		
	    } else {
		lastExpr.nextExpr = e;
		lastExpr = e;
	    }
	    e.exprListNum++;
	    size++;

	    if(Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }
	}
	
	Log.leaveParser("</expr list>");

    }

    @Override void printTree() {
	//-- Must be changed in part 1:
	Expression tmp = firstExpr;
	while(tmp != null) {
	    tmp.printTree();
	    if(tmp.nextExpr != null) {
		Log.wTree(", ");
	    }
	    tmp = tmp.nextExpr;
	}
    }
    //-- Must be changed in part 1:
    
    int getNumParams() {
	return size;
    }
    
    void pushRecursive(Expression cur, int paramNum, FuncDecl curFunc) {
    	if(cur.nextExpr != null) {
    	    pushRecursive(cur.nextExpr, paramNum + 1, curFunc);
    	}

    	cur.genCode(curFunc);
	//if(curFunc.type.typeName().equals(Types.doubleType.typeName())) {
	if(cur.valType.typeName().equals(Types.doubleType.typeName())) {
	    Code.genInstr("", "subl", "$8,%esp", "");
	    Code.genInstr("", "fstpl", "(%esp)", "Push parameter #" + paramNum);
	}
	else {
	    Code.genInstr("", "pushl", "%eax", "Push parameter #" + paramNum);
	}
    }
}

/*
 * An <expression>
 */
class Expression extends Operand {
    Expression nextExpr = null;
    Term firstTerm = new Term(), secondTerm = null;
    Operator relOp = null;
    boolean innerExpr = false;
    int exprListNum = 0;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	firstTerm.check(curDecls);
	if(relOp != null) {
	    secondTerm.check(curDecls);
	    
	    if(secondTerm.type == firstTerm.type) {
		valType = firstTerm.type;
		relOp.opType = valType;
		System.out.println(relOp.opType.typeName());
	    }
	    else {
		Syntax.error(this, "only same type arithmetic is allowed");
	    }
	}
	else {
	    valType = firstTerm.type;
	}	
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	
	firstTerm.genCode(curFunc);
	if(relOp != null) {
	    if (relOp.opType == Types.doubleType) 
		if (relOp.opType.typeName().equals(Types.doubleType.typeName())) {
		    //Code.genInstr("", "subl", "8(%esp)", "");
		    Code.genInstr("", "subl", "$8,%esp", "");
		    Code.genInstr("", "fstpl", "(%esp)", "");
		} 
		else {
		    Code.genInstr("", "pushl", "%eax", "");
		}

	    secondTerm.genCode(curFunc);
	    relOp.genCode(curFunc);
	}
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

class Opr extends Operator {
    Opr nextOpr = null;
    String op;
    
    Opr(String o) {
	op = o;
	opToken = Scanner.curToken;
    }
    
    public String getOpr() {
	return op;
    }

    public void setOpr(String newO) {
	op = newO;
    }

    @Override void check(DeclList curDecls) {
    }
    
    @Override void genCode(FuncDecl curFunc) {
	if (opType == Types.doubleType) {
	    // Code.genInstr("", "subl", "$8,%esp", "");
	    // Code.genInstr("", "fstpl", "(%esp)", "");
	    Code.genInstr("", "fldl", "(%esp)", "");
	    Code.genInstr("", "addl", "$8,%esp", "");
	    /*might be wrong*/
	    switch (opToken) {
	    case addToken:
		// Code.genInstr("", "fldl", "(%esp)", "");
		// Code.genInstr("", "addl", "$8,(%esp)", "");
		Code.genInstr("", "faddp", "", "Compute +");
		break;
	    case subtractToken:
		// Code.genInstr("", "fldl", "(%esp)", "");
		// Code.genInstr("", "addl", "$8,(%esp)", "");
		Code.genInstr("", "fsubp", "", "Compute -");
		break;
	    case multiplyToken:  
      		// Code.genInstr("", "fldl", "(%esp)", "");
		// Code.genInstr("", "addl", "$8,(%esp)", "");
		Code.genInstr("", "fmulp", "", "Compute *");
		break;
	    case divideToken:
      		// Code.genInstr("", "fldl", "(%esp)", "");
		// Code.genInstr("", "addl", "$8,(%esp)", "");
		//Code.genInstr("", "cdq", "", "");
		Code.genInstr("", "fdivp", "", "Compute /");
		break;
	    }
	}
	else {
	    Code.genInstr("", "movl", "%eax,%ecx", "");
	    Code.genInstr("", "popl", "%eax", "");
	
	    switch (opToken) {
	    case addToken:        
		Code.genInstr("", "addl", "%ecx,%eax", "Compute +");
		break;
	    case subtractToken:
		Code.genInstr("", "subl", "%ecx,%eax", "Compute -");
		break;
	    case multiplyToken:        
		Code.genInstr("", "imull", "%ecx,%eax", "Compute *");
		break;
	    case divideToken:
		Code.genInstr("", "cdq", "", "");
		Code.genInstr("", "idivl", "%ecx", "Compute /");
		break;
	    }
	}	    
    }
    
    @Override void parse() {
        op = Scanner.curName;
 	opToken = Scanner.curToken; // save the token
    }
    
    @Override void printTree() {
	Log.wTree(" " + op + " ");
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
   
    FactorList fl = null;
    OprList tol = null;
    Factor f = null;
    Opr t = null; 
    Type type = null;

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	fl.check(curDecls);
	type = fl.type;
	Opr otmp = tol.firstOpr;

	while(otmp != null) {
	    otmp.opType = type;
	    otmp = otmp.nextOpr;
	}
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	Factor ftmp = fl.firstFactor;
	Opr otmp = tol.firstOpr;
	
	ftmp.genCode(curFunc);
	while(otmp != null) {
	    if(otmp.opType == null) {
		System.err.println("\nin Term.genCode: otmp.opType is null");
	    }
	    //if(otmp.opType == Types.doubleType) { // might be wrong////////////////////////////////////////////////////
	    if(otmp.opType.typeName().equals(Types.doubleType.typeName())) {
		//Code.genInstr("", "subl", "8(%esp)", "");
		Code.genInstr("", "subl", "$8,%esp", "");
		Code.genInstr("", "fstpl", "(%esp)", "");
	    }
	    else {
		Code.genInstr("", "pushl", "%eax", "");	
	    }
	    
	    if(ftmp.nextFactor != null) {
		ftmp = ftmp.nextFactor;
		ftmp.genCode(curFunc);
	    }

	    otmp.genCode(curFunc);
	    otmp = otmp.nextOpr;
	}
    }

    @Override void parse() {
	//-- Must be changed in part 1: 

	Log.enterParser("<term>");
	
	fl = new FactorList();
	tol = new OprList();
	
	do {	
	    if(Scanner.curToken == addToken) {
		TermOpr to = new TermOpr("+");
		tol.add(to);
		Scanner.skip(addToken);
		
	    } else if(Scanner.curToken == subtractToken) {
		// tmp solution until support for negativ number
		//} else if(Scanner.curToken == subtractToken && Scanner.nextToken != subtractToken) {
		TermOpr to = new TermOpr("-");
		tol.add(to);
		Scanner.skip(subtractToken);
	    }
	    // if(Scanner.curToken == subtractToken && Scanner.nextToken == subtractToken) {
	    // 	break; // tmp solution until support for negativ number
	    // }

	    f = new Factor();
	    f.parse();
	    fl.addFactor(f);    
	}
	while(isTermOperator(Scanner.curToken));

	if(isTermOperator(Scanner.curToken)) {
	    f = new Factor();
	    f.parse();
	    fl.addFactor(f);
	}

	Log.leaveParser("</term>");
	
    }

    @Override void printTree() {
	//-- Must be changed in part 1+2:
	int n = 1;
        Factor tmp = fl.getFactor(n);
	while(tmp != null) {
	    tmp.printTree();
	    if(tmp.nextFactor != null) {
		//	tol.printCorrect();
		Log.wTree(tol.getFirst().getOpr());
	    }
	    
	    n++;
	    tmp = fl.getFactor(n);
	}
    }
}

class FactorList extends SyntaxUnit {
    Factor firstFactor = null;
    Factor lastFactor = null;
    int counter = 1;
    int listSize = 0;
    Type type = null;

    @Override void check(DeclList curDecls) {
	Factor tmp = firstFactor;
	Type prevOprType = null;

	// first set type to firstFactor type
	// so that we have something to compare with
	if(tmp != null) {
	    tmp.check(curDecls);
	     type = tmp.type;
	     tmp = tmp.nextFactor;
	 }
	
	while(tmp != null) {
	    
	    tmp.check(curDecls);
	    //type = tmp.type;
	    prevOprType = tmp.type;

	    if(prevOprType != null && !type.typeName().equals(prevOprType.typeName())) {
		Syntax.error(tmp, "Illegal term causes type error");
	    }
	    
	    tmp = tmp.nextFactor;
	}
    }
    
    @Override void genCode(FuncDecl curFunc) {
	Factor tmp = firstFactor;
	
	while(tmp != null) {
	    tmp.genCode(curFunc);
	    tmp = tmp.nextFactor;
	}
    }

    @Override void parse() {
    }
    
    @Override void printTree() {
	Factor tmp = firstFactor;
	
	while(tmp != null) {
	    tmp.printTree();
	    tmp = tmp.nextFactor;
	}
    }

    public Factor getFactor(int n) {
	Factor tmp = firstFactor;
	
	while(tmp != null) {
	    //System.out.println("n = " + n + "  counter = " + counter);
	    if(counter == n) {
		break;
	    }

	    counter++;
	    tmp = tmp.nextFactor;
	}

	counter = 1; // reset counter
	return tmp;
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

class OperandList extends SyntaxUnit {
    Operand firstOperand = null;
    Operand lastOperand = null;
    int counter = 1;
    int listSize = 0;
    Type type = null;
    
    @Override void check(DeclList curDecls) {
	Operand tmp = firstOperand;
	Type prevOprType = null;
	
	// first set type to firstOprerand type
	// so that we have something to compare with
	if(tmp != null) {
	    tmp.check(curDecls);
	     type = tmp.valType;
	     //System.out.println("\nin operandlist: " + t.typeName());
	     tmp = tmp.nextOperand;
	 }
	
	while(tmp != null) {

	    tmp.check(curDecls);
	    //System.out.println(type.typeName() + "\tOperandList\t" + tmp.valType.typeName());
	    prevOprType = tmp.valType;
	
	if(prevOprType != null && !type.typeName().equals(prevOprType.typeName())) {
		Syntax.error(tmp, "Illegal factor causes type error");
	    }
	    
	    tmp = tmp.nextOperand;
	}
    }
    
    @Override void genCode(FuncDecl curFunc) {
	Operand tmp = firstOperand;
	
	while(tmp != null) {
	    tmp.genCode(curFunc);
	    tmp = tmp.nextOperand;
	}
    }

    @Override void parse() {
    }
    
    @Override void printTree() {
	Operand tmp = firstOperand;
	
	while(tmp != null) {
	    tmp.printTree();
	    tmp = tmp.nextOperand;
	}
    }
    
    public Operand getOpr(int n) {
	Operand tmp = firstOperand;
	
	while(tmp != null) {
	    //System.out.println("n = " + n + "  counter = " + counter);
	    if(counter == n) {
		//return tmp;
		break;
	    }
	    //System.err.println("is this a never ending loop?");
	    counter++;
	    tmp = tmp.nextOperand;
	}
	counter = 1;
	return tmp;
    }
    
    void addOperand(Operand o) {
	if(firstOperand == null) {
	    firstOperand = o;
	    lastOperand = firstOperand;
	    listSize++;	    
	} else {
	    lastOperand.nextOperand = o;
	    lastOperand = o;
	    listSize++;
	}
    }
}

class Factor extends SyntaxUnit  {
    Factor nextFactor = null;
    Operand o = null;
    OperandList ol = null;
    OprList fol = null;
    Opr fo = null;
    Type type = null;
    
    @Override void check(DeclList curDecls) {
	ol.check(curDecls);
	type = ol.type;
	Opr optmp = fol.firstOpr;

	while(optmp != null) {
	    optmp.opType = type;
	    optmp = optmp.nextOpr;
	}
    }

    @Override void genCode(FuncDecl curFunc) {
	// ol.genCode(curFunc);
	Operand otmp = ol.firstOperand;
	Opr optmp = fol.firstOpr;
	
	otmp.genCode(curFunc);
	while(optmp != null) {
	    if(optmp.opType == null) {
		System.err.println("\nin Factor.genCode: optmp.opType is null");
	    }
	    if(optmp.opType == Types.doubleType) {
		//Code.genInstr("", "subl", "8(%esp)", "");
		Code.genInstr("", "subl", "$8,%esp", "");
		Code.genInstr("", "fstpl", "(%esp)", "");
	    }
	    else {
		Code.genInstr("", "pushl", "%eax", "");	
	    }
	    
	    // otmp = otmp.nextOperand;
	    // otmp.genCode(curFunc);
	    // optmp.genCode(curFunc);

	    if(otmp.nextOperand != null) {
		otmp = otmp.nextOperand;
		otmp.genCode(curFunc);

		// if(optmp == null) {
		//     System.err.println("\nin Factor.genCode: optmp is null");
		// }
		optmp.genCode(curFunc);
	    }
	    // if(otmp.nextOperand == null) {
	    // 	System.err.println("\nin Factor.genCode: otmp.nextOperand is null");
	    // }
	    // if(optmp == null) {
	    // 	System.err.println("\nin Factor.genCode: optmp is null");
	    // }


	    optmp = optmp.nextOpr;
	}
    }

    @Override void parse() {
	
	Log.enterParser("<factor>");
	
	ol = new OperandList();
	fol = new OprList();
	
	do {	    
	    /*add and skip factor opr*/
	    if(Scanner.curToken == multiplyToken) {
		fo = new FactorOpr("*");
		fol.add(fo);
		Scanner.skip(multiplyToken);
	    }
	    else if(Scanner.curToken == divideToken) {
		fo = new FactorOpr("/");
		fol.add(fo);
		Scanner.skip(divideToken);
	    }

	    createOperand();
	    ol.addOperand(o); // add the operand to a the list
	}
	while(isFactorOperator(Scanner.curToken));
	
	if(isFactorOperator(Scanner.curToken)) {
	    // catch the last operand
	    createOperand();
	    ol.addOperand(o); // add the operand to a the list
	}
	
	Log.leaveParser("</factor>");
    }
    
    @Override void printTree() {
	int n = 1;
	Operand tmp = ol.getOpr(n);
	while(tmp != null) {
	    tmp.printTree();
	    if(tmp.nextOperand != null) {
		//	fol.printCorrect();
		Log.wTree(fol.getFirst().getOpr());
	    }
	    
	    n++;
	    tmp = ol.getOpr(n);
	}
    }
    
    private void createOperand() {
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
    FuncDecl declRef = null;
    
    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	e.check(curDecls);
	Declaration d = curDecls.findDecl(name, this);	
	d.checkWhetherFunction(e.getNumParams(), this);
	valType = d.type;
	declRef = (FuncDecl)d;
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:
	e.genCode(declRef);
	if(valType.typeName().equals(Types.doubleType.typeName())) {
	    Code.genInstr("", "fstps", Code.tmpLabel, "Remove return value.");
	}	
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
    //VarDecl declRef = null;

    Number() {}
    
    Number(int n) {
	numVal = n;
    }
    
    public int getNumVal() {
	return numVal;
    }

    @Override void check(DeclList curDecls) {
	//-- Must be changed in part 2:
	//Declaration d = curDecls.findDecl(varName,this);
	//System.out.println("in number.check\t" + numVal);
	valType = Types.intType; // number is always int
	//System.out.println("in number.check\t" + valType.typeName());
    }
	
    @Override void genCode(FuncDecl curFunc) {
	Code.genInstr("", "movl", "$"+numVal+",%eax", ""+numVal); 
    }
    
    @Override void parse() {
	Log.enterParser("<operand>");
	Log.enterParser("<number>");
	
	numVal = Scanner.curNum;
	Scanner.skip(numberToken);

	Log.leaveParser("</number>");
	Log.leaveParser("</operand>");
    }

    @Override void printTree() {
	Log.wTree("" + numVal);
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
	    //System.out.println(index); // test
	    index.check(curDecls);
	    //System.out.println("\nin variable.check: " + index.valType);
	    index.valType.checkType(lineNum, Types.intType, "Array index");
	    //valType = ((ArrayType)d.type).elemType; // <--- this might be wrong in our code
	    if(d instanceof LocalArrayDecl) {
		valType = ((LocalArrayDecl)d).at.elemType;
	    }
	    else {
		valType = ((GlobalArrayDecl)d).at.elemType;
	    }
	}
	declRef = (VarDecl)d;
    }

    @Override void genCode(FuncDecl curFunc) {
	//-- Must be changed in part 2:

	if (index != null) {
	    index.genCode(curFunc);
	    Code.genInstr("", "leal", declRef.assemblerName +
			  ",%edx", declRef.name + "[...]");

	    if(declRef.type.typeName().equals(Types.intType.typeName())) {
	    	Code.genInstr("", "movl", "(%edx,%eax,4),%eax", ""); 
	    }
	    else {
	    	Code.genInstr("", "fldl", "(%edx,%eax,8)", ""); 
	    }
	} 
	else {
	    if (valType == Types.doubleType) {
		//System.err.println("fldl\t" + declRef.assemblerName);
		Code.genInstr("", "fldl", declRef.assemblerName, declRef.name);
	    } else {
		Code.genInstr("", "movl", declRef.assemblerName + ",%eax", declRef.name);
	    }
	}
    }

    @Override void parse() {
	Log.enterParser("<operand>");
	Log.enterParser("<variable>");
	
	varName = Scanner.curName;
	Scanner.skip(nameToken);

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
