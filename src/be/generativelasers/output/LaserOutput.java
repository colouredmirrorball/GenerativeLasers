package be.generativelasers.output;

import be.generativelasers.procedures.Procedure;
import cmb.soft.cgui.CGui;
import ilda.IldaFrame;

/**
 * @author Florian Created on 27/01/2020
 */
public abstract class LaserOutput extends Thread
{

    protected Procedure procedure;
    private int pps = 30000;
    private boolean paused;
    private int fps = 30;
    private Mode mode = Mode.STATIC_PPS;
    private long millisecondsPerFrame = 1000L / fps;
    private int lastFramePointCount = 0;

    @Override
    public void run()
    {
        boolean interrupted = false;
        long lastTime = 0L;
        while (!interrupted)
        {

            try
            {
                long currentTime = System.currentTimeMillis();
                if (!paused && canProject(lastTime, currentTime))
                {
                    IldaFrame frame = procedure.getRenderedFrame();
                    project(frame);
                    lastFramePointCount = frame.getPointCount();
                }
            } catch (Exception exception)
            {
                CGui.log(exception.getMessage());
                exception.printStackTrace();
                interrupted = true;
            }
        }
    }

    public synchronized void setProcedure(Procedure currentProcedure)
    {
        this.procedure = currentProcedure;
    }

    protected boolean canProject(long lastTime, long currentTime)
    {
        switch (mode)
        {
            case STATIC_FPS:
                return currentTime - lastTime > millisecondsPerFrame;
            case STATIC_PPS:
                long allottedFrameDuration = lastFramePointCount / pps;
                return currentTime - lastTime > allottedFrameDuration;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    public int getPps()
    {
        return pps;
    }

    public int getFps()
    {
        return fps;
    }

    public void setFps(int fps)
    {
        this.fps = fps;
        if (fps != 0)
        {
            millisecondsPerFrame = 1000L / fps;
        } else
        {
            paused = true;
        }
    }

    public LaserOutput setPps(int pps)
    {
        this.pps = pps;
        return this;
    }

    public abstract void project(IldaFrame frame);

    public Mode getMode()
    {
        return mode;
    }

    public LaserOutput setMode(Mode mode)
    {
        this.mode = mode;
        return this;
    }

    public enum Mode
    {
        STATIC_FPS, STATIC_PPS
    }
}
