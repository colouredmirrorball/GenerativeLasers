package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class TextEffect extends Effect
{

    String text;
    float  textWidth;

    TextEffect(String text)
    {
        setType(Type.FLASH);
        this.text = text;
        setAlias("Display text " + text);
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
        renderer.text(text, renderer.width / 2 - textWidth, renderer.height / 2);

    }

}
