package be.cmbsoft.livecontrol.sources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.livecontrol.SourceWrapper;

import static be.cmbsoft.livecontrol.LiveControl.log;

public class IldaFolderPlayerSourceWrapper extends SourceWrapper
{

    private final List<IldaPlayerSource> sources = new ArrayList<>();
    private final File                   folder;
    private       int                    index;

    public IldaFolderPlayerSourceWrapper(File folder)
    {
        super();
        this.folder = folder;
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
            sources.add(ildaPlayerSource);
            return ildaPlayerSource;
        }
        else
        {
            return EmptySource.INSTANCE;
        }
    }

}
