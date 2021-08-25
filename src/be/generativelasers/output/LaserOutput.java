package be.generativelasers.output;

import be.generativelasers.procedures.Procedure;
import cmb.soft.cgui.CGui;
import ilda.IldaPoint;

import java.util.List;

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

    protected LaserOutput()
    {
        setName("Laser output");
    }

    @Override
    public void run()
    {
        boolean interrupted = false;
        long lastTime = 0L;
        while (!interrupted)
        {

            try
            {
                if (!paused)
                {
                    List<IldaPoint> points = procedure.getPoints();
                    project(points);
                    lastFramePointCount = points.size();
                }
                long sleepTime = getSleepTime(lastTime, lastFramePointCount);
                lastTime = System.currentTimeMillis();
                sleep(sleepTime);
            } catch (InterruptedException exception)
            {
                CGui.log(exception.getMessage());
                exception.printStackTrace();
                interrupt();
                interrupted = true;
            }
        }
    }

    private long getSleepTime(long lastTime, int lastFramePointCount)
    {
        long currentTime = System.currentTimeMillis();
        switch (mode)
        {
            case STATIC_FPS:
                return Math.max(0, millisecondsPerFrame - (currentTime - lastTime));
            case STATIC_PPS:
                long allottedFrameDuration = lastFramePointCount == 0 ? 33 : lastFramePointCount / pps;
                return Math.max(0, allottedFrameDuration - (currentTime - lastTime));
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    public synchronized void setProcedure(Procedure currentProcedure)
    {
        this.procedure = currentProcedure;
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

    public abstract void project(List<IldaPoint> points);

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
