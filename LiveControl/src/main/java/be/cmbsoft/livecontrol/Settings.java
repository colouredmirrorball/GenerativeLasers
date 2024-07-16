package be.cmbsoft.livecontrol;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;

public class Settings
{
    public List<LsxOutputSettings>        lsxOutputs        = new ArrayList<>();
    public List<EtherdreamOutputSettings> etherdreamOutputs = new ArrayList<>();
    private List<SourceSettings> sources;

    public List<SourceSettings> getSources()
    {
        return sources;
    }

    public void setSources(List<SourceSettings> sources)
    {
        this.sources = sources;
    }


    public static class LsxOutputSettings extends OutputSettings
    {

        int timeline;
        int frameNumber;

        public int getFrameNumber()
        {
            return frameNumber;
        }

        public void setFrameNumber(int frameNumber)
        {
            this.frameNumber = frameNumber;
        }

        public int getTimeline()
        {
            return timeline;
        }

        public void setTimeline(int timeline)
        {
            this.timeline = timeline;
        }

    }

    public static class EtherdreamOutputSettings extends OutputSettings
    {
        String alias;

        public String getAlias()
        {
            return alias;
        }

        public void setAlias(String alias)
        {
            this.alias = alias;
        }

    }


    @JsonSubTypes({
        @JsonSubTypes.Type(value = EtherdreamOutputSettings.class,
            name = "Etherdream"),
        @JsonSubTypes.Type(value = LsxOutputSettings.class,
            name = "LSX")
    })
    public static class OutputSettings
    {
        String host;
        int    port;

        public String getHost()
        {
            return host;
        }

        public void setHost(String host)
        {
            this.host = host;
        }

        public int getPort()
        {
            return port;
        }

        public void setPort(int port)
        {
            this.port = port;
        }

    }
}
