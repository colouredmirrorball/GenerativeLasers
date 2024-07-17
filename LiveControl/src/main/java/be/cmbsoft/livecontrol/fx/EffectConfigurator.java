package be.cmbsoft.livecontrol.fx;

public class EffectConfigurator
{

    private final EffectConfiguratorContainer parent;

    public EffectConfigurator(EffectConfiguratorContainer parent)
    {
        this.parent = parent;
    }

    public <T> Parameter<T> newParameter(String name, Class<T> parameterType)
    {
        Parameter<T> parameter = new Parameter<>(name, parameterType);
        registerParameter(parameter);
        return parameter;
    }

    private void registerParameter(Parameter<?> parameter)
    {
        parent.newParameter(parameter.getName(), parameter);
    }
}
