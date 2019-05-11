package com;

import java.util.Enumeration;
import java.util.Vector;

//A class to store all quads collectively
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
        Quadruple.add(nextQuad, dummy_quadruple);
        nextQuad++;
    }

    String GetField(int quadIndex, int field)
    {
        //MFQ
        return Quadruple.elementAt(quadIndex).GetOps()[field];
    }

    void SetField(int quadIndex, int index, String field)
    {
        //MFQ
        Quadruple.elementAt(quadIndex).GetOps()[index] = field;
    }

    int GetNextQuad()
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
        return Quadruple.elementAt(index).GetOps();
    }

    //amended to interact directly with Quad type
    void AddQuad(Quadruple quad)
    {
        Quadruple.add(nextQuad, quad);
        nextQuad++;
    }

    //prints out ALL quad values
    void Print()
    {
        int quadLabel = 1;
        String separator;

        System.out.println("CODE");

        Enumeration<Quadruple> e = this.Quadruple.elements();
        e.nextElement();

        while (e.hasMoreElements())
        {
            Quadruple nextQuad = e.nextElement();
            String[] quadOps = nextQuad.GetOps();
            System.out.print(quadLabel + ":  " + quadOps[0]);
            for (int i = 1; i < nextQuad.GetQuadSize(); i++)
            {
                System.out.print(" " + quadOps[i]);
                if (i != nextQuad.GetQuadSize() - 1)
                {
                    System.out.print(",");
                } else System.out.print("");
            }
            System.out.println("");
            quadLabel++;
        }
    }
}