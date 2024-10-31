package be.cmbsoft.livecontrol.sources;

import java.util.Optional;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ildaviewer.IldaViewer;
import processing.core.PApplet;

import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;

public class OscillabstractSource extends Source
{
    private final IldaViewer ildaViewer;

    public OscillabstractSource()
    {
        this.ildaViewer = new IldaViewer();
        PApplet.runSketch(new String[]{""}, ildaViewer);
    }

    @Override
    public IldaFrame getFrame()
    {
        return Optional.ofNullable(ildaViewer.getCurrentOutFrame())
                       .orElse(EMPTY_FRAME);
    }

    @Override
    public void update()
    {

    }

    public void loadWorkspace()
    {

    }

}