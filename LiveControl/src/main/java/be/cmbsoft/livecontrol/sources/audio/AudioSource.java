package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.sources.Source;
import processing.core.PApplet;

public abstract class AudioSource extends Source
{
    private final AudioProcessor processor;
    private final IldaRenderer   renderer;

    protected AudioSource(AudioProcessor processor, PApplet parent)
    {
        this.processor = processor;
        renderer = new IldaRenderer(parent);
    }

    protected IldaRenderer getRenderer()
    {
        return renderer;
    }

    @Override
    public IldaFrame getFrame()
    {
        return renderer.getCurrentFrame();
    }

    protected AudioProcessor getProcessor()
    {
        return processor;
    }

}
