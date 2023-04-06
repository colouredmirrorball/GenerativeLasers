package be.generativelasers;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.laseroutput.LaserOutput;

/**
 * @author Florian Created on 27/01/2020
 */
public class OutputAggregator
{
    List<LaserOutput> outputs = new ArrayList<>();

    public void addOutput(LaserOutput output)
    {
        outputs.add(output);
    }

    public void start()
    {
        for (LaserOutput output : outputs)
        {
            output.start();
        }
    }

    public void stop()
    {
        outputs.forEach(LaserOutput::halt);
    }
}
