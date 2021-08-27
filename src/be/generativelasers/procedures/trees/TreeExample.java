package be.generativelasers.procedures.trees;

import be.generativelasers.procedures.Procedure;
import cmb.soft.cgui.CGui;
import processing.core.PApplet;
import processing.core.PGraphics;

import static processing.core.PApplet.radians;

/**
 * Code from the Processing example files.
 * <p>
 * Recursive Tree by Daniel Shiffman.
 * <p>
 * Renders a simple tree-like structure via recursion. The branching angle is calculated as a function of the horizontal
 * mouse location. Move the mouse left and right to change the angle.
 * <p>
 * <p>
 * ====================================================
 * <p>
 * This produces way too many points, and there is some weird overflow going on in LSX. Could be the OSC message just
 * turning into garbage with such long messages, or the translate() method in the IldaRenderer not working...
 */

public class TreeExample extends Procedure
{

    float theta;


    public TreeExample(PApplet applet)
    {
        super(applet);
    }

    @Override
    public void updateRender()
    {
        renderer.background(0);

        renderer.stroke(255);
        // Let's pick an angle 0 to 90 degrees based on the mouse position
        float a = (parent.mouseX / (float) renderer.width) * 90f;
        // Convert it to radians
        theta = radians(a);
        // Start the tree from the bottom of the screen
        renderer.translate(renderer.width / 2, renderer.height);
        // Draw a line 120 pixels
        renderer.line(0, 0, 0, -120);
        // Move to the end of that line
        renderer.translate(0, -120);
        // Start the recursive branching!
        branch(60, renderer);
        CGui.log(String.valueOf(renderer.getCurrentFrame().getPointCount()));

    }

    private void branch(float h, PGraphics renderer)
    {
        // Each branch will be 1/3rds the size of the previous one
        h *= 0.66;

        // All recursive functions must have an exit condition!!!!
        // Here, ours is when the length of the branch is 2 pixels or less
        if (h > 20)
        {
            renderer.pushMatrix();    // Save the current state of transformation (i.e. where are we now)
            renderer.rotate(theta);   // Rotate by theta
            renderer.line(0, 0, 0, -h);  // Draw the branch
            renderer.translate(0, -h); // Move to the end of the branch
            branch(h, renderer);       // Ok, now call myself to draw two new branches!!
            renderer.popMatrix();     // Whenever we get back here, we "pop" in order to restore the previous matrix state

            // Repeat the same thing, only branch off to the "left" this time!
            renderer.pushMatrix();
            renderer.rotate(-theta);
            renderer.line(0, 0, 0, -h);
            renderer.translate(0, -h);
            branch(h, renderer);
            renderer.popMatrix();
        }
    }

    @Override
    public void trigger(float value)
    {

    }
}
