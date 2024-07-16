package be.cmbsoft.livecontrol.fx;

import java.util.List;

import be.cmbsoft.ilda.IldaFrame;

public class IldaPlayerEffect extends Effect
{
    private final List<IldaFrame> frames;
    private       int             activeFrame;

    public IldaPlayerEffect(List<IldaFrame> frames)
    {
        this.frames = frames;
    }

    @Override
    IldaFrame getFrame()
    {
        return frames.isEmpty() ? null : frames.get(activeFrame);
    }

}
