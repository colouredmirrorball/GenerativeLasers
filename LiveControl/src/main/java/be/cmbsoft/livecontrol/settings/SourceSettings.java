package be.cmbsoft.livecontrol.settings;

public class SourceSettings
{
    private String     ildaFolder;
    private SourceType sourceType;

    public SourceType getType()
    {
        return sourceType;
    }

    public void setType(SourceType sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getIldaFolder()
    {
        return ildaFolder;
    }

    public void setIldaFolder(String ildaFolder)
    {
        this.ildaFolder = ildaFolder;
    }

}
