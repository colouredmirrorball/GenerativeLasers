package be.generativelasers.emoji;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.OutputOption;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class EmojiProjector extends PApplet {

    IldaRenderer renderer;
    LaserOutput output;

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{EmojiProjector.class.getPackageName()};
        EmojiProjector emojiProjector = new EmojiProjector();
        PApplet.runSketch(appletArgs, emojiProjector);
    }

    @Override
    public void settings() {
        size(600, 600, P3D);
    }

    @Override
    public void setup() {
        PFont font = createFont("Arial", 100);
        renderer = new IldaRenderer(this);
        renderer.beginDraw();
        renderer.stroke(200);
        renderer.textFont(font);
//        renderer.textSize(100);
        renderer.textAlign(PConstants.CENTER);
        renderer.text("😊", 300, 300);
        renderer.endDraw();

        output = new EtherdreamOutput().setAlias("6E851F3F2177");
//        output.option(OutputOption.INVERT_X);
        output.option(OutputOption.INVERT_Y);
    }

    @Override
    public void draw() {
        background(0);
        IldaFrame currentFrame = renderer.getCurrentFrame();
        currentFrame.renderFrame(this);
        output.project(currentFrame);
    }

}
