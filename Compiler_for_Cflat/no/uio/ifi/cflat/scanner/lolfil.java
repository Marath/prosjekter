//	System.err.println("Scanner - pre letter, curr" + curr);
		
		if(isLetterAZ(next)) {
		   
		    System.err.println("Scanner we got a letter!");
		    while(next != 32) {
			
			System.err.println("next is: " + next);
			
			if(isSymbol(next)) {
			    
			    break;
			}
			
			if(nextNextName == null) {
			     nextNextName = Character.toString(curr);
			 }
			
			
			nextNextName += next;
			//nextNextName += curr;
			System.out.println("Scanner - nextNextName: " + nextNextName);
			
			CharGenerator.readNext();
			
			if(i == 6) break;
			i++;
		    }

		    // sette tokens fra uber Tokenfil inni while løkke
		    // System.err.println("nextNextName: " + nextNextName);
		    
		    switch(nextNextName) { /** int, double, for, if, else, while, name->(default) */
		    case "int": nextNextToken = intToken;
		    case "double": nextNextToken = doubleToken;
		    case "for": nextNextToken = forToken;
		    case "if": nextNextToken = ifToken;
		    case "else": nextNextToken = elseToken;
		    case "while": nextNextToken = whileToken;
		    default: nextNextToken = nameToken;
		    }
		    
		    
		  
		} else {
		    
		    System.err.println("scanner - pre symbol");  

		    if(CharGenerator.nextC != ' ' || curr != ' ') {
			if(!isLetterAZ(CharGenerator.nextC)) 
			    nextNextName = Character.toString(CharGenerator.nextC);
		    }
		    
		    if(isSymbol(curr)) { /** symbolbehandling */
			// if tester fra Uberfil'
			if(curr != ' ') {
			    switch(nextNextName) { 
			    case ",": nextNextToken = commaToken; /** , ; */
			    case ";": nextNextToken = semicolonToken;
				
			    case "+": nextNextToken = addToken; /** +, -, /, * */
			    case "-": nextNextToken = subtractToken;
			    case "/": nextNextToken = divideToken;
			    case "*": nextNextToken = multiplyToken;
				
			    case ">": nextNextToken = greaterToken; /** >, <, = */
			    case "<": nextNextToken = lessToken;
			    case "=": nextNextToken = assignToken;
				
			    case "(": nextNextToken = leftParToken; /** (, ), [, ], {, } */
			    case ")": nextNextToken = rightParToken;
			    case "[": nextNextToken = leftBracketToken;
			    case "]": nextNextToken = rightBracketToken;
			    case "{": nextNextToken = leftCurlToken;
			    case "}": nextNextToken = rightCurlToken;
				
			    }
			    
			} else if(CharGenerator.nextC != ' ') {
			    
			    if (nextName.equals('>') && nextNextName.equals('=')) { /** >=, <=, ==, != */
				nextNextToken = greaterEqualToken;
			    } else if (nextName.equals('<') && nextNextName.equals('=')) {
				nextNextToken = lessEqualToken;
			    } else if (nextName.equals('=') && nextNextName.equals('=')) {
				nextNextToken = equalToken;
			    } else {
				nextNextToken = notEqualToken;
			    }    
			    
			} else if(CharGenerator.curC != ' ') {
			    
			}  
			
			
			
			
		    } // else { /** tallbehandling */ 
		    // 	System.err.println("Scanner - is number");
		    // 	while(CharGenerator.nextC != ' ') {
		    // 	    nextNextName += curr;
		    // 	}
		    // 	nextNextNum = Integer.parseInt(nextNextName);
		    // 	nextNextToken = numberToken;
		    // }
		    
		    
		} 
	    }


    // if(nextNextName.equals("int")) {
		    // 	nextNextToken = intToken;
		    // } else if(nextNextName.equals("double")) {
		    // 	nextNextToken = doubleToken;
		    // }  else if(nextNextName.equals("for")) {
		    // 	nextNextToken = forToken;
		    // } else if(nextNextName.equals("if")) {
		    // 	nextNextToken = ifToken;
		    // } else if(nextNextName.equals("else")) {
		    // 	nextNextToken = elseToken;
		    // } else if(nextNextName.equals("while")) {
		    // 	nextNextToken = whileToken;
		    // } else {
		    // 	nextNextToken = nameToken;
		    // }