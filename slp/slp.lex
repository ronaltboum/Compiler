/***************************/
/* FILE NAME: LEX_FILE.lex */ 
/***************************/

/*************/
/* USER CODE */
/*************/
package slp;

import java_cup.runtime.*;
import java.util.List;
import java.util.ArrayList;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/
      
%%
   
/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer
%type Token
/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column
    
/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup



/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied letter to letter into the Lexer class code.                */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{   
	StringBuffer string = new StringBuffer();
    /*********************************************************************************/
    /* Create a new java_cup.runtime.Symbol with information about the current token */
    /*********************************************************************************/
    public int getLineNumber() { return yyline; }
    private Token printAndSymbol(String str, int type) 
    {
    	return new Token(yyline, str, type);
    }
    private List<String> CLASS_ID_LIST;
    private Boolean      ERROR_OCCURED;
    private int          COMMENT_START;
%}

%init{
	yyline = 1;
	ERROR_OCCURED = false;
	COMMENT_START = 0;
	CLASS_ID_LIST = new ArrayList<String>();
%init}

%eof{
	if( yystate() == COMMENT )
	{
		System.out.println((++yyline)+": "+"For comment starting on line: <"+COMMENT_START+"> ,missing comment closure.");
	}
	else if( !ERROR_OCCURED )
	{
		//System.out.println((++yyline)+": "+"EOF");
	}
%eof}
   
/***********************/
/* MACRO DECALARATIONS */
/***********************/

LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]
InputCharacter = [^\r\n]
INTEGER			= 0 | [1-9][0-9]*
IDENTIFIER		= [A-Za-z_][A-Za-z_0-9]*
ALPHA_NUMERIC={ALPHA}|{DIGIT}
ALPHA=[A-Za-z_] 
DIGIT=[0-9]
BIG_LETTER=[A-Z]
SMALL_LETTER=[a-z]


// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?


%state STRING
%state CLASS
%state ELIMINATE_WS
%state COMMENT
%state ERROR

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/
%%
/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/
   
/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/
   
   

