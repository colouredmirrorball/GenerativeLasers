package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public abstract class Effect
{
    public abstract void initialize(Lichtfestival parent);

    public abstract void generate(IldaRenderer renderer, Lichtfestival parent);

}
