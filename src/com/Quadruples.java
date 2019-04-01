package com;

import java.util.Enumeration;
import java.util.Vector;

public class Quadruples
{
    //modified so gen can directly interact with quads, other lines modified accordingly
    private Vector<Quadruple> Quadruple;
    private int nextQuad;

    public Quadruples()
    {
        Quadruple = new Vector<Quadruple>();
        nextQuad = 0;
        //MFQ
        Quadruple dummy_quadruple = new Quadruple();
        dummy_quadruple = null;
        Quadruple.add(nextQuad,dummy_quadruple);
        nextQuad++;
    }

    public String getField(int quadIndex, int field)
    {
        //MFQ
        return Quadruple.elementAt(quadIndex).getOps()[field];
    }

    public void setField(int quadIndex, int index, String field)
    {
        //MFQ
        Quadruple.elementAt(quadIndex).getOps()[index] = field;
    }

    public int getNextQuad()
    {
        return nextQuad;
    }

    public void incrementNextQuad()
    {
        nextQuad++;
    }

    public String[] getQuad(int index)
    {
        //MFQ
        return Quadruple.elementAt(index).getOps();
    }

    //amended to interact directly with Quad type
    public void addQuad(Quadruple quad)
    {
        Quadruple.add(nextQuad, quad);
        nextQuad++;
    }

    public void print()
    {
        int quadLabel = 1;
        String separator;

        System.out.println("CODE");

        Enumeration<Quadruple> e = this.Quadruple.elements();
        e.nextElement();

        while (e.hasMoreElements())
        {
            Quadruple nextQuad = e.nextElement();
            String[] quadOps = nextQuad.getOps();
            System.out.print(quadLabel + ":  " + quadOps[0]);
            for(int i = 1; i<nextQuad.getQuadSize(); i++){
                System.out.print(" " + quadOps[i]);
                if(i != nextQuad.getQuadSize() - 1){
                    System.out.print(",");
                }
                else System.out.print("");
            }
            System.out.println("");
            quadLabel++;
        }
    }
}