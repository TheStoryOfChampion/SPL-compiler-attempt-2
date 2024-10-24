package Semantics;
import Parser.TreeNode;

import javax.swing.plaf.FontUIResource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SemanticAnalyzer {
    private int ScopeID = 0;
    private String ScopeName = "global";
    static ArrayList<SymbolTable> symbolTable = new ArrayList<SymbolTable>();
    static ArrayList<SymbolTable> symbolTab = new ArrayList<SymbolTable>();

    public void start(TreeNode root) throws Exception{
        checkVClass(root, ScopeName, ScopeID);
        ArrayList<SymbolTable> sym = checkFunctions(root, "main", 1);

        for (int c  = 0; c < sym.size(); c++){
            symbolTable.add(sym.get(c));
        }

        checkCalls(root, "main", 1);
        String report = checkFunctionCalls(symbolTable);
        checkDeclarations(root, "main", 1);
        printST(symbolTable, "st.html", report);
    }

    public static void checkVClass(TreeNode parent, String scopeN, int scopeID){
        if (parent == null){
            return;
        }

        if (parent.type.equals("NonTerminal")){
            for (int c = 0 ; c < parent.children.size() ; c++){
                if (parent.children.get(c).name.equals("GLOBVARS")){
                    TreeNode child = parent.children.get(c).children.get(1);
                    String digits = concatDigs(child);

                    if (searchTable(parent.children.get(c).children.get(0).name + digits)){
                        symbolTable.add(new SymbolTable(parent.children.get(c).id, parent.children.get(c).children.get(0).name+digits, 0, "global"));
                    }
                    checkVClass(parent.children.get(c), scopeN, scopeID);
                }
                checkVClass(parent.children.get(c), scopeN, scopeID);
            }
        } else if (parent.type.equals("Terminal")){
            checkVClass(null, scopeN, scopeID);

        }
    }

    public static ArrayList<SymbolTable> checkFunctions(TreeNode parent, String scopeN, int scopeID){
        ArrayList<SymbolTable> functionSymbolTable = new ArrayList<SymbolTable>();
        Boolean inside = true;

        if (parent.name.equals("FUNCTIONS")){
            TreeNode node = parent.children.get(1);
            String digits = concatDigs(node);

            functionSymbolTable.add(new SymbolTable(parent.id, parent.children.get(0).name+digits, scopeID, scopeN));

            scopeN = parent.children.get(0).name+digits;
            inside = true;
        }

        if (parent.children.size() >0){
            for (TreeNode child : parent.children){
                if (inside){
                    functionSymbolTable.addAll(checkFunctions(child, scopeN, parent.id));
                } else {
                    functionSymbolTable.addAll(checkFunctions(child, scopeN, scopeID));
                }
            }
        }

        return functionSymbolTable;
    }

    public static void checkCalls(TreeNode parent, String scopeN, int scopeID){
        Boolean inside = false;

        if(parent.name.equals("FUNCTIONS")){
            TreeNode node = parent.children.get(1);
            String digits = concatDigs(node);
            scopeN = parent.children.get(0).name+digits;
            inside = true;
        }

        if (parent.children.size() > 0){
            for (TreeNode child : parent.children){
                if (child.name.equals("FUNCTION")){
                    TreeNode nodeAgain = child.children.get(2);
                    String digs = concatDigs(nodeAgain);
                    String funcName = "F_"+digs;

                    if (checkFunctionDeclarations(funcName)){
                        if (checkForParentCall(funcName, scopeID, scopeN)){
                            continue;
                        } else if (checkForSiblingCall(funcName, scopeID, scopeN)){
                            continue;
                        } else if (checkForSelfCall(funcName, scopeID, scopeN)){
                            continue;
                        } else {
                            System.out.println("FUNCTION " + funcName + " isn't defined in this scope");
                            System.exit(1);
                        }
                    } else {
                        System.out.println("FUNCTION " + funcName + "is called but not declared");
                        System.exit(1);
                    }
                    checkCalls(child, scopeN, scopeID);
                } else if (inside){
                    checkCalls(child, scopeN, parent.id);
                } else {
                    checkCalls(child, scopeN, scopeID);
                }
            }
        }
    }
    private static String checkFunctionCalls(ArrayList<SymbolTable> symTab){
        String report = "";

        for (int c = 0 ; c < symTab.size() ; c++){
            if ((symTab.get(c).NodeName).charAt(0) == 'F' && symTab.get(c).called == false){
                report += "<p style='color: red'>warning: FUNCTION " + symTab.get(c).NodeName + " id: " + symTab.get(c).NodeId +
                " that was declared here wasn't called in this scope</p> \n";
                System.out.println("\u001B Warning: FUNCTION: " + symTab.get(c).NodeName + " id: "
                        + symbolTab.get(c).NodeId + " is not called in this scope \u001B[0m");
            }
        }
        return report;
    }

    private static void checkDeclarations(TreeNode parent, String scopeN, int scopeID){
        Boolean inside = false;

        if (parent.name.equals("FUNCTIONS")){
            TreeNode node = parent.children.get(1);
            String digs = concatDigs(node);

            if (checkParentSiblingNameSimilarity("F"+digs, scopeN, scopeID)){
                System.exit(1);
            } else if (checkSiblingNameSimilarity("F"+digs, scopeN, scopeID)) {
                System.exit(1);
            } else if (checkParentChildNameSimilarity("F"+digs, scopeN, scopeID)){
                System.exit(1);
            }

            scopeN = parent.children.get(0).name+digs;
            inside = true;
        }
        if (parent.children.size() > 0){
            for (TreeNode child : parent.children){
                if (inside){
                    checkDeclarations(child, scopeN, parent.id);
                } else {
                    checkDeclarations(child, scopeN, scopeID);
                }
            }
        }
    }

    public static void printST(ArrayList<SymbolTable> symTab, String fileName, String report){
        String table = "<table>\n";
        table += "    <tr>\n";
        table += "        <th>Node ID</th>\n";
        table += "        <th>Node Name</th>\n";
        table += "        <th>Scope ID</th>\n";
        table += "        <th>Scope Name</th>\n";
        table += "    </tr>\n";

        for (SymbolTable sym : symTab){
            String row = "  <tr>\n";
            row += "        <td>" + sym.NodeId + "</td>\n";
            row += "        <td>" + sym.NodeName + "</td>\n";
            row += "        <td>" + sym.ScopeId + "</td>\n";
            row += "        <td>" + sym.ScopeName + "</td>\n";
            row += "    </tr>\n";
            table += row;
        }

        table += "</table>\n";

        try{
            BufferedWriter doc = new BufferedWriter(new FileWriter(fileName));
            doc.write("<!DOCTYPE html>\n<html>\n<head>\n<title>Symbol Table</title>\n</head>\n<body>\n");
            doc.write(table+"\n");

            if (!report.equals("")){
                doc.write(report);
            }

            doc.write("</body>\n</html>");
            doc.close();
            System.out.println("Symbol table done as HTML file: " + fileName);
        } catch (IOException e) {
            System.out.println("failed to write to file: " + e.getMessage());
        }
    }

//    ///////////////////////DEPRECIATED\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static String concatDigs(TreeNode parent){

        return "";
    }

    public static Boolean searchTable(String search){
        for (int c = 0 ; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeName.equals(search)){
                return false;
            }
        }
        return true;
    }

    public static Boolean checkFunctionDeclarations(String fName){
        for (int c = 0 ; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeName.equals(fName)){
                return true;
            }
        }
        return false;
    }

    public static Boolean checkForParentCall(String fName, int scopeID, String scopeName){
        for (int c = 0; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeName.equals(fName)){
                if (symbolTable.get(c).ScopeName.equals(scopeName)){
                    setTrueFunctionCall(fName, symbolTable.get(c).NodeId);
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean checkForSiblingCall(String fName, int scopeID, String scopeName){
        int temp = 0;

        for (int c = 0 ; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeId == scopeID && symbolTable.get(c).NodeName.equals(scopeName)){
                scopeID = symbolTable.get(c).ScopeId;
                scopeName = symbolTable.get(c).ScopeName;
                temp = c;
                break;
            }
        }
        for (int i = 0; i < symbolTable.size() ; i++){
            if (temp == i){
                continue;
            }
            if (symbolTable.get(i).NodeName.equals(fName) && scopeID==symbolTable.get(i).ScopeId){
                setTrueFunctionCall(fName, symbolTable.get(i).NodeId);
                return true;
            }
        }
        return false;
    }

    public static Boolean checkForSelfCall(String fName, int scopeID, String scopeName){
        if (fName.equals(scopeName)){
            for (int c = 0 ; c < symbolTable.size() ; c++){
                if (symbolTable.get(c).NodeId == scopeID){
                    setTrueFunctionCall(fName, symbolTable.get(c).NodeId);
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean checkParentSiblingNameSimilarity(String name, String scopeName, int scopeID){
        int temp = 0;
        if (scopeName == "main"){
            return false;
        }

        for (int c = 0; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeId == scopeID){
                scopeID = symbolTable.get(c).ScopeId;
                scopeName = symbolTable.get(c).ScopeName;
                temp = c;
                break;
            }
        }

        for (int c = 0 ; c < symbolTable.size() ; c++){
            if (temp == c){
                continue;
            }
            if (symbolTable.get(c).ScopeId == scopeID && symbolTable.get(c).ScopeName.equals(scopeName) && symbolTable.get(c).NodeName.equals(name)){
                System.out.equals("ERROR: " + name + "Can't have the same name as parent " + "(" +
                        symbolTable.get(c).NodeName + ") Functions can't have a child or sibling with the same name");

                return true;
            }
        }
        return false;
    }

    public static Boolean checkSiblingNameSimilarity(String name, String scopeName, int scopeID){
        int ctr = 0;

        for (int c = 0; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeName.equals(name) && scopeID == symbolTable.get(c).ScopeId && symbolTable.get(c).ScopeName.equals(scopeName)){
                ctr++;

                if (ctr > 1){
                    System.out.println("ERROR: Siblings with the same Function names present. Names: " + name +
                            " in scope: " + symbolTable.get(c).ScopeName);
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean checkParentChildNameSimilarity(String name, String scopeName, int scopeID){
        if (name.equals(scopeName)){
            System.out.println("ERROR: Parent and child have the same names: " + scopeName + " and "+ name
            + " and this is not allowed");
            return true;
        }
        return false;
    }

    public static void setTrueFunctionCall(String fName, int nodeId){
        for (int c = 0 ; c < symbolTable.size() ; c++){
            if (symbolTable.get(c).NodeName.equals(fName) && symbolTable.get(c).NodeId == nodeId){
                symbolTable.get(c).called = true;
            }
        }

    }
}
