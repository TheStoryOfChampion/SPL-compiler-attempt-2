import Lexer.Lexer;
import Parser.Parser;
import Parser.TreeNode;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String directory = "";
        int count = 1;
        try {
//////////////////////////////////////START\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//..................................LEXER....................................................
            Lexer lex = new Lexer("src/Test/Test1.txt");
//            Lexer lex = new Lexer("src/location.txt");
            System.out.println("Lexing.......................");

//             tokenArray = new ArrayList<Lexer.token>();
            ArrayList<Lexer.token> tokenArray = lex.start();
//            System.out.println(tokenArray.get(0).contents);
            System.out.println("Done Lexing..................");

//....................................PARSER..................................................
            Parser pars = new Parser(tokenArray);
            System.out.println("Parsing......................");
            TreeNode parsedTree = pars.start(tokenArray);
            String treeString = pars.print(parsedTree, "");
            writeToFile(treeString, count++);
            System.out.println("Done Parsing.................");
//.....................................DONE PARSING...........................................
//....................................SEMANTIC ANALYZER.......................................
            System.out.println("Semantic Checking............");

            System.out.println("Done Semantic Checking.......");
//....................................DONE SEMANTIC ANALYZING.................................
//.....................................CODE GENERATOR.........................................



//.....................................DONE CODE GENERATING...................................

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
