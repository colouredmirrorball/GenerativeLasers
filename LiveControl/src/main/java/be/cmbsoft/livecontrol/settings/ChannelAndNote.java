package be.cmbsoft.livecontrol.settings;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public record ChannelAndNote(int channel, int pitch)
{
    public static class ChannelAndNoteDeserialiser extends KeyDeserializer
    {
        @Override
        public ChannelAndNote deserializeKey(String key, DeserializationContext ctxt) throws IOException
        {
            String[] split = key.split(":");
            if (split.length != 2)
            {
                throw new IOException("Invalid key format");
            }
            return new ChannelAndNote(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
    }

    @Override
    public String toString()
    {
        return channel + ":" + pitch;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof ChannelAndNote channelAndNote && channelAndNote.channel == channel &&
            pitch == channelAndNote.pitch;
    }

    @Override
    public int hashCode()
    {
        return 1000 * channel + pitch;
    }

}
