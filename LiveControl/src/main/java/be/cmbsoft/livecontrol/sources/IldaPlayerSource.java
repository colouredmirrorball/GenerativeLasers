package be.cmbsoft.livecontrol.sources;

import java.io.File;
import java.util.List;
import java.util.Optional;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaReader;
import static be.cmbsoft.laseroutput.LsxOscOutput.map;
import be.cmbsoft.livecontrol.fx.Parameter;

public class IldaPlayerSource extends Source
{
    private final List<IldaFrame> frames;
    private       int             activeFrame;
    private Parameter playbackSpeed;
    private       long lastTime;
    private final int  index = 0;

    public IldaPlayerSource(File ildaFile)
    {
        frames = ildaFile.getPath().endsWith("ild") || ildaFile.getPath().endsWith("ILD") ?
            IldaReader.readFile(ildaFile) : List.of();
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
        float speed    =
            map(Optional.ofNullable(playbackSpeed).map(Parameter::getValue).orElse(64f), 0, 127, 0.1f, 40f);
        float interval = 1000.0f / speed;
        if (System.currentTimeMillis() - lastTime > interval)
        {
            activeFrame++;
            if (activeFrame >= frames.size())
            {
                activeFrame = 0;
            }
            lastTime = System.currentTimeMillis();
        }
    }

    public void setPlaybackSpeedParameter(Parameter playbackSpeed)
    {
        this.playbackSpeed = playbackSpeed;
    }
}
