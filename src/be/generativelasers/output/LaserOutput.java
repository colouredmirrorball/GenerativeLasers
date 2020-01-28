package be.generativelasers.output;

import be.generativelasers.procedures.Procedure;
import ilda.IldaFrame;

/**
 * @author Florian
 * Created on 27/01/2020
 */
public abstract class LaserOutput
{
    Procedure procedure;
    public abstract void project();

    public void setProcedure(Procedure currentProcedure)
    {
        this.procedure = currentProcedure;
    }
}
