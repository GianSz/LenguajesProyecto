/** Programming Languajes
	By: Carmen Carvajal
	Parser Class: this class verifies the source file syntax according to the grammar
**/
import java.util.*;
import java.lang.*;
import java.io.*;

public class Parser{
	//token: (Token) used to process each token in the source code
	private Token token;
	//lexer: (Lexer) used to obtain each token in the source code
	private Lexer lexer;
	
	/**
		Constructor: 
			Creates the lexer to obtain each token in the source file
			Points the iterator to the first token
			Starts parsing at the start symbol according the the grammar
	**/
	public Parser(String fileName){
		try{
			//Creates the lexer
			lexer = new Lexer(fileName);
			//Points the iterator to the first token
			token = lexer.nextToken();
			//Starts the parser
			program();
		} catch (FileNotFoundException ex){
			System.out.println("File not found!");
			System.exit(1);
		}
	}
	/**
		Function recognize: verifies if the current token corresponds to the expected token 
		according with the grammar and move the tokenIterator to the next one.
		@param expected: (int) expected token code
	**/
	private void recognize(int expected){
		if (token.code == expected)
			//the token is the expected one, move the tokenIterator
			token = lexer.nextToken();
		else {
			//The current token is not expected, syntax error!
			System.out.println("Syntax error in line " + token.line);
			System.out.println("Expected: "  + expected + " found " + token.code);
			//STOP!
			System.exit(2);
		}
	}
	/**
		Function program: verifies the production 
			<program>::= program <funDefinitionList> endProgram
	**/
	public void program(){
		//checks for "program"
		recognize(Lexer.PROGRAM);
		//checks for <funDefinitionList>
		funDefinitionList();
		//checks for "endProgram"
		recognize(Lexer.ENDPROGRAM);
		//if EOf is found, no error is found!
		if (token.code == Lexer.EOF){
			System.out.println("No errors found!");
		}
	}
	
	/**
		Function funDefinitionList: verifies the production 
		<funDefinitionList>::=<funDefinition> { <funDefinition> }
	**/ 
	public void funDefinitionList()
	{
		//checks for <funDefinition>
		funDefinition();
		//verifies if there are more <funDefinition>
		while(lexer.getCurrentToken().code == Lexer.DEF){
			funDefinition();
		}
	}
	
	/**
		Function funDefinition: verifies the production 
		<funDefinition>::= def <variable> <lparen> [<varDefList>] <rparen>
								[<varDefList>]
								[<statementList>]
							enddef
	**/
	public void funDefinition(){
		//checks for "def"
		recognize(Lexer.DEF);
		//checks for <variable>
		recognizeVariable();
		//checks for "("
		recognize(Lexer.LPAREN);
		//verifies if there are parameters
		if (lexer.getCurrentToken().code != Lexer.RPAREN) // def main ( )           def main ( int a int b)
			varDefList();
		//checks for ")"
		recognize(Lexer.RPAREN);
		//ver is there are variables definition
		if (lexer.getCurrentToken().code == Lexer.INT)
			varDefList();
		//checks for <statementList>
		statementList();
		//checks for "enddef"
		recognize(Lexer.ENDDEF);
	}
	
	/**
		Function varDefList: verifies the production <varDefList>::=<varDef> {<varDef>}
	**/
	public void varDefList(){
		//checks for <varDef>
		varDef();
		//Verifies if there are more <varDef>
		while (lexer.getCurrentToken().code == Lexer.INT){
			varDef();
		}	
	}
	
	/**
		Function varDef: verifies the production <varDef>::=int variable
	**/
	public void varDef(){
		recognize(Lexer.INT);
		//recognize(Lexer.VARIABLE);
		recognizeVariable();
	}
	/**
		Function statementList: verifies the production <statementList>::=<statement> {<statement>}
	**/
	public void statementList()
	{
		//checks for <statement>
		statement();
		//verifies if there are more <statement>
		while ((lexer.getCurrentToken().code != Lexer.ENDDEF) && (statement()));
	}

	/**
		Function statement: verifies the production <statement>::= read <variable> | print <variable> | call <variable> <lparen> [ <argumentList> ] <rparen> | <conditional> | <while> | <assignment>	
		**/
	public boolean statement(){
		boolean r = false;
		//checks for read <variable>
		if (lexer.getCurrentToken().code == Lexer.READ){
			recognize(Lexer.READ);
			recognize(Lexer.VARIABLE);
			System.out.println("Read ok!");
			r=true;
		} //checks for print <variable>
		else if (lexer.getCurrentToken().code == Lexer.PRINT){
			recognize(Lexer.PRINT);
			recognize(Lexer.VARIABLE);
			System.out.println("Print ok!");
			r=true;
		} //checks for call <variable> <lparen> [argumentList] <rparen>
		else if (lexer.getCurrentToken().code == Lexer.CALL){
			//checks for "call"
			recognize(Lexer.CALL);
			//checks for <variable>
			recognize(Lexer.VARIABLE);
			//checks for <lparen>
			recognize(Lexer.LPAREN);
			//verifies if there is an argument list
			if (lexer.getCurrentToken().code != Lexer.RPAREN)
				argumentList();
			//checks for <rparen>
			recognize(Lexer.RPAREN);
			System.out.println("call function ok!");
			r=true;			
		}
		else if (lexer.getCurrentToken().code == Lexer.IF){
			conditional();
			System.out.println("conditional ok!");
			r = true;
		}
		else if(lexer.getCurrentToken().code == Lexer.WHILE){
			cicloWhile();
			System.out.println("while ok!");
			r = true;
		}
		else if(lexer.getCurrentToken().code == Lexer.VARIABLE){
			assignment();
			System.out.println("assignment ok!");
			r = true;
		}
		return r;
	}
	
