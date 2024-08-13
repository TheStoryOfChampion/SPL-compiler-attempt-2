import Lexer.Lexer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.CoderMalfunctionError;
import java.util.Scanner;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String directory = "";
        int count = 1;
        try {
            Lexer lex = new Lexer();
            System.out.println("Lexing.......................");
//            LinkedList lst = lex.start();
            System.out.println("Done Lexing..................");

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
