package be.generativelasers;

import be.generativelasers.procedures.Procedure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian
 * Created on 27/01/2020
 */
public class ProcedureThread extends Thread
{
    List<Procedure> procedures = new ArrayList<>();
    public ProcedureThread()
    {

    }

    public void run()
    {
        while (true)
        {
            try
            {
                for (Procedure procedure : procedures)
                {
                    procedure.update();
                }
                Thread.sleep(1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addProcedure(Procedure currentProcedure)
    {
        procedures.add(currentProcedure);
    }
}