<YYINITIAL> 
{

/**********************/
/*      KEYWORDS 	  */
/**********************/

	"boolean"			{ return printAndSymbol("BOOLEAN", sym.BOOLEAN); }
	"break"				{ return printAndSymbol("BREAK", sym.BREAK); }
	"continue"			{ return printAndSymbol("CONTINUE", sym.CONTINUE); }
	"extends"			{ return printAndSymbol("EXTENDS", sym.EXTENDS); }
	"if"				{ return printAndSymbol("IF", sym.IF); }
	"else"				{ return printAndSymbol("ELSE", sym.ELSE); }
	"while"				{ return printAndSymbol("WHILE", sym.WHILE); }
	"false"				{ return printAndSymbol("FALSE", sym.FALSE); }
	"true"				{ return printAndSymbol("TRUE", sym.TRUE); }
	"return"			{ return printAndSymbol("RETURN", sym.RETURN); }
	"static"			{ return printAndSymbol("STATIC", sym.STATIC); }
	"int"				{ return printAndSymbol("INT", sym.INT); }
	"length"			{ return printAndSymbol("LENGTH", sym.LENGTH); }
	"new"				{ return printAndSymbol("NEW", sym.NEW); }
	"null"				{ return printAndSymbol("NULL", sym.NULL); }
	"string"			{ return printAndSymbol("STRING", sym.STRING); }
	"this"				{ return printAndSymbol("THIS", sym.THIS); }
	"void"				{ return printAndSymbol("VOID", sym.VOID); }
	"class"             { 
					  string.setLength(0); yybegin(ELIMINATE_WS); 
					  return printAndSymbol("CLASS", sym.CLASS);
					}
	\"                  { string.setLength(0); yybegin(STRING); }
								
/**********************/
/*      BRACKETS 	  */
/**********************/

	"("					{ return printAndSymbol("LP", sym.LP); }
	")"					{ return printAndSymbol("RP", sym.RP); }
	"["					{ return printAndSymbol("LB", sym.LB); }
	"]"					{ return printAndSymbol("RB", sym.RB); }
	"{"					{ return printAndSymbol("LCBR", sym.LCBR); }
	"}"					{ return printAndSymbol("RCBR", sym.RCBR); }

/**********************/
/*    OPERATORS 	  */
/**********************/

	";"					{ return printAndSymbol("SEMI", sym.SEMI); }
	"+"					{ return printAndSymbol("PLUS", sym.PLUS); }
	"%"					{ return printAndSymbol("MOD", sym.MOD); }
	"-"					{ return printAndSymbol("MINUS", sym.MINUS); }
	"*"					{ return printAndSymbol("MULTIPLY", sym.MULTIPLY); }
	"."					{ return printAndSymbol("DOT", sym.DOT); }
	"/"					{ return printAndSymbol("DIVIDE", sym.DIVIDE); }
	"="					{ return printAndSymbol("ASSIGN", sym.ASSIGN); }
	"=="				{ return printAndSymbol("EQUAL", sym.EQUAL); }
	">"					{ return printAndSymbol("GT", sym.GT); }
	">="				{ return printAndSymbol("GTE", sym.GTE); }
	"<"					{ return printAndSymbol("LT", sym.LT); }
	"<="				{ return printAndSymbol("LTE", sym.LTE); }
	","					{ return printAndSymbol("COMMA", sym.COMMA); }
	"!"					{ return printAndSymbol("LNEG", sym.LNEG); }
	"!="				{ return printAndSymbol("NEQUAL", sym.NEQUAL); }
	"||"				{ return printAndSymbol("LOR", sym.LOR); }
	"&&"				{ return printAndSymbol("LAND", sym.LAND); }


/*	{IDENTIFIER}		{
 						String id = new String( yytext() );
						
						// Check if this identifier is a class identifier
						// do this by checking if a class with this name was declared
						// or check if its the 'Library' class
						for( String class_id : CLASS_ID_LIST )
						{
							if( id.equals( class_id ) || id.equals("Library") )
							{
								// This identifier is a class ID
								return new Token(yyline, "CLASS_ID", sym.CLASS_ID, id );
							}
						}
						return new Token(yyline, "ID", sym.ID, id );
						}
					
					
					*/

	 {SMALL_LETTER}({ALPHA_NUMERIC})* { String id = new String( yytext() );
	return new Token(yyline, "ID", sym.ID, id ); }
 {BIG_LETTER}({ALPHA_NUMERIC})* { String id = new String( yytext() );
 				 return new Token(yyline, "CLASS_ID", sym.CLASS_ID, id ); }
		
					
					
	{INTEGER}			{
						return new Token(yyline, "NUMBER", sym.NUMBER,  new Integer(yytext()) );
						}
					
	{EndOfLineComment}  {}
	"/*"     			{ 
						COMMENT_START = yyline;
						yybegin(COMMENT); 
					}
					
	{WhiteSpace}		{ /* just skip what was found, do nothing */ }   


} // YYINITIAL END

<STRING> 
{
	
	\"   				{ 
							yybegin(YYINITIAL);
        					return new Token(yyline, "QUOTE", sym.QUOTE,  string.toString() );
     					}
	[^\n\r\"\\]+        { 	string.append( yytext() ); }
	\\t                 { 	string.append('\t'); }
	\\n                 { 	string.append('\n'); }
	\\r                 { 	string.append('\r'); }
	\\\"                { 	string.append('\"'); }
	\\                  { 	string.append('\\'); }

}

<CLASS>
{
	[ \t\f]				{
			      	  		yybegin(YYINITIAL);
			      	  		CLASS_ID_LIST.add(string.toString());
	      			  		return new Token(yyline, "CLASS_ID", sym.CLASS_ID, string.toString() );
	      				}
	[A-Za-z_0-9]        { 	string.append( yytext() ); }
}


<ELIMINATE_WS> 
{

	[ \t\f]				{ }
	[A-Za-z_] 			{
      						string.append( yytext() );
	  						yybegin(CLASS);
      					}
	[^]             	{ 	throw new Error("Class name is illegal <"+ yytext() + "> lineNumber: " + yyline); }

}

<COMMENT> 
{
	// Eat symbols until "*/", then return to the initial state
	"*/"                    { 
                              	yybegin(YYINITIAL); 
                            }
	[^]  					{}
}

<ERROR>
{
	[^]						{ /* This is where code goes to die */ yyclose(); }
}

// Errors
.|\n                { 
						// Sometimes it dosent close immidiately, so send it to the dead zone until it's closed
						yyclose();
						yybegin( ERROR );
						
						System.out.println(yyline+": "+"Lexical error: illegal character "+yytext());
						ERROR_OCCURED = true;
					}
					
<<EOF>> 	{ return new Token(yyline, "EOF", sym.EOF); }