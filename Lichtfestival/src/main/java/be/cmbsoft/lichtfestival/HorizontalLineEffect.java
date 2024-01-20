package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class HorizontalLineEffect extends Effect
{

    HorizontalLineEffect()
    {
        setType(Type.FLASH);
    }


    public void initialize(Lichtfestival parent)
    {

    }


    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {

        renderer.stroke(255, 255, 255);
        renderer.line(0, renderer.height / 2, renderer.width, renderer.height / 2);

    }

}
