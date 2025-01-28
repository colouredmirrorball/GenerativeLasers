package be.cmbsoft.livecontrol.gui;


import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * GuiButton but with an image instead of text
 * Created by Florian on 11/11/2017.
 */
public class GuiImageButton extends GuiButton
{
    PImage image;

    private boolean displayName  = false;
    private int     namePosition = PConstants.RIGHT;


    public GuiImageButton(GUIContainer parent, PImage image, String name)
    {
        super(parent, name);
        this.image = image;
        if (image == null)
        {
            displayName = true;
        }

    }

    @Override
    public void display(PGraphics graphics)
    {

        graphics.strokeWeight(strokeWeight);
        graphics.textFont(parent.getFont(16));
        if (mouseOver)
        {
            graphics.stroke(graphics.red(mouseovercolour), graphics.green(mouseovercolour),
                graphics.blue(mouseovercolour));
        }
        else {graphics.stroke(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));}

        //if(clicked) graphics.fill(graphics.red(activecolour), graphics.green(activecolour), graphics.blue
        // (activecolour));
        //else
        graphics.noFill();

        graphics.rect(x, y, width, height, 3);
        if (displayName)
        {
            if (clicked) {graphics.fill(0);}
            else
            {
                graphics.fill(graphics.red(strokecolour), graphics.green(strokecolour),
                    graphics.blue(strokecolour));
            }

            int w = 0, h = 0;
            if (image != null)
            {
                w = image.width;
                h = image.height;
            }
            switch (namePosition)
            {
                case PConstants.LEFT:
                    if (image != null) graphics.image(image, x + 3, y + 3);
                    graphics.textAlign(PConstants.RIGHT, PConstants.CENTER);
                    graphics.text(title, x + 3, y + 3, width - w - 3, height);
                    break;
                case PConstants.TOP:
                    if (image != null) graphics.image(image, x + 3, y + height - h);
                    graphics.textAlign(PConstants.CENTER, PConstants.CENTER);
                    graphics.text(title, x + 3, y + 3, width, height - h);
                    break;
                case PConstants.BOTTOM:
                    if (image != null) graphics.image(image, x + 3, y + 3);
                    graphics.textAlign(PConstants.CENTER, PConstants.CENTER);
                    graphics.text(title, x + 3, y + 3 + h, width, height);
                    break;
                case PConstants.RIGHT:
                default:
                    if (image != null) graphics.image(image, x + 3, y + 3);
                    graphics.textAlign(PConstants.LEFT, PConstants.CENTER);
                    graphics.text(title, x + w + 13, y + 3, width - w - 13, height);
                    break;
            }

        }
        else if (image != null) graphics.image(image, x + 3, y + 3);

    }

    public GuiImageButton recolourImage(PGraphics graphics)
    {
        if (image != null)
        {
            //recolour image to parent stroke colour:
            image.loadPixels();
            for (int i = 0; i < image.pixels.length; i++)
            {
                image.pixels[i] = image.pixels[i] == 0 ? 0 : graphics.color(graphics.red(strokecolour),
                    graphics.green(strokecolour), graphics.blue(strokecolour));
            }
            image.updatePixels();
        }
        return this;
    }

    @Override
    public GuiImageButton setSize(int sx, int sy)
    {
        this.width = sx;
        this.height = sy;
        if (image != null)
        {
            if (displayName)
            {
                switch (namePosition)
                {
                    case PConstants.LEFT:
                    case PConstants.RIGHT:
                    default:

                        image.resize(0, sy - 6);
                        break;
                    case PConstants.TOP:
                    case PConstants.BOTTOM:
                        image.resize(sx - 6, 0);
                        break;
                }
            }
            else {image.resize(sx - 6, sy - 6);}
        }
        return this;
    }

    public PImage getImage()
    {
        return image;
    }

    public GuiImageButton setImage(PImage image)
    {
        this.image = image;
        return this;
    }

    public boolean isDisplayName()
    {
        return displayName;
    }

    public GuiImageButton setDisplayName(boolean displayName)
    {
        this.displayName = displayName;
        return this;
    }

    public int getNamePosition()
    {
        return namePosition;
    }

    public GuiImageButton setNamePosition(int namePosition)
    {
        this.namePosition = namePosition;
        return this;
    }

}