	/**
		Function argumentList: verifies the production <argumentList> ::= <argumentDef> { <argumentDef> }
	**/
	public void argumentList(){
		//Checks for <argumentDef>
		argumentDef();
		//verifies if there are more <argumentDef>
		while (lexer.getCurrentToken().code != Lexer.RPAREN)
			argumentDef();
	}
	
	/**
		Function argumentDef: verifies the production <argumentDef> ::= <variable>
	**/
	public void argumentDef(){
		recognize(Lexer.VARIABLE);
	}
	
	/**
		Function recognizeVariable: verifies for variables
	
	**/
	public void recognizeVariable(){

		if(token.text.length()>1){	//213
			if(letter(token.text.charAt(0))){
				for(int i = 1; i<token.text.length(); i++){
					if(!letter(token.text.charAt(i))||!digit(token.text.charAt(i)))
					System.out.println("Syntax error in line " + token.line);
					System.out.println("The variable cannot be declared using that syntax rule.");
					//STOP!
					System.exit(3);
				}
				recognize(Lexer.VARIABLE);
			}
			else
				if(recognizeConstant())		// 7 = 3
					lexer.getCurrentToken().code = Lexer.CONSTANT;
				recognize(Lexer.CONSTANT);
			
		}
		else
			if(letter(token.text.charAt(0))){
				recognize(Lexer.VARIABLE);
			}
				
			else if(digit(token.text.charAt(0))){
				lexer.getCurrentToken().code = Lexer.CONSTANT;
				recognize(Lexer.CONSTANT);
			}
			else{
				System.out.println("Syntax error in line " + token.line);
					System.out.println("The variable cannot be declared using that syntax rule.");
					//STOP!
					System.exit(3);
			}
	}

	public void conditional(){
		recognize(Lexer.IF);
		recognize(Lexer.LPAREN);
		condition();
		recognize(Lexer.RPAREN);
		if(lexer.getCurrentToken().code != Lexer.ENDIF)
			statementList();
		else
			recognize(Lexer.ENDIF);

		recognize(Lexer.ELSE);
		if(lexer.getCurrentToken().code != Lexer.ENDELSE)
			statementList();
		recognize(Lexer.ENDELSE);
	}

	public void cicloWhile(){
		recognize(Lexer.WHILE);
		recognize(Lexer.LPAREN);
		condition();
		recognize(Lexer.RPAREN);
		if(lexer.getCurrentToken().code != Lexer.ENDWHILE)
			statementList();
		else
			recognize(Lexer.ENDWHILE);
	}

	public void expr(){		
		term();

		while(lexer.getCurrentToken().code == Lexer.SUM){
			term();
		}
	}

	public void term(){
		if(factor()){
			while(lexer.getCurrentToken().code == Lexer.MULT){
				if(!(factor())){
					break;
				}
			}
		}
	}

	public boolean factor(){
		boolean vf = false;
		if(lexer.getCurrentToken().code == Lexer.LPAREN){
			expr();
			vf = true;
			recognize(Lexer.RPAREN);
		}
		else if(lexer.getCurrentToken().code == Lexer.VARIABLE){
			recognizeVariable();
			vf = true;
		}
		else if(lexer.getCurrentToken().code == Lexer.CONSTANT){
			recognize(Lexer.CONSTANT);
			vf = true;
		}
		return vf;
	}

	public void condition(){
		expr();
		if(lexer.getCurrentToken().code == Lexer.EQUALS){
			recognize(Lexer.EQUALS);
		}
		else
			recognize(Lexer.DIFFERENT);
		expr();
	}

	public void assignment(){
		recognizeVariable();
		recognize(Lexer.ASSIGN);
		expr();
	}

	public boolean letter(char e){
		if(((64<=e)||(90>=e))||((97<=e)||(122>=e)))
			return true;
		else
			return false;
	}
	
	public boolean digit(char e){

		if((48<=e)||(57>=e))
			return true;
		else
			return false;
	}

	public boolean recognizeConstant(){

		
		for(int i = 0; i<token.text.length(); i++){

			if(!digit(token.text.charAt(i)))
				return false;

		}
		return true;
		


	}
	public static void main(String args[])
	{
		try {
			String fileName = args[0];
			Parser parser = new Parser(fileName);
				} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}



}