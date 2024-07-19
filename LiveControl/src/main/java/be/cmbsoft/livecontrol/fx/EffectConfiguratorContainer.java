package be.cmbsoft.livecontrol.fx;

public interface EffectConfiguratorContainer
{
    void newParameter(String name, Parameter parameter);

    Parameter getController(int channel, int pitch);
}
