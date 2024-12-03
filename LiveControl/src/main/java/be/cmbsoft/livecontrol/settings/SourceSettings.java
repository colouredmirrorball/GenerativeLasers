package be.cmbsoft.livecontrol.settings;

import be.cmbsoft.livecontrol.sources.AudioEffectsSourceWrapper;
import be.cmbsoft.livecontrol.sources.BeamSourceWrapper;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.IldaFolderPlayerSourceWrapper;
import be.cmbsoft.livecontrol.sources.OscillabstractSourceWrapper;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = AudioEffectsSourceWrapper.AudioEffectsSettings.class, name = "audioEffects"),
    @JsonSubTypes.Type(value = BeamSourceWrapper.BeamSourceSettings.class, name = "beam"),
    @JsonSubTypes.Type(value = EmptySourceWrapper.EmptySourceSettings.class, name = "empty"),
    @JsonSubTypes.Type(value = IldaFolderPlayerSourceWrapper.IldaFolderPlayerSettings.class, name = "ildaFolderPlayer"),
    @JsonSubTypes.Type(value = OscillabstractSourceWrapper.OscillabstractSourceSettings.class,
        name = "oscillabstract"),})
public interface SourceSettings
{


}
