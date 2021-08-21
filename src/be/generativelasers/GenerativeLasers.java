package be.generativelasers;

import be.generativelasers.output.LaserOutput;
import be.generativelasers.output.LsxOscOutput;
import be.generativelasers.procedures.Procedure;
import be.generativelasers.procedures.test.DotOnMouse;
import be.generativelasers.ui.UIBuilder;
import cmb.soft.cgui.CGui;
import netP5.NetAddress;

/**
 * @author Florian
 * Created on 26/01/2020
 */
public class GenerativeLasers {

    private Procedure currentProcedure;
    private LaserOutput currentOutput;
    private CGui gui;

    public static final String VERSION = "0.0.1 Alpha";

    public static void main(String[] args) {
        GenerativeLasers instance = new GenerativeLasers();
        instance.gui = CGui.getInstance();
        instance.gui.setTitle("Generative Lasers");
        instance.gui.launch();
        UIBuilder.buildUI(instance.gui);
        instance.currentProcedure = new DotOnMouse(instance.gui.getDefaultWindow());
        instance.currentOutput = new LsxOscOutput(instance.gui.getDefaultWindow(), 0, 10,
            new NetAddress("127.0.0.1", 10000));
        instance.currentOutput.setProcedure(instance.currentProcedure);

        instance.run();
    }

    private void run() {
        ProcedureThread procedureThread = new ProcedureThread();
        procedureThread.addProcedure(currentProcedure);
        procedureThread.start();
        OutputThread outputThread = new OutputThread();
        outputThread.addOutput(currentOutput);
        outputThread.start();
        CGui.log("Generative Lasers version " + VERSION + " booted");
    }
}
