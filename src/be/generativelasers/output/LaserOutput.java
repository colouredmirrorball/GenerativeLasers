package be.generativelasers.output;

import be.generativelasers.procedures.Procedure;
import cmb.soft.cgui.CGui;

/**
 * @author Florian Created on 27/01/2020
 */
public abstract class LaserOutput extends Thread
{
    Procedure procedure;
    private boolean paused;

    public abstract void project();

    private int fps = 30;
    private long millisecondsPerFrame = 1000L / fps;

    public synchronized void setProcedure(Procedure currentProcedure)
    {
        this.procedure = currentProcedure;
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
                long currentTime = System.currentTimeMillis();
                if (!paused && currentTime - lastTime > millisecondsPerFrame)
                {
                    project();
                }
            } catch (Exception exception)
            {
                CGui.log(exception.getMessage());
                exception.printStackTrace();
                interrupted = true;
            }
        }
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
}
