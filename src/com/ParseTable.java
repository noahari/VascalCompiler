package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ParseTable
{

    private BufferedReader reader;
    ArrayList<String> nonterminals;
    ArrayList<String> terminals;
    ArrayList<ArrayList<Integer>> derivationcodes;

    //X is terminals, Y is token
    //999 is error, negative is epsilon production
    //otherwise push while pop

    private static boolean runningFromIntelliJ()
    {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }

    public ParseTable() throws IOException
    {
        String file;
        if (runningFromIntelliJ())
        {
            file = System.getProperty("user.dir") + "/src/com/LanguageResources/parsetable.txt";
        } else
        {
            file = System.getProperty("user.dir") + "/com/LanguageResources/parsetable.txt";
        }
        reader = new BufferedReader(new FileReader(file));
        nonterminals = new ArrayList<>();
        terminals = new ArrayList<>();
        derivationcodes = new ArrayList<>();
    }

    //assumes all nonterminals listed at top of the file in one line
    //as the x axis
    private void ReadNonterm() throws IOException
    {
        String temp = this.reader.readLine();
        String[] tempa = temp.split(",");
        this.nonterminals.addAll(Arrays.asList(tempa));
    }

    void ReadPtable() throws IOException
    {
        ReadNonterm();
        while (this.reader.ready())
        {
            ReadPline();
        }

    }

    private void ReadPline() throws IOException
    {
        char next = '~';
        ArrayList<Integer> dcodes = new ArrayList<>();
        String tnt = "";
        //first, read until comma to get y axis terminal
        while (next != ',')
        {
            next = (char) this.reader.read();
            if (next != ',')
            {
                tnt += next;
            }
        }
        this.terminals.add(tnt.toUpperCase());
        //System.out.println("TERM: " + tnt);
        //Read line, split at , to get all dcodes
        String temps = this.reader.readLine();
        //getting redundant, can make helper
        String[] tempa = temps.split(",");
        for (String iTemp : tempa)
        {
            //System.out.println("dcode: " + iTemp);
            dcodes.add(Integer.parseInt(iTemp));
        }
        this.derivationcodes.add(dcodes);
    }
}

