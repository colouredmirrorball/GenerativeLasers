package be.generativelasers;

import be.generativelasers.output.LaserOutput;

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

    public void run()
    {
        while (true)
        {
            try
            {
                for (LaserOutput output : outputs)
                {
                    output.project();
                }
                Thread.sleep(1);
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
