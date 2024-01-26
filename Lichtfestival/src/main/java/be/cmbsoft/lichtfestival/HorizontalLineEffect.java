package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import static processing.core.PApplet.map;

public class HorizontalLineEffect extends Effect
{

    HorizontalLineEffect()
    {
        setType(Type.FLASH);
        setAlias("Static horizontal line");
    }


    public void initialize(Lichtfestival parent)
    {

    }


    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset)
    {

        renderer.stroke(255, 255, 255);
        offset = map(offset, 0, 1, 0, parent.height);
        renderer.line(0, offset, renderer.width, offset);

    }

}
