package be.cmbsoft.livecontrol.fx;

public class Parameter
{
    private final String name;
    float value;

    public Parameter(String name)
    {
        this.name = name;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue(float value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }
}
