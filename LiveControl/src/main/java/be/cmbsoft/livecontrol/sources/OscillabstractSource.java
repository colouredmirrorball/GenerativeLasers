package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ildaviewer.ProgramState;
import be.cmbsoft.ildaviewer.oscillabstract.ExternalOutput;
import be.cmbsoft.ildaviewer.oscillabstract.Oscillabstract;
import be.cmbsoft.ildaviewer.oscillabstract.Workspace;

import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;

public class OscillabstractSource extends Source
{

    private final Workspace workspace = new Workspace();

    public OscillabstractSource(ProgramState state, Oscillabstract oscillabstract)
    {
        ExternalOutput externalOutput = new ExternalOutput(state);
        workspace.getElements().add(externalOutput);
        workspace.setName("Default workspace");
        oscillabstract.registerWorkspace(workspace);
    }

    @Override
    public IldaFrame getFrame()
    {
        return EMPTY_FRAME;
    }

    @Override
    public void update()
    {

    }

    public void loadWorkspace()
    {

    }

}
