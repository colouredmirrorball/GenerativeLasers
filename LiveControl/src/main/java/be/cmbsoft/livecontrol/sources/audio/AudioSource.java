package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.sources.Source;

public abstract class AudioSource extends Source
{
    private final IldaRenderer renderer;
    private final LiveControl  parent;

    protected AudioSource(LiveControl parent)
    {
        this.parent = parent;
        renderer = new IldaRenderer(parent);
        renderer.setOptimise(true);
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
        return parent.getAudioProcessor();
    }

}
