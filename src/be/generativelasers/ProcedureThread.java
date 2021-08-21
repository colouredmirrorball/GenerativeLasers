package be.generativelasers;

import java.util.ArrayList;
import java.util.List;

import be.generativelasers.procedures.Procedure;
import cmb.soft.cgui.CGui;

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

    @Override
    public void run() {
        boolean interrupted = false;
        while (!interrupted) {
            try {
                for (Procedure procedure : procedures) {
                    procedure.update();
                }
                Thread.sleep(1);
            } catch (InterruptedException exception) {
                interrupt();
                interrupted = true;
            } catch (Exception exception) {
                exception.printStackTrace();
                CGui.log(exception);
            }
        }
    }

    public void addProcedure(Procedure currentProcedure)
    {
        procedures.add(currentProcedure);
    }
}
