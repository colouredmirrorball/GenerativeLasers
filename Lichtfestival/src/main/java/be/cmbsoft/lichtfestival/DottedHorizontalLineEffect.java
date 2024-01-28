package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import static processing.core.PApplet.map;
import static processing.core.PConstants.POINTS;

public class DottedHorizontalLineEffect extends Effect
{

    private static final int POINTCOUNT = 15;

    DottedHorizontalLineEffect()
    {
        setType(Type.FLASH);
        setAlias("Static dotted horizontal line");
    }


    public void initialize(Lichtfestival parent)
    {

    }


    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset, Laser laser)
    {
        renderer.stroke(255, 255, 255);
        renderer.beginShape(POINTS);
        for (int i = 0; i < POINTCOUNT; i++) {
            renderer.vertex(map(i, 0, POINTCOUNT, 0, parent.width), offset);
        }
        renderer.endDraw();
    }

}
