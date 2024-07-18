package be.cmbsoft.livecontrol.sources;

import java.io.File;
import java.util.List;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaReader;

public class IldaPlayerSource extends Source
{
    private final List<IldaFrame> frames;
    private       int             activeFrame;

    public IldaPlayerSource(File ildaFile)
    {
        frames = ildaFile.getPath().endsWith("ild") ? IldaReader.readFile(ildaFile) : List.of();
    }

    public IldaPlayerSource(List<IldaFrame> frames)
    {
        this.frames = frames;
    }

    @Override
    public IldaFrame getFrame()
    {
        return frames.isEmpty() ? null : frames.get(activeFrame);
    }

    @Override
    public void update()
    {
        activeFrame++;
        if (activeFrame >= frames.size())
        {
            activeFrame = 0;
        }
    }

}
