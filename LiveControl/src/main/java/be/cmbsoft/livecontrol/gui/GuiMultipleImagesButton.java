package be.cmbsoft.livecontrol.gui;


import org.jetbrains.annotations.NotNull;

import processing.core.PGraphics;
import processing.core.PImage;

import static be.cmbsoft.ildaviewer.Utilities.blue;
import static be.cmbsoft.ildaviewer.Utilities.color;
import static be.cmbsoft.ildaviewer.Utilities.green;
import static be.cmbsoft.ildaviewer.Utilities.red;
import static be.cmbsoft.ildaviewer.Utilities.requireValue;

/**
 * GuiButton where the image changes dependent on the state of the button (idle, mouseover, clicked)
 */
public class GuiMultipleImagesButton extends GuiButton
{
    private final PImage[] images;

    /**
     * Using this constructor, the single image will be used to generate the other images based on the GUI
     * configuration in the parent GUIContainer.
     */
    public GuiMultipleImagesButton(GUIContainer parent, @NotNull PImage image, String name)
    {
        super(parent, name);
        images = generateImages(requireValue(image, "Image cannot be null"));
    }

    private PImage[] generateImages(@NotNull PImage baseImage)
    {
        return new PImage[]{recolourImage(baseImage, parent.getGuiStrokeColor()), recolourImage(baseImage,
            parent.getGuiMouseOverColor()), recolourImage(baseImage, parent.getGuiActiveColor())};
    }

    private PImage recolourImage(@NotNull PImage baseImage, int colour)
    {
        PGraphics image = parent.createGraphics(baseImage.width, baseImage.height);
        //recolour image to parent stroke colour:
        image.beginDraw();
        image.loadPixels();
        baseImage.loadPixels();
        int color = color(255, red(colour), green(colour), blue(colour));
        for (int i = 0; i < image.pixels.length; i++)
        {
            image.pixels[i] = baseImage.pixels[i] == 0 ? 0 : color;
        }
        image.updatePixels();
        image.endDraw();
        return image;
    }

    public GuiMultipleImagesButton(GUIContainer parent, @NotNull PImage defaultImage, PImage mouseOverImage,
        PImage clickedImage, String name)
    {
        super(parent, name);
        this.images = new PImage[]{defaultImage, mouseOverImage, clickedImage};
    }

    @Override
    public void display(PGraphics graphics)
    {
        graphics.stroke(parent.getGuiStrokeColor());
        graphics.rect(x, y, width, height);
        PImage image = images[0];
        if (mouseOver)
        {
            image = images[1];
        }
        if (clicked)
        {
            image = images[2];
        }
        graphics.image(image, x, y);
    }

}
