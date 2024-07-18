package be.cmbsoft.livecontrol;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.laseroutput.Bounds;
import com.fasterxml.jackson.annotation.JsonSubTypes;

public class Settings
{
    public List<LsxOutputSettings>        lsxOutputs        = new ArrayList<>();
    public List<EtherdreamOutputSettings> etherdreamOutputs = new ArrayList<>();
    private List<SourceSettings> sources;
    private String midiMatrixInputDevice;
    private String midiMatrixOutputDevice;
    private String midiControlDevice;
    private String optimisationSettings;

    public List<SourceSettings> getSources()
    {
        return sources;
    }

    public void setSources(List<SourceSettings> sources)
    {
        this.sources = sources;
    }

    public String getMidiMatrixInputDevice()
    {
        return midiMatrixInputDevice;
    }

    public void setMidiMatrixInputDevice(String midiMatrixInputDevice)
    {
        this.midiMatrixInputDevice = midiMatrixInputDevice;
    }

    public String getMidiMatrixOutputDevice()
    {
        return midiMatrixOutputDevice;
    }

    public void setMidiMatrixOutputDevice(String midiMatrixOutputDevice)
    {
        this.midiMatrixOutputDevice = midiMatrixOutputDevice;
    }

    public String getMidiControlDevice()
    {
        return midiControlDevice;
    }

    public void setMidiControlDevice(String midiControlDevice)
    {
        this.midiControlDevice = midiControlDevice;
    }

    public String getOptimisationSettings()
    {
        return optimisationSettings;
    }

    public void setOptimisationSettings(String optimisationSettings)
    {
        this.optimisationSettings = optimisationSettings;
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
        private Bounds bounds;
        private boolean invertX;
        private boolean invertY;
        private float   intensity;

        public String getAlias()
        {
            return alias;
        }

        public void setAlias(String alias)
        {
            this.alias = alias;
        }

        public Bounds getBounds()
        {
            return bounds;
        }

        public void setBounds(Bounds bounds)
        {
            this.bounds = bounds;
        }

        public boolean isInvertX()
        {
            return invertX;
        }

        public void setInvertX(boolean invertX)
        {
            this.invertX = invertX;
        }

        public boolean isInvertY()
        {
            return invertY;
        }

        public void setInvertY(boolean invertY)
        {
            this.invertY = invertY;
        }

        public float getIntensity()
        {
            return intensity;
        }

        public void setIntensity(float intensity)
        {
            this.intensity = intensity;
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
