package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class TextEffect extends Effect
{

    String text = "Hello test!";
    float  textWidth;

    TextEffect()
    {
        setType(Type.FLASH);
    }


    public void initialize(Lichtfestival parent)
    {
        parent.textSize(parent.height / 5);
        textWidth = parent.textWidth(text) / 2;
    }


    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {
        renderer.textSize(parent.height / 5);
        renderer.stroke(255, 255, 255);
        renderer.text("Hello test!", renderer.width / 2 - textWidth, renderer.height / 2);

    }

}
