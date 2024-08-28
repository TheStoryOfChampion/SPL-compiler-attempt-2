package Lexer;
import java.util.Arrays;
import java.lang.*;
import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
    public ArrayList<token> start() throws FileNotFoundException{
        Character[] letterChars = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        Character[] letterUpperCaseChars = new Character[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Character[] nums = new Character[] { '0', '2', '1', '3', '4', '5', '6', '7', '8', '9' };
        Character tokenSymbols[] = { ';', '{', '}', '(', ')', ',', '-', '*', '<', '.', '=' };
        Character[] validLetters = new Character[] { 'p', 'h', 'c', 'w', 'i', 'e', 'n', 'b', 's', 'a', 'm', 'd', 'T', 'F',
                'v', 'E', 'g', 'o', 'r', 't' };

        List<Character> validLetter = new ArrayList<>(Arrays.asList(validLetters));
        List<Character> letteruppercase = new ArrayList<>(Arrays.asList(letterUpperCaseChars));
        List<Character> letter = new ArrayList<>(Arrays.asList(letterChars));
        List<Character> numbers = new ArrayList<>(Arrays.asList(nums));
        List<Character> _tokenSymbols = new ArrayList<>(Arrays.asList(tokenSymbols));

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

                for (int c = 0 ; c < myChar.length ; c++){
                    if (myChar[c] == ' ') {
                        continue;
                    }
                    if (myChar[c] == '\t') {
                        continue;
                    }
                    if (myChar[c] != ' ' && myChar[c] != '*' && myChar[c] != ':' && myChar[c] != '=' && myChar[c] != '0'
                            && !numbers.contains(myChar[c]) && myChar[c] != '\"'
                            && !(letter.contains(myChar[c]) || numbers.contains(myChar[c]) || _tokenSymbols.contains(myChar[c])
                            || letteruppercase.contains(myChar[c]))) {
                        System.out.println(
                                "LEXICAL ERROR Undefined Symbol: " + myChar[c] + " line number: " + lineNumber + " in position " + (c));
//                        System.exit(1);

                        message = "LEXICAL ERROR "+ c +"(ascii "+myChar+") Unidentified error. Scanning aborted";
                        throw new FileNotFoundException(message);
                    }
                    else if(myChar[c] == '\"') {
                        int count_string_leng = 0;
                        c++;
                        store += '\"';
                        for(int i = c; i < myChar.length ; c++){
                            if(myChar[i] == '\"'){
                                store += '\"';
                                Boolean status = AsciiCharBetween32to127(store);
                                if(status == true ){
                                    if(count_string_leng > 8){
                                        c = i;
                                        break;
                                    }
                                    else if(count_string_leng == 8){
                                        outputArr.add(store);
                                        idNum++;
                                        token obj = new token(idNum, "string", store, lineNumber);
                                        Tok.add(obj);
                                        outputArrCount++;
                                        c = i;
                                        break;
                                    }
                                }
                                else {

                                }
                            }
                        }
                    }
                }
            }
        }




        return Tok;
    }
}
