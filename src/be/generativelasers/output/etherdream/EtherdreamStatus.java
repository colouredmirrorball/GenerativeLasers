package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;

public class EtherdreamStatus
{
    private final int protocol;
    private final EtherdreamLightEngineState lightEngineState;
    private final EtherdreamPlaybackState playbackState;
    private final EtherdreamSource source;
    private final EtherdreamLightEngineFlags lightEngineFlags;
    private final EtherdreamPlaybackFlags playbackFlags;
    private final int sourceFlags;
    private final int bufferFullness;
    private final int pointRate;
    private final int pointCount;


    /*
    struct dac_status {
        uint8_t protocol;
        uint8_t light_engine_state;
        uint8_t playback_state;
        uint8_t source; (0 = streaming, 1 = ILDA playback from SD card, 2 = abstract generator)
        uint16_t light_engine_flags;
        uint16_t playback_flags;
        uint16_t source_flags;
        uint16_t buffer_fullness;
	    uint32_t point_rate;
	    uint32_t point_count;
    };
     */

    /**
     * Convert a byte buffer to a status
     *
     * @param buffer it is assumed that the initial position is the first position of the status
     */

    public EtherdreamStatus(ByteBuffer buffer)
    {
        protocol = buffer.get();
        lightEngineState = EtherdreamLightEngineState.get(buffer.get());
        playbackState = EtherdreamPlaybackState.get(buffer.get());
        source = EtherdreamSource.get(buffer.get());
        lightEngineFlags = new EtherdreamLightEngineFlags(buffer.getShort());
        playbackFlags = new EtherdreamPlaybackFlags(buffer.getShort());
        sourceFlags = buffer.getShort();
        bufferFullness = buffer.getShort();
        pointRate = buffer.getInt();
        pointCount = buffer.getInt();
    }

    public int getProtocol()
    {
        return protocol;
    }

    public EtherdreamLightEngineState getLightEngineState()
    {
        return lightEngineState;
    }

    public EtherdreamPlaybackState getPlaybackState()
    {
        return playbackState;
    }

    public EtherdreamSource getSource()
    {
        return source;
    }

    public EtherdreamLightEngineFlags getLightEngineFlags()
    {
        return lightEngineFlags;
    }

    public EtherdreamPlaybackFlags getPlaybackFlags()
    {
        return playbackFlags;
    }

    public int getSourceFlags()
    {
        return sourceFlags;
    }

    public int getBufferFullness()
    {
        return bufferFullness;
    }

    public int getPointRate()
    {
        return pointRate;
    }

    public int getPointCount()
    {
        return pointCount;
    }
}
