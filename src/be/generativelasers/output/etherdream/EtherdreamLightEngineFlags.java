package be.generativelasers.output.etherdream;

import static be.generativelasers.output.etherdream.Etherdream.isFlag;

public class EtherdreamLightEngineFlags
{

    private final boolean emergencyStopDueToEStopPacketOrInvalidCommand;
    private final boolean emergencyStopDueToEStopInputToProjector;
    private final boolean emergencyStopInputToProjectorIsActive;
    private final boolean emergencyStopDueToOvertemperature;
    private final boolean overtemperatureConditionActive;
    private final boolean lossOfEthernetLink;

    public EtherdreamLightEngineFlags(short flags)
    {
        emergencyStopDueToEStopPacketOrInvalidCommand = isFlag(flags, 0);
        emergencyStopDueToEStopInputToProjector = isFlag(flags, 1);
        emergencyStopInputToProjectorIsActive = isFlag(flags, 2);
        emergencyStopDueToOvertemperature = isFlag(flags, 3);
        overtemperatureConditionActive = isFlag(flags, 4);
        lossOfEthernetLink = isFlag(flags, 5);
    }


    public boolean isEmergencyStopDueToEStopPacketOrInvalidCommand()
    {
        return emergencyStopDueToEStopPacketOrInvalidCommand;
    }

    public boolean isEmergencyStopDueToEStopInputToProjector()
    {
        return emergencyStopDueToEStopInputToProjector;
    }

    public boolean isEmergencyStopInputToProjectorIsActive()
    {
        return emergencyStopInputToProjectorIsActive;
    }

    public boolean isEmergencyStopDueToOvertemperature()
    {
        return emergencyStopDueToOvertemperature;
    }

    public boolean isOvertemperatureConditionActive()
    {
        return overtemperatureConditionActive;
    }

    public boolean isLossOfEthernetLink()
    {
        return lossOfEthernetLink;
    }
}
