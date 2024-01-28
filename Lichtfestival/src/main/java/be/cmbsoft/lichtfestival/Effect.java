package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public abstract class Effect
{

    enum Type
    {
        FLASH, // Immediately turns off on note off
        TOGGLE // Next note turns it off or the effect runs out
    }
    private boolean expired = false;
    private Type type;
    private String alias;

    public boolean isExpired()
    {
        return expired;
    }

    public Type getType()
    {
        return type;
    }

    public String getAlias()
    {
        return alias;
    }

    public abstract void initialize(Lichtfestival parent);

    public abstract void generate(IldaRenderer renderer, Lichtfestival parent, float offset, Laser laser);

    protected void expire()
    {
        expired = true;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    protected void setType(Type type)
    {
        this.type = type;
    }
}
