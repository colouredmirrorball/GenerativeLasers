package be.cmbsoft.livecontrol.sources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.cmbsoft.livecontrol.LiveControl;
import static be.cmbsoft.livecontrol.LiveControl.log;
import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.fx.Parameter;
import be.cmbsoft.livecontrol.settings.SourceSettings;

public class IldaFolderPlayerSourceWrapper extends SourceWrapper
{

    public record IldaFolderPlayerSettings(String ildaFolder) implements SourceSettings
    {

    }

    private final List<IldaPlayerSource> sources = new ArrayList<>();
    private final File                   folder;
    private       int                    index;
    private final Parameter   playbackSpeed;

    public IldaFolderPlayerSourceWrapper(File folder, LiveControl parent)
    {
        super();
        this.folder = folder;
        playbackSpeed = new Parameter("Playback speed");
        parent.newParameter("Playback speed", playbackSpeed);
    }


    @Override
    public SourceSettings getSettings()
    {
        return new IldaFolderPlayerSettings(Optional.ofNullable(folder).map(File::getAbsolutePath).orElse(null));
    }

    @Override
    protected Source provideNextSource()
    {
        /*
         * I chose not to cache the folder, so hot swapping is possible. Might impact performance?
         */
        if (folder == null)
        {
            log("No folder for ILDA folder player");
            return EmptySource.INSTANCE;
        }
        File[] files = folder.listFiles();
        if (files == null)
        {
            log("No files in folder " + folder);
            return EmptySource.INSTANCE;
        }
        index++;
        if (index >= files.length)
        {
            index = 0;
        }
        return getSource(files);
    }

    @Override
    protected Source providePreviousSource()
    {
        File[] files = folder.listFiles();
        if (files == null)
        {
            log("No files in folder " + folder);
            return EmptySource.INSTANCE;
        }
        index--;
        if (index < 0)
        {
            index = files.length - 1;
        }
        return getSource(files);
    }

    private Source getSource(File[] files)
    {
        if (index < sources.size())
        {
            return sources.get(index);
        }
        if (index < files.length)
        {
            File             nextFile         = files[index];
            IldaPlayerSource ildaPlayerSource = new IldaPlayerSource(nextFile);
            ildaPlayerSource.setPlaybackSpeedParameter(playbackSpeed);
            sources.add(ildaPlayerSource);
            return ildaPlayerSource;
        }
        else
        {
            return EmptySource.INSTANCE;
        }
    }

}
