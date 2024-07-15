package be.cmbsoft.livecontrol.ui;

import java.util.HashMap;
import java.util.Map;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.actions.AddOutput;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import controlP5.Tab;

public class UIBuilder
{

    public static void buildUI(ControlP5 ui, LiveControl liveControl)
    {
        Map<ControllerInterface, PositionCalculator> positions = new HashMap<>();

        int backgroundColor = liveControl.color(20, 20, 20);
        int foregroundColor = liveControl.color(50, 70, 128);
        int activeColor     = liveControl.color(80, 128, 208);
        int fontColor       = liveControl.color(200, 200, 255);
        int tabHeight       = 64;

        ui.setColorBackground(backgroundColor);
        ui.setColorForeground(foregroundColor);
        ui.setColorActive(activeColor);
        ui.setColorCaptionLabel(fontColor);

        Tab global = ui.getTab("global");

        Tab play = ui.getTab("default");
        play.setTitle("Play").setHeight(tabHeight);

        Tab output = ui.addTab("Outputs").setHeight(tabHeight);
        positions.put(ui.addButton("Detect")
                        .moveTo(output)
                        .setWidth(256)
                        .setHeight(80)
                        .addCallback(listener -> liveControl.doAction(listener, new AddOutput(liveControl))),
            upperLeft(10, 128));

        Tab settings = ui.addTab("Settings").setHeight(tabHeight);

        Tab about = ui.addTab("About").setHeight(tabHeight);

        positions.put(ui.addButton("Undo")
                        .addCallback(listener -> liveControl.doAction(listener, liveControl::undo))
                        .setImages(liveControl.getIcons("undo", 64, 64, backgroundColor, foregroundColor, activeColor))
                        .moveTo(global)
                        .setWidth(64)
                        .setHeight(64), upperRight(84, 10));
        positions.put(ui.addButton("Redo")
                        .addCallback(listener -> liveControl.doAction(listener, liveControl::redo))
                        .setImages(liveControl.getIcons("redo", 64, 64, backgroundColor, foregroundColor, activeColor))
                        .moveTo(global)
                        .setWidth(64)
                        .setHeight(64), upperRight(10, 10));

        liveControl.setUIPositions(positions);
        liveControl.updateUIPositions();

    }

    private UIBuilder()
    {

    }

    private static PositionCalculator upperLeft(int x, int y)
    {
        PositionCalculator calculator = new PositionCalculator();
        calculator.setOffsetX(x);
        calculator.setOffsetY(y);
        calculator.setType(PositionType.UPPER_LEFT_ANCHOR);
        return calculator;
    }

    private static PositionCalculator upperRight(int x, int y)
    {
        PositionCalculator calculator = new PositionCalculator();
        calculator.offsetX = x;
        calculator.offsetY = y;
        calculator.type = PositionType.UPPER_RIGHT_ANCHOR;
        return calculator;
    }

    public static class PositionCalculator
    {

        private PositionType type;
        private int          offsetX;
        private int          offsetY;

        public int getOffsetX()
        {
            return offsetX;
        }

        public void setOffsetX(int offsetX)
        {
            this.offsetX = offsetX;
        }

        public int getOffsetY()
        {
            return offsetY;
        }

        public void setOffsetY(int offsetY)
        {
            this.offsetY = offsetY;
        }

        public PositionType getType()
        {
            return type;
        }

        public void setType(PositionType type)
        {
            this.type = type;
        }

    }


}
