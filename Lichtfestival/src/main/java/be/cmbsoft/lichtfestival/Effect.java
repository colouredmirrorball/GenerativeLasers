package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public abstract class Effect
{

    enum Type
    {
        FLASH, // Immediately turs off on note off
        TOGGLE // Next note turns it off or the effect runs out
    }

    private Type type;

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public abstract void initialize(Lichtfestival parent);

    public abstract void generate(IldaRenderer renderer, Lichtfestival parent);

}
