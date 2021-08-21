package be.generativelasers;

import be.generativelasers.output.LaserOutput;
import cmb.soft.cgui.CGui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian
 * Created on 27/01/2020
 */
public class OutputThread extends Thread
{
    List<LaserOutput> outputs = new ArrayList<>();

    public void addOutput(LaserOutput output)
    {
        outputs.add(output);
    }

    @Override
    public void run()
    {
        boolean interrupted = false;
        while (!interrupted)
        {
            try {
                for (LaserOutput output : outputs) {
                    output.project();
                }
                Thread.sleep(1);
            }  catch (InterruptedException exception) {
                interrupted = true;
                interrupt();
            } catch (Exception exception) {
                CGui.log(exception.getMessage());
                exception.printStackTrace();
            }
        }
    }
}
