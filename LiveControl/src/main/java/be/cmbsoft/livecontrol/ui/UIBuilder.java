package be.cmbsoft.livecontrol.ui;

import java.util.HashMap;
import java.util.Map;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.actions.AddOutput;
import be.cmbsoft.livecontrol.gui.GUI;
import controlP5.ControlP5;
import controlP5.ControllerInterface;

import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.ABOUT;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.DEFAULT;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.OUTPUTS;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.SETTINGS;

public class UIBuilder
{

    public static void buildUI(ControlP5 controlP5, GUI gui, LiveControl parent)
    {
        Map<ControllerInterface, PositionCalculator> positions = new HashMap<>();


        int tabHeight = 64;

        controlP5.setColorBackground(parent.getUiConfig().getBackgroundColor());
        controlP5.setColorForeground(parent.getUiConfig().getForegroundColor());
        controlP5.setColorActive(parent.getUiConfig().getActiveColor());
        controlP5.setColorCaptionLabel(parent.getUiConfig().getFontColor());


        controlP5.Tab global = controlP5.getTab("global");

        controlP5.Tab play = controlP5.getTab("default");
        play.setTitle("Play").setHeight(tabHeight).addListener(listener -> activateTab(gui,
            DEFAULT, parent));

        controlP5.Tab output = controlP5.addTab("Outputs").setHeight(tabHeight).addListener(listener -> activateTab(gui,
            OUTPUTS, parent));

        gui.addButton("Add output")
           .setPressAction(() -> parent.doAction(new AddOutput(parent)));


//        positions.put(controlP5.addButton("Detect")
//                        .moveTo(output)
//                        .setWidth(256)
//                        .setHeight(80)
//                        .addCallback(listener -> parent.doAction(new AddOutput(parent))),
//            upperLeft(10, 128));

        controlP5.Tab settings = controlP5.addTab("Settings").setHeight(tabHeight).addListener(
            listener -> activateTab(gui,
                SETTINGS, parent));

        controlP5.Tab about = controlP5.addTab("About").setHeight(tabHeight).addListener(listener -> activateTab(gui,
            ABOUT, parent));

        positions.put(controlP5.addButton("Undo")
                               .addCallback(listener -> parent.doAction(parent::undo))
                               .setImages(parent.getIcons("undo", 64, 64, parent.getUiConfig().getBackgroundColor(),
                                   parent.getUiConfig().getForegroundColor(), parent.getUiConfig().getActiveColor()))
                               .moveTo(global)
                               .setWidth(64)
                               .setHeight(64), upperRight(84, 10));
        positions.put(controlP5.addButton("Redo")
                               .addCallback(listener -> parent.doAction(parent::redo))
                               .setImages(parent.getIcons("redo", 64, 64, parent.getUiConfig().getBackgroundColor(),
                                   parent.getUiConfig().getForegroundColor(), parent.getUiConfig().getActiveColor()))
                               .moveTo(global)
                               .setWidth(64)
                               .setHeight(64), upperRight(10, 10));

        parent.setUIPositions(positions);
        parent.updateUIPositions();

    }

    private static void activateTab(GUI gui, Tab tab, LiveControl parent)
    {
        gui.elements.forEach(element ->
        {
            int groupIndex = element.getGroupIndex();
            if (groupIndex != 0)
            {
                element.setVisible(groupIndex == tab.ordinal());
            }
        });
        parent.activateTab(tab);
    }

    public enum Tab
    {
        DEFAULT, OUTPUTS, SETTINGS, ABOUT;
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
