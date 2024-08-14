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
    public ArrayList<token> start() throws Exception{

        return Tok;
    }
}
