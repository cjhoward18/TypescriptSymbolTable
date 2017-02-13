grammar TypeScript;

@header {
import org.antlr.symtab.*;
}

program returns [Scope scope] : sourceElement* EOF ;

sourceElement : classDeclaration | functionDeclaration | varDeclaration|statement ;

classDeclaration returns [Scope scope]
    : 'class' name=ID ( 'extends' superClassName=ID )* '{'
    (field | functionDeclaration)*
    '}'
    ;

field : ID ':' type ';' ;

functionDeclaration returns [Scope scope]
    : (('function')? ID '(' (parameter? | ((parameter ',')+ parameter)) ')' ( ':' typename = type)?  block)
    ;

parameter returns [Scope scope]  : ID ':' type ;

block returns [Scope scope]
    : '{' (block|statement|decl|functionDeclaration|varDeclaration|funcCall)* '}'
    ;

decl: ID ':' type  ';';

varDeclaration:
    'var' ID ':' type ';'
    ;

statement : expr ';'
    ;

funcCall returns [Type etype]
    :
    ID '(' (arg= (ID | INT)? ) ')'
    ;

    argument returns [Type etype] : ID|INT
    ;


expr returns [Type etype]
    :
      funcCall      #Functioncalls
    | expr sign expr  #Allmath
    | INT            #IntLiteral
    | STRING         #StringLiteral
    | ID             #Varref
    | 'this'        #Isthis
    | expr '.' ID    #Fieldreference
    | expr '=' expr   #Assignement

    ;

math :  INT sign INT | math sign INT
    ;

sign : '*' | '-' | '+' | '/'
    ;

type : 'number' | 'string' | ctype=ID ;

STRING : '"' [a-zA-Z]* '"' ;
ID : [a-zA-Z]+ ;
INT : [0-9]+ ;
WS : [\ \r\t\n]+ -> skip ;
