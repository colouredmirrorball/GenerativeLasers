package be.cmbsoft.livecontrol.sources;

import java.util.Optional;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ildaviewer.IldaFrameWrapper;
import be.cmbsoft.ildaviewer.ProgramState;
import be.cmbsoft.ildaviewer.oscillabstract.Oscillabstract;
import be.cmbsoft.ildaviewer.oscillabstract.Workspace;
import be.cmbsoft.ildaviewer.oscillabstract.elements.ExternalOutput;

import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;

public class OscillabstractSource extends Source
{

    private final Workspace workspace = new Workspace();
    private final Oscillabstract   oscillabstract;
    private       IldaFrameWrapper frame;

    public OscillabstractSource(ProgramState state, Oscillabstract oscillabstract)
    {
        ExternalOutput externalOutput = new ExternalOutput(state).setOutput(f -> this.frame = f);
        workspace.getElements().add(externalOutput);
        workspace.setName("Workspace " + oscillabstract.getWorkspaces().size());
        oscillabstract.registerWorkspace(workspace);
        this.oscillabstract = oscillabstract;
    }

    @Override
    public IldaFrame getFrame()
    {
        return Optional.ofNullable(frame).map(IldaFrameWrapper::getInternalFrame).orElse(EMPTY_FRAME);
    }

    @Override
    public void update()
    {
//oscillabstract is updated externally
    }

    public void loadWorkspace()
    {

    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
