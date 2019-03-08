package com;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RHSTable {

    private BufferedReader reader;
    ArrayList<ArrayList<String>> rhs;

    private static boolean runningFromIntelliJ(){
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }

    public RHSTable() throws FileNotFoundException{
        String file;
        if(runningFromIntelliJ()){
            file = System.getProperty("user.dir") + "/src/com/LanguageResources/rhstable.txt";
        }
        else{
            file = System.getProperty("user.dir") + "/com/LanguageResources/rhstable.txt";
        }
        reader = new BufferedReader(new FileReader(file));
        rhs = new ArrayList<>();
    }

    public void readGrammar() throws IOException{
        while(this.reader.ready()){
            this.rhs.add(readGline());
        }
    }

    //This construction of the rhstable assumes some things about the grammar's formatting
    //There is always a space separating the left and right side of the grammar
    //A second space indicates an EPSILON

    public ArrayList<String> readGline() throws IOException{
        char last = '~';
        String tnt = "";
        boolean eflag = false;
        boolean sflag = false;
        ArrayList<String> linetokens = new ArrayList<>();
        //read until we pass the assign op for the line
        while(!eflag){
            if(last == '='){
                //skip the implicit space that comes after the :=
                this.reader.read();
                //break while. using flag to allow for debugging
                eflag = true;
            }
            else{
                //track to know when we are getting the actual derivation
                char cur = (char) this.reader.read();
                last = cur;
            }
        }
        //read until full line complete
        tnt = this.reader.readLine();
        String[] temp = tnt.split(" ");
        if((temp.length == 0) || (temp[0].equals("\n"))){
            String[] ep = {"EPSILON"};
            temp = ep;
        }
        /*for (String aTemp : temp) {
            System.out.println(aTemp);
        }*/
        linetokens.addAll(Arrays.asList(temp));
//        System.out.println("NEW ARRAY");

        /*
        while(this.reader.ready() && ()){
            while(!sflag){
                char cur = (char) this.reader.read();
                //if we haven't reached the end of the first token
                if(cur != ' '){
                    tnt += cur;
                    last = cur;
                }
                else{
                    System.out.println(tnt);
                    linetokens.add(tnt);
                    last = cur;
                    tnt = "";
                    sflag = true;
                }
            }
            sflag = false;
        }
        */
        return linetokens;
    }

    public void printGrammar(){
        int length = this.rhs.size();
        for(int i = 0; i < length; i++){
            for(int j = 0; j < rhs.get(i).size(); j++){
                System.out.println(rhs.get(i).get(j).toCharArray().length);
                System.out.println(rhs.get(i).get(j));
            }
        }
    }
}
