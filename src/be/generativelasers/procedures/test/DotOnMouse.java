package be.generativelasers.procedures.test;

import be.generativelasers.procedures.Procedure;
import processing.core.PApplet;

/**
 * @author Florian
 * Created on 24/07/2020
 */
public class DotOnMouse extends Procedure {

    public DotOnMouse(PApplet applet) {
        super(applet);
    }

    @Override
    public void update() {
        renderer.beginDraw();
        renderer.stroke(255, 255, 255);
        for (int i = 0; i < 250; i++)
        {
            renderer.point(parent.mouseX, parent.mouseY);
        }

        renderer.endDraw();
        frame = renderer.getCurrentFrame();
    }

    @Override
    public void trigger(float value) {

    }
}
