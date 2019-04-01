package com;

import java.util.ArrayList;

//A quadruple contains one single TVI Code and all its associated ops
//called a quad bc at most 4 ops
public class Quadruple {

    //originally used arraylist but since
    // there is a max size array is just faster
    private String[] ops;
    private int opCount = 0;

    //null generic instantiator
    Quadruple(){}

    public Quadruple(String op1){

        opCount = 1;
        ops = new String[1];

        ops[0] = (op1);
    }

    public Quadruple(String op1, String op2){

        opCount = 2;
        ops = new String[2];

        ops[0] = (op1);
        ops[1] = (op2);
    }

    public Quadruple(String op1, String op2, String op3){

        opCount = 3;
        ops = new String[3];

        ops[0] = (op1);
        ops[1] = (op2);
        ops[2] = (op3);
    }

    public Quadruple(String op1, String op2, String op3, String op4){

        opCount = 4;
        ops = new String[4];

        ops[0] = (op1);
        ops[1] = (op2);
        ops[2] = (op3);
        ops[3] = (op4);
    }

    //Getters and Setters
    //No setter for quad size since it is set as we go
    //The same goes for get ops since any setter access would
    //be unnecessary/create an error
    public int getQuadSize(){
        return opCount;
    }

    public String[] getOps(){
        return ops;
    }

    public void printTVI(){
        System.out.print("QUAD: ");
        for(String op : ops){
            System.out.print(op + " ");
        }
        System.out.println("");
    }
}
