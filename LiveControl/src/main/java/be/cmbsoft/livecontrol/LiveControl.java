package be.cmbsoft.livecontrol;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import static be.cmbsoft.livecontrol.ui.UIBuilder.buildUI;
import cmbsoft.cgui.CGui;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import processing.core.PApplet;

public class LiveControl extends PApplet
{

    private final Settings               settings;
    private final File                   settingsFile = new File("settings.json");
    private final ObjectMapper           objectMapper;
    private final Map<UUID, LaserOutput> outputs      = new HashMap<>();
    public LiveControl()
    {
        Settings settings1;
        log("Hello there! This is Generative Lasers.");
        objectMapper = new ObjectMapper();
        try {
            if (settingsFile.exists()) {
                settings1 = objectMapper.readValue(settingsFile, Settings.class);
            } else {
                settings1 = new Settings();
            }
        } catch (IOException e) {
            settings1 = new Settings();
            error(e);
            log("Could not initialise settings...");
        }
        settings = settings1;
    }

    public static void main(String[] passedArgs)
    {
        String[]    appletArgs  = new String[]{LiveControl.class.getPackageName()};
        LiveControl liveControl = new LiveControl();
        PApplet.runSketch(appletArgs, liveControl);
        CGui instance = CGui.getInstance();
        instance.setTitle("LiveControl UI");
        instance.launch();
    }

    public static void log(String what)
    {
        LogManager.getLogger(LiveControl.class).info(what);
    }

    @Override
    public void settings()
    {
        size(1920, 1080);
    }

    public static void error(Exception exception)
    {
        LogManager.getLogger(LiveControl.class).error(exception);
    }

    @Override
    public void draw()
    {
        background(0);
    }

    @Override
    public void setup()
    {
        surface.setResizable(true);
        buildUI(CGui.getInstance(), this);
    }

    @Override
    public void exit()
    {
        outputs.values().forEach(LaserOutput::halt);
        saveSettings();
//        if (midiDevice != null) {
//            close();
//        }
        super.exit();
    }

    public void addOutput(UUID uuid)
    {
        outputs.put(uuid, new EtherdreamOutput());
    }

    public void removeOutput(UUID uuid)
    {
        outputs.get(uuid).halt();
        outputs.remove(uuid);
    }

    private void saveSettings()
    {
        try {
            if (!settingsFile.exists() && !settingsFile.createNewFile()) {
                log("Could not create settings file!");
            }
            objectMapper.writeValue(settingsFile, settings);
            log("Persisted the settings");
        } catch (IOException e) {
            error(e);
            log("Could not write settings file...");
        }
    }
}
