package be.cmbsoft.livecontrol.gui;

import be.cmbsoft.livecontrol.actions.IAction;
import processing.core.PFont;
import processing.core.PGraphics;

public interface GUIContainer
{
    int getMouseX();

    int getMouseY();

    int getPMouseX();

    int getPMouseY();

    boolean isMouseClicked();

    boolean isMouseReleased();

    boolean isMousePressed();

    boolean isMouseDragged();

    void releaseMouse();

    PGraphics getGraphics();

    PGraphics createGraphics(int width, int height);

    int getGuiStrokeColor();

    int getGuiFillColor();

    int getGuiMouseOverColor();

    int getGuiActiveColor();

    PFont getFont(int size);

    void doAction(IAction action);

    void setMouseOverInfoText(String infoText);

    void addGuiElement(GuiElement<?> element);

    float getSliderDampFactor();

    int getWidth();

    int getHeight();

    int getActiveGroupIndex();

}
