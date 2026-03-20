grammar Sql;

schema
    : createTable* EOF
    ;

createTable
    : CREATE TABLE tableName '(' columnDef (',' columnDef)* ')' SEMI?
    ;

columnDef
    : columnName type columnConstraint*
    ;

columnConstraint
    : PRIMARY KEY
    | NOT NULL
    | UNIQUE
    ;

type
    : INT
    | BIGINT
    | VARCHAR '(' NUMBER ')'
    | TEXT
    | TIMESTAMP
    ;

tableName : IDENTIFIER;
columnName : IDENTIFIER;

/* ===== TOKENS ===== */

CREATE : 'CREATE';
TABLE : 'TABLE';
PRIMARY : 'PRIMARY';
KEY : 'KEY';
NOT : 'NOT';
NULL : 'NULL';
UNIQUE : 'UNIQUE';
SEMI : ';';

INT : 'INT';
BIGINT : 'BIGINT';
VARCHAR : 'VARCHAR';
TEXT : 'TEXT';
TIMESTAMP : 'TIMESTAMP';

IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*;
NUMBER : [0-9]+;

WS : [ \t\r\n]+ -> skip;