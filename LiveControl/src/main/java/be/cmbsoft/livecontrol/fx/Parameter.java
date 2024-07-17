package be.cmbsoft.livecontrol.fx;

public class Parameter<F>
{
    private final String name;
    F value;

    public <T> Parameter(String name, Class<T> parameterType)
    {
        this.name = name;
    }

    public F getValue()
    {
        return value;
    }

    public void setValue(F value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }
}
