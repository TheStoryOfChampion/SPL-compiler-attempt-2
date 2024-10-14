package Lexer;
import java.awt.*;
import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;
import java.lang.*;
import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.io.File;
//import java.io.Exception;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    final ArrayList<token> Tok = new ArrayList<token>();

//    private final LinkedList lst;
    private final String filePath;
    public Lexer(String pFilePath)
    {
//        lst = new LinkedList();
        filePath = pFilePath;
    }
    public class token{
        int id;
        String classification;
        String contents;
        int line;

        public token(int id, String classification, String contents, int line){
            this.id = id;
            this.classification = classification;
            this.contents = contents;
            this.line = line;
        }
    }

    public static boolean AsciiCharBetween32to127(String s) {
        for (char c : s.toCharArray()) {

            if (c >= 32 && c <= 127) {
                continue;

            } else {
                return false;
            }

        }
        return true;
    }

    private static final String ClassT ="\"[A-Z][a-z]{0,7}\"";   //Matches strings of 1 to 8 characters That start with a capitakl letter abd is within quotes
    private static final String ClassN = "0|" +                                      // Matches the literal 0
            "0\\.[0-9]*[1-9]|" +                        // Matches 0 followed by a decimal and non-zero number
            "-0\\.[0-9]*[1-9]|" +                       // Matches negative decimal numbers with at least one non-zero digit
            "[1-9][0-9]*|" +                            // Matches positive integers without leading zeros
            "-[1-9][0-9]*|" +                           // Matches negative integers without leading zeros
            "[1-9][0-9]*\\.[0-9]*[1-9]|" +              // Matches positive decimals without leading zeros, ending in a non-zero
            "-[1-9][0-9]*\\.[0-9]*[1-9]";               // Matches negative decimals, ending in a non-zero
    private static final String ClassV= "V_[a-z]([a-z]|[0-9])*";  // Matches Class V {Variable Names} e.g V_ascno34sc
    private static final String ClassF= "F_[a-z]([a-z]|[0-9])*";  // Matches Class F {Function Names} e.g F_c16s5d5

    public ArrayList<token> start() throws Exception{
        Character[] letterChars = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        Character[] letterUpperCaseChars = new Character[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Character[] nums = new Character[] { '0', '2', '1', '3', '4', '5', '6', '7', '8', '9' };
        Character tokenSymbols[] = { ';', '{', '}', '(', ')', ',', '<', '.', '=' };
        Character[] validLetters = new Character[] { 'p', 'h', 'c', 'w', 'i', 'e', 'n', 'b', 's', 'a', 'm', 'd', 'T', 'F',
                'v', 'E', 'g', 'o', 'r', 't' };

        String[] validKeywords = new String[] {"main", "num", "text", "begin", "end", "skip", "halt", "print", "input",
                "if", "then", "else", "not", "sqrt", "or", "and", "eq", "grt", "add", "sub", "mul", "div", "void"};

        List<Character> validLetter = new ArrayList<>(Arrays.asList(validLetters));
        List<Character> letteruppercase = new ArrayList<>(Arrays.asList(letterUpperCaseChars));
        List<Character> letter = new ArrayList<>(Arrays.asList(letterChars));
        List<Character> numbers = new ArrayList<>(Arrays.asList(nums));
        List<Character> _tokenSymbols = new ArrayList<>(Arrays.asList(tokenSymbols));
        List<String> keyWords = new ArrayList<>(Arrays.asList(validKeywords));

        int idNum = 0;
        final ArrayList<String> outputArr = new ArrayList<String>();
        int outputArrCount = 0;
        String message;

//        TEST PATH = "src/Test/9_test-for-correct-scoping.txt"
        File file = new File(filePath);
        try (Scanner scan = new Scanner(file)){
            int counter = 0;
            String store = "";
            int lineNumber = 1;

            while (scan.hasNextLine()){
                String str = scan.nextLine();
                char[] myChar = str.toCharArray();
                int linelength = myChar.length;

                for (int c = 0 ; c < linelength ; c++){
                    if (myChar[c] == ' ') {
                        continue;
                    }
                    if (myChar[c] == '\t') {
                        continue;
                    }
                    if (myChar[c] != '-' && myChar[c] != ' ' && myChar[c] != '=' && myChar[c] != '0'
                            && !numbers.contains(myChar[c]) && myChar[c] != '\"'
                            && !(letter.contains(myChar[c]) || numbers.contains(myChar[c]) || _tokenSymbols.contains(myChar[c])
                            || letteruppercase.contains(myChar[c]))) {
//------------------------------------------This is to check if there is a character that isn't in the grammar in the given SPL code-------------------
                        System.out.println(
                                "LEXICAL ERROR Undefined Symbol: " + myChar[c] + " line number: " + lineNumber + " in position " + (c));
//                        System.exit(1);

                        message = "LEXICAL ERROR "+ c +"(ascii "+myChar+") Unidentified error. Scanning aborted";
                        throw new Exception(message);
//                        System.exit(1);
                    }
                    else if(myChar[c] == '\"') {
//------------------------------------------Opening Quote------------------------------------------------------------------------------
//                        System.out.println("Öla");
                        int count_string_leng = 0;
                        c++;
//                        Add Opening Quote
                        store += '\"';
                        for(int i = c; i < myChar.length ; i++){
                            if(myChar[i] == '\"'){
//--------------------------------------------Check for Closing Quote----------------------------------------------------------------
                                store += '\"';
                                if (matchesClassT(store)) {
                                    System.out.println("The string matches the regex.");
                                    outputArr.add(store);
                                    idNum++;
                                    token obj = new token(idNum, "CONST", store, lineNumber);
                                    Tok.add(obj);
                                    outputArrCount++;
                                    c = i;
                                    System.out.println("Ola, adding string.." + store);
                                    break;
                                } else {
                                    System.out.println("The string does not match the regex.");
                                    System.out.println("LEXICAL ERROR: CONST LENGTH INVALID " + " line number: " + lineNumber);
                                    message = "LEXICAL ERROR: CONST LENGTH INVALID Unidentified error. Scanning aborted";
                                    c = i;
                                    throw new Exception(message);
                                }
                            } else {
                                store += myChar[i];
                                count_string_leng++;
                            }
                        }
                        if (store.charAt(store.length()-1) != '\"'){
                            System.out.println("LEXICAL ERROR: CONST HAS NO CLOSING QUOTATION MARK " + " line number: " + lineNumber);
                            message = "LEXICAL ERROR: QUOTATION MARK MISSING. Scanning aborted";
                            throw new Exception(message);
                        }
                        else if(letter.contains(myChar[c]) || letteruppercase.contains(myChar[c])){
                            if(!validLetter.contains(myChar[c])){
                                System.out.println("Danko Ola");
                                System.out.println("LEXICAL ERROR: INVALID LETTER " + " line number: " + lineNumber);
                                message = "LEXICAL ERROR: INVALID LETTER. Scanning aborted";
                                throw new Exception(message);
                            }

                            store = "";
                            store += myChar[c];
                            idNum++;
                            token obj = new token(idNum, "LETTER", store, lineNumber);
                            Tok.add(obj);
                        }
                        else if (_tokenSymbols.contains(myChar[c])){
                            store = "";
                            store += myChar[c];
                            idNum++;
                            token obj = new token(idNum, "SYMBOL", store, lineNumber);
                            Tok.add(obj);
                        }
                        store = "";
//                        System.out.println("Ola");
                    } else if (_tokenSymbols.contains(myChar[c])){
                        System.out.println("Ola, adding character "+ myChar[c]);
                        store = "";
                        store += myChar[c];
                        idNum++;
                        token obj = new token(idNum, "SYMBOL", store, lineNumber);
                        Tok.add(obj);
                    } else if(myChar[c] == 'V'){
                        System.out.println("Öla V class");
                        c++;
//                        Add Opening Quote
                        store += 'V';
                        for(int i = c; i < myChar.length ; i++){
//                            System.out.println("inside for " + myChar[i]);
                            if (myChar[i] == '_' || letter.contains(myChar[i]) || numbers.contains(myChar[i])){
                                store += myChar[i];
                                c=i;
                            }
                        }
//                        System.out.println("Match: " + store);
                        if (matchesClassV(store)) {
//                            System.out.println("Inside match");
                            System.out.println("The string matches the regex.");
                            outputArr.add(store);
                            idNum++;
                            token obj = new token(idNum, "CONST", store, lineNumber);
                            Tok.add(obj);
                            outputArrCount++;
                            System.out.println("Ola, adding string.." + store);
                            store = "";
                            break;
                        } else {
                            System.out.println("The string does not match the regex.");
                            System.out.println("LEXICAL ERROR: INVALID VARIABLE " + " line number: " + lineNumber);
                            message = "LEXICAL ERROR: VARIABLE NAME INVALID Unidentified error. Scanning aborted";
                            throw new Exception(message);
                        }
                    } else if(myChar[c] == 'F'){
                        System.out.println("Öla F class");
                        c++;
//                        Add Opening Quote
                        store += 'F';
                        for(int i = c; i < myChar.length ; i++){
//                            System.out.println("inside for " + myChar[i]);
                            if (myChar[i] == '_' || letter.contains(myChar[i]) || numbers.contains(myChar[i])){
                                store += myChar[i];
                                c=i;
                            }
                        }
//                        System.out.println("Match: " + store);
                        if (matchesClassF(store)) {
//                            System.out.println("Inside match");
                            System.out.println("The string matches the regex.");
                            outputArr.add(store);
                            idNum++;
                            token obj = new token(idNum, "CONST", store, lineNumber);
                            Tok.add(obj);
                            outputArrCount++;
                            System.out.println("Ola, adding Function " + store);
                            store = "";
                            break;
                        } else {
                            System.out.println("The string does not match the regex.");
                            System.out.println("LEXICAL ERROR: INVALID FUNCTION NAME " + " line number: " + lineNumber);
                            message = "LEXICAL ERROR: FUNCTION NAME INVALID Unidentified error. Scanning aborted";
                            throw new Exception(message);
                        }
                    } else if(myChar[c] == '-' || myChar[c] == '.' || numbers.contains(myChar[c])){
                        System.out.println("Öla N class");
//                        Add Opening Quote
                        store += myChar[c];
                        c++;
                        for(int i = c; i < myChar.length ; i++){
//                            System.out.println("inside for " + myChar[i]);
                            if (myChar[i] == '-' || numbers.contains(myChar[i]) || myChar[i] == '.'){
                                store += myChar[i];
                                c=i;
                            }
                        }
//                        System.out.println("Match: " + store);
                        if (matchesClassN(store)) {
//                            System.out.println("Inside match");
                            System.out.println("The string matches the regex.");
                            outputArr.add(store);
                            idNum++;
                            token obj = new token(idNum, "CONST", store, lineNumber);
                            Tok.add(obj);
                            outputArrCount++;
                            System.out.println("Ola, adding number " + store);
                            System.out.println("---------------");
                            store = "";
                            break;
                        } else {
                            System.out.println("The string does not match the regex.");
                            System.out.println("LEXICAL ERROR: INVALID NUMBER " + " line number: " + lineNumber);
                            message = "LEXICAL ERROR: NUMBER IS INVALID Unidentified error. Scanning aborted";
                            throw new Exception(message);
                        }
                    }
                    lineNumber++;
                }
            }
        }
        return Tok;
    }

    private static boolean matchesClassT(String input) {
        Pattern classT = Pattern.compile(ClassT);
        Matcher classTmatcher = classT.matcher(input);
        return classTmatcher.matches();
    }

    private static boolean matchesClassV(String input) {
        Pattern classV = Pattern.compile(ClassV);
        Matcher classVmatcher = classV.matcher(input);
        return classVmatcher.matches();
    }

    private static boolean matchesClassF(String input) {
        Pattern classF = Pattern.compile(ClassF);
        Matcher classFmatcher = classF.matcher(input);
        return classFmatcher.matches();
    }

    private static boolean matchesClassN(String input) {
        Pattern classN = Pattern.compile(ClassN);
        Matcher classNmatcher = classN.matcher(input);
        return classNmatcher.matches();
    }
}
