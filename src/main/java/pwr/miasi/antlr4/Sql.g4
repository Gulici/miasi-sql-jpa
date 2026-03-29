grammar Sql;

@header {
package pwr.miasi.antlr4;
}

schema
    : statement* EOF
    ;

statement
    : createTable SEMI?
    ;

createTable
    : CREATE TABLE tableName LPAREN tableElement (COMMA tableElement)* RPAREN
    ;

tableElement
    : columnDef
    | tableConstraint
    ;

columnDef
    : columnName dataType columnConstraint*
    ;

tableConstraint
    : (CONSTRAINT constraintName)? PRIMARY KEY LPAREN columnNameList RPAREN
    | (CONSTRAINT constraintName)? FOREIGN KEY LPAREN columnNameList RPAREN referencesClause
    | (CONSTRAINT constraintName)? UNIQUE LPAREN columnNameList RPAREN
    ;

columnConstraint
    : PRIMARY KEY
    | NOT NULL
    | UNIQUE
    | referencesClause
    ;

referencesClause
    : REFERENCES tableName LPAREN columnNameList RPAREN
    ;

dataType
    : INT
    | BIGINT
    | VARCHAR LPAREN NUMBER RPAREN
    | TEXT
    | TIMESTAMP
    ;

columnNameList
    : columnName (COMMA columnName)*
    ;

constraintName : IDENTIFIER;
tableName : IDENTIFIER;
columnName : IDENTIFIER;

/* ===== TOKENS ===== */

CREATE : 'CREATE';
TABLE : 'TABLE';
PRIMARY : 'PRIMARY';
KEY : 'KEY';
FOREIGN : 'FOREIGN';
REFERENCES : 'REFERENCES';
CONSTRAINT : 'CONSTRAINT';
NOT : 'NOT';
NULL : 'NULL';
UNIQUE : 'UNIQUE';
SEMI : ';';
COMMA : ',';
LPAREN : '(';
RPAREN : ')';

INT : 'INT';
BIGINT : 'BIGINT';
VARCHAR : 'VARCHAR';
TEXT : 'TEXT';
TIMESTAMP : 'TIMESTAMP';

IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*;
NUMBER : [0-9]+;

WS : [ \t\r\n]+ -> skip;