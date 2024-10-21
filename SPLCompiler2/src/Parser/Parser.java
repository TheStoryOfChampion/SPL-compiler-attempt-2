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
import com.sun.source.tree.Tree;
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
    static TreeNode root;
    private TreeNode currentNode;

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

    public TreeNode start(ArrayList<Lexer.token> tokens){
        input = tokens;

        try {
            TreeNode PROG = null;
            PROG(PROG);

            if (pos != input.size()){
                report("EOF");
            }
            System.out.println("Program successfully parsed!");
            return root;


        } catch (Exception e){
            System.out.println("Parsing Error: ");
        }
        return root;
    }
    private static void PROG(TreeNode parent){
        if(pos >= input.size()){
            report("main");
        }

        TreeNode PROG = createNode(parent, "PROG");

        if (parent == null){
            root = new TreeNode("PROG");
            root.type = "NonTerminal";
            root.setId(id++);
        }

        if(pos < input.size() && (((input.get(pos).contents).equals("main")))){
            PROG = addTerminalChild(PROG, input.get(pos).contents);
            next();

            GLOBVARS(PROG);
            ALGO(PROG);
            FUNCTIONS(PROG);
        } else {
            report("main");
        }

    }
    private static void GLOBVARS(TreeNode parent){
        if(pos >= input.size()){
            return;
        }

        if(pos < input.size() && (input.get(pos).contents).equals("begin")){
            return;
        }

        TreeNode GLOBVARS = createNode(parent, "GLOBVARS");

        VTYP(parent);
        VNAME(parent);
        if (pos < input.size() && (input.get(pos).contents).equals(",")){
            GLOBVARS = addTerminalChild(GLOBVARS, input.get(pos).contents);
            next();

            GLOBVARS(GLOBVARS);
        } else {
            report(",");
        }

    }

    private static void ALGO(TreeNode parent){
        if(pos >= input.size()){
            report("begin");
        }

        TreeNode ALGO = createNode(parent, "ALGO");

        if(pos < input.size() && (input.get(pos).contents).equals("begin")){
            ALGO = addTerminalChild(ALGO, input.get(pos).contents);
            next();

            INSTRUC(ALGO);

            if(pos < input.size() && (input.get(pos).contents).equals("end")){
                ALGO = addTerminalChild(ALGO, input.get(pos).contents);
                next();
            } else {
                report("end");
            }
        } else {
            report("begin");
        }
    }

    private static void FUNCTIONS(TreeNode parent){
        if (pos >= input.size()){
            return;
        }

        TreeNode FUNCTIONS = createNode(parent, "FUNCTIONS");
        DECL(FUNCTIONS);
        FUNCTIONS(FUNCTIONS);
    }

    private static void VNAME(TreeNode parent){
        if (pos >= input.size()){
            report("Variable name");
        }

        TreeNode VNAME = createNode(parent, "VNAME");

        if (pos < input.size() && (input.get(pos).classification).equals("VNAME")){
            VNAME = addTerminalChild(VNAME, input.get(pos).contents);
            next();
        } else {
            report("Variable Name");
        }
    }

    private static void VTYP(TreeNode parent){
        if (pos >= input.size()){
            report("num or text");
        }

        TreeNode VTYP = createNode(parent, "VTYP");

         if (pos < input.size() && ((input.get(pos).contents).equals("num") || (input.get(pos).contents).equals("text"))){
             VTYP = addTerminalChild(VTYP, input.get(pos).contents);
             next();
         } else {
             report("num or text");
         }
    }

    private static void INSTRUC(TreeNode parent){
        if (pos >= input.size()){
            return;
        }

        if (pos < input.size() && (input.get(pos).contents).equals("end")){
            return;
        }

        TreeNode INSTRUC = createNode(parent, "INSTRUC");

        COMMAND(INSTRUC);

        if (pos < input.size() && (input.get(pos).contents).equals(";")){
            INSTRUC = addTerminalChild(INSTRUC, input.get(pos).contents);
            next();

            INSTRUC(INSTRUC);
        }
    }

    private static void DECL(TreeNode parent){
        if (pos >= input.size()){
            report("num or void");
        }

        TreeNode DECL = createNode(parent, "DECL");
        HEADER(DECL);
        BODY(DECL);
    }

    private static void COMMAND(TreeNode parent){
        if (pos >= input.size()){
            report("skip, halt, print, variable name, function name, or if");
        }

        TreeNode COMMAND = createNode(parent, "COMMAND");

        if (pos < input.size() && (input.get(pos).contents).equals("skip")){
            COMMAND = addTerminalChild(COMMAND, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("halt")){
            COMMAND = addTerminalChild(COMMAND, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("print")){
            COMMAND = addTerminalChild(COMMAND, input.get(pos).contents);
            next();

            ATOMIC(COMMAND);
        } else if (pos < input.size() && (input.get(pos).classification).equals("VNAME")){
            ASSIGN(COMMAND);
        } else if (pos < input.size() && (input.get(pos).classification).equals("FNAME")){
            CALL(COMMAND);
        } else if (pos < input.size() && (input.get(pos).contents).equals("if")){
            BRANCH(COMMAND);
        } else {
            report("skip, halt, print, variable name, function name, or if");
        }
    }

    private static void HEADER(TreeNode parent){
        if (pos >= input.size()){
            report("num or void");
        }

        TreeNode HEADER = createNode(parent, "HEADER");

        FTYP(HEADER);
        FNAME(HEADER);

        if (pos < input.size() && (input.get(pos).contents).equals("(")){
            HEADER = addTerminalChild(HEADER, input.get(pos).contents);
            next();

            VNAME(HEADER);
            if (pos < input.size() && (input.get(pos).contents).equals(",")){
                HEADER = addTerminalChild(HEADER, input.get(pos).contents);
                next();

                VNAME(HEADER);

                if (pos < input.size() && (input.get(pos).contents).equals(",")){
                    HEADER = addTerminalChild(HEADER, input.get(pos).contents);
                    next();

                    VNAME(HEADER);

                    if (pos < input.size() && (input.get(pos).contents).equals(")")){
                        HEADER = addTerminalChild(HEADER, input.get(pos).contents);
                        next();
                    } else {
                        report(")");
                    }
                } else {
                    report(",");
                }
            } else {
                report(",");
            }
        } else {
            report("(");
        }
    }

    private static void BODY(TreeNode parent){
        if (pos >= input.size()){
            report("{");
        }

        TreeNode BODY = createNode(parent, "BODY");

        PROLOG(BODY);
        LOCVARS(BODY);
        ALGO(BODY);
        EPILOG(BODY);
        SUBFUNCS(BODY);

        if (pos < input.size() && (input.get(pos).contents).equals("end")){
            BODY = addTerminalChild(BODY, input.get(pos).contents);
            next();
        } else {
            report("end");
        }
    }

    private static void ATOMIC(TreeNode parent){
        if (pos >= input.size()){
            report("Variable Name, number or text");
        }

        TreeNode ATOMIC = createNode(parent, "ATOMIC");

        if (pos < input.size() && (input.get(pos).classification).equals("VNAME")){
            VNAME(ATOMIC);
        } else if (pos < input.size() && ((input.get(pos).contents).equals("NCONST") || (input.get(pos).classification).equals("TCONST"))){
            CONST(ATOMIC);
        } else {
            report("Variable name, number or text");
        }
    }

    private static void ASSIGN(TreeNode parent){
        if (pos >= input.size()){
            report("Variable name followed by = or <");
        }

        TreeNode ASSIGN = createNode(parent, "ASSIGN");

        if (pos < input.size() && (input.get(pos+1).contents).equals("<")){
            VNAME(ASSIGN);
            ASSIGN= addTerminalChild(ASSIGN, input.get(pos).contents);
            next();

            if (pos < input.size() && (input.get(pos).contents).equals("input")){
                ASSIGN = addTerminalChild(ASSIGN, input.get(pos).contents);
                next();
            } else {
                report("input");
            }
        } else if (pos < input.size() && (input.get(pos+1).contents).equals("=")){
            VNAME(ASSIGN);
            ASSIGN = addTerminalChild(ASSIGN, input.get(pos).contents);
            TERM(ASSIGN);
        } else {
            report("Variable Name followed by = or <");
        }
    }

    private static void CALL(TreeNode parent){
        if (pos >= input.size()){
            report("Function name");
        }

        TreeNode CALL = createNode(parent, "CALL");

        FNAME(CALL);

        if (pos < input.size() && (input.get(pos).contents).equals("(")){
            CALL = addTerminalChild(CALL, input.get(pos).contents);
            next();

            ATOMIC(CALL);

            if (pos < input.size() && (input.get(pos).contents).equals(",")){
                CALL = addTerminalChild(CALL, input.get(pos).contents);
                next();

                ATOMIC(CALL);

                if (pos < input.size() && (input.get(pos).contents).equals(",")){
                    CALL = addTerminalChild(CALL, input.get(pos).contents);
                    next();

                    ATOMIC(CALL);

                    if (pos < input.size() && (input.get(pos).contents).equals(")")){
                        CALL = addTerminalChild(CALL, input.get(pos).contents);
                        next();
                    } else {
                        report(")");
                    }
                } else {
                    report(",");
                }
            } else {
                report(",");
            }
        } else {
            report("(");
        }
    }

    private static void BRANCH(TreeNode parent){
        if (pos >= input.size()){
            report("if");
        }

        TreeNode BRANCH = createNode(parent, "BRANCH");

        if (pos < input.size() && (input.get(pos).contents).equals("if")){
            BRANCH = addTerminalChild(BRANCH, input.get(pos).contents);
            next();

            COND(BRANCH);

            if (pos < input.size() && (input.get(pos).contents).equals("then")){
                BRANCH = addTerminalChild(BRANCH, input.get(pos).contents);
                next();

                ALGO(BRANCH);

                if (pos < input.size() && (input.get(pos).contents).equals("else")){
                    BRANCH = addTerminalChild(BRANCH, input.get(pos).contents);
                    next();

                    ALGO(BRANCH);
                } else {
                    report("else");
                }
            } else {
                report("then");
            }
        } else {
            report("if");
        }

    }

    private static void FNAME(TreeNode parent){
        if (pos >= input.size()){
            report("Function name");
        }

        TreeNode FNAME = createNode(parent, "FNAME");

        if (pos < input.size() && (input.get(pos).classification).equals("FNAME")){
            FNAME = addTerminalChild(FNAME, input.get(pos).contents);
            next();
        } else {
            report("Function Name");
        }
    }

    private static void FTYP(TreeNode parent){
        if (pos >= input.size()){
            report("num or void");
        }

        TreeNode FTYP = createNode(parent, "FTYP");

        if (pos < input.size() && ((input.get(pos).contents).equals("num") || (input.get(pos).contents).equals("void"))){
            FTYP = addTerminalChild(FTYP, input.get(pos).contents);
            next();
        } else {
            report("num or void");
        }
    }

    private static void PROLOG(TreeNode parent){
        if (pos >= input.size()){
            report("{");
        }

        TreeNode PROLOG = createNode(parent, "PROLOG");

        if (pos < input.size() && (input.get(pos).contents).equals("{")){
            PROLOG = addTerminalChild(PROLOG, input.get(pos).contents);
            next();
        } else {
            report("{");
        }
    }

    private static void LOCVARS(TreeNode parent){
        if (pos >= input.size()){
            report("num or text");
        }

        TreeNode LOCVARS = createNode(parent, "LOCVARS");

        VTYP(LOCVARS);
        VNAME(LOCVARS);

        if (pos < input.size() && (input.get(pos).contents).equals(",")){
            LOCVARS = addTerminalChild(LOCVARS, input.get(pos).contents);
            next();

            VTYP(LOCVARS);
            VNAME(LOCVARS);

            if (pos < input.size() && (input.get(pos).contents).equals(",")){
                LOCVARS = addTerminalChild(LOCVARS, input.get(pos).contents);
                next();

                VTYP(LOCVARS);
                VNAME(LOCVARS);

                if (pos < input.size() && (input.get(pos).contents).equals(",")){
                    LOCVARS = addTerminalChild(LOCVARS, input.get(pos).contents);
                    next();
                } else {
                    report(",");
                }
            } else {
                report(",");
            }
        } else {
            report(",");
        }
    }

    private static void EPILOG(TreeNode parent){
        if (pos >= input.size()){
            report("}");
        }

        TreeNode EPILOG = createNode(parent, "EPILOG");

        if (pos < input.size() && (input.get(pos).contents).equals("}")){
            EPILOG = addTerminalChild(EPILOG, input.get(pos).contents);
            next();
        } else {
            report("}");
        }
    }

    private static void SUBFUNCS(TreeNode parent){
        if (pos >= input.size()){
            return;
        }

        TreeNode SUBFUNCS = createNode(parent, "SUBFUNCS");

        FUNCTIONS(SUBFUNCS);
    }

    private static void CONST(TreeNode parent){
        if (pos >= input.size()){
            report("number or text");
        }

        TreeNode CONST = createNode(parent, "CONST");

        if (pos < input.size() && (input.get(pos).classification).equals("NCONST")){
            CONST = addTerminalChild(CONST, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).classification).equals("TCONST")){
            CONST = addTerminalChild(CONST, input.get(pos).contents);
            next();
        } else {
            report("number or text");
        }
    }

    private static void TERM(TreeNode parent){
        if (pos >= input.size()){
            report("Variable name, number, text, Function name or operation");
        }

        TreeNode TERM = createNode(parent, "TERM");

        if (pos < input.size() && ((input.get(pos).classification).equals("VNAME") || (input.get(pos).classification).equals("TCONST") || (input.get(pos).classification).equals("NCONST"))){
            ATOMIC(TERM);
        } else if (pos < input.size() && (input.get(pos).classification).equals("FNAME")){
            CALL(TERM);
        } else if (pos < input.size() && (((input.get(pos).contents.equals("not")) || (input.get(pos).contents.equals("sqrt"))
        || (input.get(pos).contents.equals("or")) || (input.get(pos).contents.equals("and")) || (input.get(pos).contents.equals("eq")) ||
                (input.get(pos).contents.equals("grt")) || (input.get(pos).contents.equals("add")) || (input.get(pos).contents.equals("sub")) ||
                (input.get(pos).contents.equals("mul")) || (input.get(pos).contents.equals("div"))))){
            OP(TERM);
        }
    }

    private static void COND(TreeNode parent){
        if (pos >= input.size()){
            report("Operand");
        }

        TreeNode COND = createNode(parent, "COND");

        if (pos < input.size() && ((input.get(pos+2).classification).equals("VNAME") || (input.get(pos+2).classification.equals("NCONST")) || (input.get(pos+2).classification).equals("TCONST"))){
            SIMPLE( COND);
        } else if (pos < input.size() && (((input.get(pos+2).contents.equals("not")) || (input.get(pos+2).contents.equals("sqrt"))
                || (input.get(pos+2).contents.equals("or")) || (input.get(pos+2).contents.equals("and")) || (input.get(pos+2).contents.equals("eq")) ||
                (input.get(pos+2).contents.equals("grt")) || (input.get(pos+2).contents.equals("add")) || (input.get(pos+2).contents.equals("sub")) ||
                (input.get(pos+2).contents.equals("mul")) || (input.get(pos+2).contents.equals("div"))))){
            COMPOSIT(COND);
        } else {
            report("Operand");
        }
    }

    private static void OP(TreeNode parents) {
        if (pos >= input.size()) {
            report("Operands");
        }

        TreeNode OP = createNode(parents, "OP");

        if (pos < input.size() && ((input.get(pos).contents).equals("not") || (input.get(pos).contents).equals("sqrt"))) {
            UNOP(OP);

            if (pos < input.size() && (input.get(pos).contents).equals("(")) {
                OP = addTerminalChild(OP, input.get(pos).contents);
                next();

                ARG(OP);

                if (pos < input.size() && (input.get(pos).contents).equals(")")) {
                    OP = addTerminalChild(OP, input.get(pos).contents);
                    next();
                } else {
                    report(")");
                }
            } else {
                report("(");
            }
        } else if (pos < input.size() && (((input.get(pos).contents.equals("or")) || (input.get(pos).contents.equals("and")) || (input.get(pos).contents.equals("eq")) ||
                (input.get(pos).contents.equals("grt")) || (input.get(pos).contents.equals("add")) || (input.get(pos).contents.equals("sub")) ||
                (input.get(pos).contents.equals("mul")) || (input.get(pos).contents.equals("div"))))) {
            BINOP(OP);

            if (pos < input.size() && (input.get(pos).contents).equals("(")) {
                OP = addTerminalChild(OP, input.get(pos).contents);
                next();

                ARG(OP);

                if (pos < input.size() && (input.get(pos).contents).equals(",")) {
                    OP = addTerminalChild(OP, input.get(pos).contents);
                    next();

                    ARG(OP);

                    if (pos < input.size() && (input.get(pos).contents).equals(")")) {
                        OP = addTerminalChild(OP, input.get(pos).contents);
                        next();
                    } else {
                        report(")");
                    }
                } else {
                    report(",");
                }
            } else {
                report("(");
            }
        } else {
            report("Operand");
        }
    }

    private static void SIMPLE(TreeNode parent){
        if (pos >= input.size()){
            report("Operand");
        }

        TreeNode SIMPLE = createNode(parent, "SIMPLE");

        BINOP(SIMPLE);

        if (pos < input.size() && (input.get(pos).contents).equals("(")){
            SIMPLE = addTerminalChild(SIMPLE, input.get(pos).contents);
            next();

            ATOMIC(SIMPLE);

            if (pos < input.size() && (input.get(pos).contents).equals(",")){
                SIMPLE = addTerminalChild(SIMPLE, input.get(pos).contents);
                next();

                ATOMIC(SIMPLE);

                if (pos < input.size() && (input.get(pos).contents).equals(")")){
                    SIMPLE = addTerminalChild(SIMPLE, input.get(pos).contents);
                    next();
                } else {
                    report(")");
                }
            } else {
                report(",");
            }
        } else {
            report("(");
        }
    }

    private static void COMPOSIT(TreeNode parent){
        if (pos >= input.size()){
            report("Operand");
        }

        TreeNode COMPOSIT = createNode(parent, "COMPOSIT");

        if (pos < input.size() && ((input.get(pos).contents).equals("not") || (input.get(pos).contents).equals("sqrt"))){
            UNOP(COMPOSIT);

            if (pos < input.size() && (input.get(pos).contents).equals("(")){
                COMPOSIT = addTerminalChild(COMPOSIT, input.get(pos).contents);
                next();

                SIMPLE(COMPOSIT);

                if (pos < input.size() && (input.get(pos).contents).equals(")")){
                    COMPOSIT = addTerminalChild(COMPOSIT, input.get(pos).contents);
                    next();
                } else {
                    report(")");
                }
            } else {
                report("(");
            }
        } else if (pos < input.size() && (((input.get(pos).contents.equals("or")) || (input.get(pos).contents.equals("and")) || (input.get(pos).contents.equals("eq")) ||
                (input.get(pos).contents.equals("grt")) || (input.get(pos).contents.equals("add")) || (input.get(pos).contents.equals("sub")) ||
                (input.get(pos).contents.equals("mul")) || (input.get(pos).contents.equals("div"))))){
            BINOP(COMPOSIT);

            if (pos < input.size() && (input.get(pos).contents).equals("(")){
                COMPOSIT =addTerminalChild(COMPOSIT,input.get(pos).contents);
                next();

                SIMPLE(COMPOSIT);

                if (pos < input.size() && (input.get(pos).contents).equals(",")){
                    COMPOSIT = addTerminalChild(COMPOSIT, input.get(pos).contents);
                    next();

                    SIMPLE(COMPOSIT);

                    if (pos < input.size() && (input.get(pos).contents).equals(")")){
                        COMPOSIT = addTerminalChild(COMPOSIT, input.get(pos).contents);
                        next();
                    } else {
                        report(")");
                    }
                } else {
                    report(",");
                }
            } else {
                report("(");
            }
        } else {
            report("Operands");
        }
    }

    private static void ARG(TreeNode parent){
        if (pos >= input.size()){
            report("Variable name, number, text or operand");
        }

        TreeNode ARG = createNode(parent, "ARG");

        if (pos < input.size() && ((input.get(pos).classification).equals("VNAME") || (input.get(pos).classification).equals("TCONST") || (input.get(pos).classification).equals("NCONST"))) {
            ATOMIC(ARG);
        } else if (pos < input.size() && (((input.get(pos).contents.equals("not")) || (input.get(pos).contents.equals("sqrt"))
                || (input.get(pos).contents.equals("or")) || (input.get(pos).contents.equals("and")) || (input.get(pos).contents.equals("eq")) ||
                (input.get(pos).contents.equals("grt")) || (input.get(pos).contents.equals("add")) || (input.get(pos).contents.equals("sub")) ||
                (input.get(pos).contents.equals("mul")) || (input.get(pos).contents.equals("div"))))){
            OP(ARG);
        } else {
            report("Variable name, number, text or operand");
        }
    }

    private static void UNOP(TreeNode parent){
        if (pos >= input.size()){
            report("Unary Operand");
        }

        TreeNode UNOP = createNode(parent, "UNOP");

        if (pos < input.size() && (input.get(pos).contents).equals("not")){
            UNOP = addTerminalChild(UNOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("sqrt")){
            UNOP = addTerminalChild(UNOP, input.get(pos).contents);
            next();
        } else {
            report("Unary Operand");
        }
    }

    private static void BINOP(TreeNode parent){
        if (pos >= input.size()){
            report("Binary Operand");
        }

        TreeNode BINOP = createNode(parent, "BINOP");

        if (pos < input.size() && (input.get(pos).contents).equals("or")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("and")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("eq")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("grt")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("add")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("sub")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("mul")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else if (pos < input.size() && (input.get(pos).contents).equals("div")){
            BINOP = addTerminalChild(BINOP, input.get(pos).contents);
            next();
        } else {
            report("Binary Operand");
        }
    }


//////////////////////////////////////HELPERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

//................................report to be ammended............................................

    private static void report(String Report){
        if (pos >= input.size()){
            System.exit(1);
        }
        System.exit(1);
    }

    private static void next(){
        if (pos < input.size()){
            pos++;
        }
    }

    private static TreeNode createNode(TreeNode parent, String nm){
        TreeNode created = new TreeNode(nm, "NonTerminal");
        created.setId(id++);
        parent.addChild(created);
        return created;
    }

    private static TreeNode addTerminalChild(TreeNode parent, String nam){
        TreeNode term = new TreeNode(nam, "Terminal");
        term.setId(id++);
        parent.addChild(term);
        return parent;
    }
}

//..............................Add print under here...........................................