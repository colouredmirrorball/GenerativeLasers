package be.cmbsoft.livecontrol.fx;

public class EffectConfigurator
{

    private final EffectConfiguratorContainer parent;

    public EffectConfigurator(EffectConfiguratorContainer parent)
    {
        this.parent = parent;
    }

    public Parameter newParameter(String name)
    {
        Parameter parameter = new Parameter(name);
        registerParameter(parameter);
        return parameter;
    }

    private void registerParameter(Parameter parameter)
    {
        parent.newParameter(parameter.getName(), parameter);
    }
}
