# SPL-compiler-attempt-2
This is my second attempt at a compiler I built for a programming language named SPL (Student programming language) as an assignment, this time with a different grammar
It is still quite crude, not great but better than what I had last time. I wll revert one day to fix it. when these last 2 sentences are gone, just know it is perfect.


## How to run it! 
In a few step, I made this project using Intellij, which makes things a lot easier in terms of running the project so
if you can, I would recommend also using IntelliJ. Here are the steps for IntelliJ:

1. Clone the repo into an empty folder
2. Add your test files into the "Test" folder in the project tree
3. Open the project in IntelliJ
4. click the green "play/run button" once all the files have been scanned
5. when it runs, it will prompt you to input a file name. this is for files that fall within the grammar
6. input the file name and voila!!

I will add how to run it via other means later on but yeah.

## The Grammar!
PROG' -> PROG $

PROG -> main GLOBVARS ALGO FUNCTIONS

GLOBVARS ->

GLOBVARS -> VTYP VNAME , GLOBVARS

VTYP -> num

VTYP -> text

VNAME -> var

ALGO -> begin INSTRUC end

INSTRUC ->

INSTRUC -> COMMAND ; INSTRUC

COMMAND -> skip

COMMAND -> halt

COMMAND -> print ATOMIC

COMMAND -> ASSIGN

COMMAND -> CALL

COMMAND -> BRANCH

ATOMIC -> VNAME

ATOMIC -> CONST

CONST -> number

CONST -> text

ASSIGN -> VNAME < input

ASSIGN -> VNAME = TERM

CALL -> FNAME ( ATOMIC , ATOMIC , ATOMIC )

BRANCH -> if COND then ALGO else ALGO

TERM -> ATOMIC

TERM -> CALL

TERM -> OP

OP -> UNOP ( ARG )

OP -> BINOP ( ARG , ARG )

ARG -> ATOMIC

ARG -> OP

COND -> SIMPLE

COND -> COMPOSIT

SIMPLE -> BINOP ( ATOMIC , ATOMIC )

COMPOSIT -> BINOP ( SIMPLE , SIMPLE )

COMPOSIT -> UNOP ( SIMPLE )

UNOP -> not

UNOP -> sqrt

BINOP -> or

BINOP -> and

BINOP -> eq

BINOP -> grt

BINOP -> add

BINOP -> sub

BINOP -> mul

BINOP -> div

FNAME -> funct

FUNCTIONS ->

FUNCTIONS -> DECL FUNCTIONS

DECL -> HEADER BODY

HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )

FTYP -> num

FTYP -> void

BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end

PROLOG -> {

EPILOG -> }

LOCVARS -> VTYP VNAME , VTYP VNAME , VTYP VNAME ,

SUBFUNCS -> FUNCTIONS
