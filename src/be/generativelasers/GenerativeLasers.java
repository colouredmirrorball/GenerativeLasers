package be.generativelasers;

import be.generativelasers.procedures.Procedure;
import be.generativelasers.procedures.trees.Trees;
import cmb.soft.cgui.CGui;

/**
 * @author Florian
 * Created on 26/01/2020
 */
public class GenerativeLasers
{
    Procedure currentProcedure;
    CGui gui;

    public static void main(String[] args)
    {
        GenerativeLasers instance = new GenerativeLasers();
        instance.gui = new CGui();
        instance.gui.setTitle("Generative Lasers");
        instance.gui.launch();
        instance.currentProcedure = new Trees(instance.gui.getDefaultWindow());
        instance.run();
    }

    private void run()
    {
       new ProcedureThread().start();
       new OutputThread().start();
    }
}
