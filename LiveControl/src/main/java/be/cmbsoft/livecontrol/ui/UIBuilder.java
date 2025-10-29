package be.cmbsoft.livecontrol.ui;

import java.util.HashMap;
import java.util.Map;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.Matrix;
import be.cmbsoft.livecontrol.actions.AddOutput;
import be.cmbsoft.livecontrol.actions.ChaseDisabledAction;
import be.cmbsoft.livecontrol.actions.ChaseEnabledAction;
import be.cmbsoft.livecontrol.actions.FlashDisabledAction;
import be.cmbsoft.livecontrol.actions.FlashEnabledAction;
import be.cmbsoft.livecontrol.gui.AnchoredPositionCalculator;
import be.cmbsoft.livecontrol.gui.GUI;
import be.cmbsoft.livecontrol.gui.GUIContainer;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import processing.core.PVector;

import static be.cmbsoft.livecontrol.LiveControl.log;
import static be.cmbsoft.livecontrol.ui.PositionType.UPPER_LEFT_ANCHOR;
import static be.cmbsoft.livecontrol.ui.PositionType.UPPER_RIGHT_ANCHOR;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.ABOUT;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.DEFAULT;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.OSCILLABSTRACT;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.OUTPUTS;
import static be.cmbsoft.livecontrol.ui.UIBuilder.Tab.SETTINGS;

public class UIBuilder
{

    public static final int CHASES_AMOUNT = 8;

