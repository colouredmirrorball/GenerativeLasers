package be.generativelasers;

import be.generativelasers.output.LaserOutput;

import java.util.ArrayList;
import java.util.List;

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

}
