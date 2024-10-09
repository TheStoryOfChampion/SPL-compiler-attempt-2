import Lexer.Lexer;
import Parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String directory = "";
        int count = 1;
        try {
//////////////////////////////////////START\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//..................................LEXER....................................................
            Lexer lex = new Lexer("src/Test/Test1.txt");
            System.out.println("Lexing.......................");
            ArrayList<Lexer.token> tokenArray = new ArrayList<Lexer.token>();
            tokenArray = lex.start();
            System.out.println("Done Lexing..................");

//....................................PARSER..................................................
            Parser pars = new Parser();
            System.out.println("Parsing......................");
//            TreeNode parsedTree = parser.start(true, false);
//            String treeString = parser.printTree();
//            writeToFile(treeString, count++);
            System.out.println("Done Parsing.................");

///////////////////////////////////////END\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        } catch (Exception e) {
            System.out.println("Exited, Found Error");
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                writeToFile(e.getMessage(), count++);
            }
        }
    }

    private static void writeToFile(String str, int i)
    {
        try
        {
            String filepath = System.getProperty("user.dir")+"\\results"+i+".txt";
            File resultFile = new File(filepath);
            resultFile.createNewFile();

            FileWriter myWriter = new FileWriter(filepath, false);
            myWriter.write(str);
            myWriter.close();
            System.out.println("results saved to "+filepath);
        } catch (IOException e)
        {
            System.out.println("An error occurred when writing to file.");
//            e.printStackTrace();
        }
    }
}