    public static void buildUI(ControlP5 controlP5, GUI gui, LiveControl parent)
    {
        Map<ControllerInterface<?>, AnchoredPositionCalculator> positions = new HashMap<>();


        int tabHeight = 64;
        controlP5.setTabEventsActive(true);
        controlP5.setColorBackground(parent.getUiConfig().getBackgroundColor());
        controlP5.setColorForeground(parent.getUiConfig().getForegroundColor());
        controlP5.setColorActive(parent.getUiConfig().getActiveColor());
        controlP5.setColorCaptionLabel(parent.getUiConfig().getFontColor());


        controlP5.Tab global = controlP5.getTab("global");

        controlP5.Tab play = controlP5.getTab("default");
        play.setTitle("Play").setHeight(tabHeight)
            .addListener(listener -> activateTab(gui, DEFAULT, parent))
            .activateEvent(true)
            .setId(DEFAULT.ordinal());

        for (int index = 0; index < CHASES_AMOUNT; index++)
        {
            chaseButton(gui, parent, index);
        }

        for (int index = 0; index < Matrix.ROWS; index++)
        {
            modifySourceButton(gui, parent, index);
        }


        gui.addToggle("Flash mode")
           .setEnabledAction(() -> parent.doAction(new FlashEnabledAction(parent)))
           .setDisabledAction(() -> parent.doAction(new FlashDisabledAction(parent)))
           .setPosition(new be.cmbsoft.livecontrol.gui.PositionCalculator()
           {
               @Override
               public PVector updatePosition(GUIContainer parent, int width, int height)
               {
                   return new PVector(parent.getWidth() - 300, 800);
               }
           })
           .setSize(256, 64)
           .setInfoText("Toggle flash mode")
           .setFontSize(32)
           .setGroupIndex(DEFAULT.ordinal())
        ;

        controlP5.Tab output = controlP5.addTab("Outputs").setHeight(tabHeight)

                                        .addListener(listener -> activateTab(gui, OUTPUTS, parent))
                                        .activateEvent(true)
                                        .setId(OUTPUTS.ordinal());

        gui.addButton("Add output")
           .setPosition(10, 100)
           .setSize(256, 64)
           .setInfoText("Add a new, unassigned output")
           .setFontSize(32)
           .setGroupIndex(OUTPUTS.ordinal())
           .setPressAction(() -> parent.doAction(new AddOutput(parent)));

        controlP5.Tab oscillabstract = controlP5.addTab("Oscillabstract").setHeight(tabHeight)
                                                .addListener(listener -> activateTab(gui, OSCILLABSTRACT, parent))
                                                .activateEvent(true)
                                                .setId(OSCILLABSTRACT.ordinal());

        parent.setOscillabstractWorkspaceButtons(gui.addLinearLayout()
                                                    .setPosition(10, 100)
                                                    .setSize(256, 64)
                                                    .setSpacing(60)
                                                    .setGroupIndex(OSCILLABSTRACT.ordinal()));


//        positions.put(controlP5.addButton("Detect")
//                        .moveTo(output)
//                        .setWidth(256)
//                        .setHeight(80)
//                        .addCallback(listener -> parent.doAction(new AddOutput(parent))),
//            upperLeft(10, 128));

        controlP5.Tab settings = controlP5.addTab("Settings").setHeight(tabHeight)
                                          .addListener(listener -> activateTab(gui, SETTINGS, parent))
                                          .activateEvent(true)
                                          .setId(SETTINGS.ordinal());

        controlP5.Tab about = controlP5.addTab("About").setHeight(tabHeight)
                                       .addListener(listener -> activateTab(gui, ABOUT, parent))
                                       .activateEvent(true)
                                       .setId(ABOUT.ordinal());

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

    private static void chaseButton(GUI gui, LiveControl parent, int index)
    {
        gui.addToggle("Chase " + index)
           .setEnabledAction(() -> parent.doAction(new ChaseEnabledAction(parent, index)))
           .setDisabledAction(() -> parent.doAction(new ChaseDisabledAction(parent, index)))
            .setPosition(new be.cmbsoft.livecontrol.gui.PositionCalculator()
            {
                @Override
                public PVector updatePosition(GUIContainer parent, int width, int height)
                {
                    return new PVector(parent.getWidth() - 300, 100 + 70 * index);
                }
            })
            .setSize(256, 64)
            .setInfoText("Activate chase")
            .setFontSize(32)
            .setGroupIndex(DEFAULT.ordinal())
        ;
    }

    private static void modifySourceButton(GUI gui, LiveControl parent, int index)
    {
        gui.addMultipleImagesButton("Modify source " + index, parent.getIcon("settings",
               Matrix.DEFAULT_ELEMENT_HEIGHT, Matrix.DEFAULT_ELEMENT_HEIGHT))
           .setPosition(new be.cmbsoft.livecontrol.gui.PositionCalculator()
           {
               @Override
               public PVector updatePosition(GUIContainer parent, int width, int height)
               {
                   return new PVector(25,
                       Matrix.DEFAULT_OFFSET_Y + (Matrix.DEFAULT_ELEMENT_HEIGHT + Matrix.DEFAULT_PADDING) * index);
               }
           })
           .setSize(64, 64)
           .setInfoText("Modify source " + index)
           .setPressAction(() -> parent.modifySource(index))
           .setGroupIndex(DEFAULT.ordinal());
    }

    public static void activateTab(GUI gui, Tab tab, LiveControl parent)
    {
        log("Activated tab: " + tab.name());
        gui.elements.forEach(element ->
        {
            int groupIndex = element.getGroupIndex();
//            if (groupIndex != 0)
            {
                element.setVisible(groupIndex == tab.ordinal());
            }
        });
        parent.activateTab(tab);
    }

    public enum Tab
    {
        DEFAULT, OUTPUTS, OSCILLABSTRACT, SETTINGS, ABOUT
    }

    private UIBuilder()
    {

    }

    private static AnchoredPositionCalculator upperRight(int x, int y)
    {
        AnchoredPositionCalculator calculator = new AnchoredPositionCalculator();
        calculator.setOffsetX(x);
        calculator.setOffsetY(y);
        calculator.setType(UPPER_RIGHT_ANCHOR);
        return calculator;
    }

    private static AnchoredPositionCalculator upperLeft(int x, int y)
    {
        AnchoredPositionCalculator calculator = new AnchoredPositionCalculator();
        calculator.setOffsetX(x);
        calculator.setOffsetY(y);
        calculator.setType(UPPER_LEFT_ANCHOR);
        return calculator;
    }


}
