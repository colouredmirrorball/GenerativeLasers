package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public abstract class Effect
{

    public boolean isExpired()
    {
        return false;
    }

    protected void setType(Type type)
    {
        this.type = type;
    }

    private Type type;

    public Type getType()
    {
        return type;
    }

    enum Type
    {
        FLASH, // Immediately turns off on note off
        TOGGLE // Next note turns it off or the effect runs out
    }

    public abstract void initialize(Lichtfestival parent);

    public abstract void generate(IldaRenderer renderer, Lichtfestival parent);

}
