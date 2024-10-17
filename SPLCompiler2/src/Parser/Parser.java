package Parser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import Lexer.Lexer;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;

public class Parser {
    static int nextTokenIndex = 0;
    static Lexer.token currentToken = null;
    static ArrayList<Lexer.token> input = new ArrayList<Lexer.token>();
    static int pos = 0;
    static int checkIfTwoD = 0;
    static int idCounter = 0;
    static Document doc;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static int id;
//    static node root;

    /*
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
    SUBFUNCS -> FUNCTIONS*/
}
