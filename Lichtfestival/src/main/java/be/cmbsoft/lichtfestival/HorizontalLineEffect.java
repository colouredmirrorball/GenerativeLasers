package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

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
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset, Laser laser)
    {

        renderer.stroke(255, 255, 255);
        renderer.line(0, offset, renderer.width, offset);

    }

}
