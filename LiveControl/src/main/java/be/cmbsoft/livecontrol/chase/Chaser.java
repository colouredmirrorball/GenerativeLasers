package be.cmbsoft.livecontrol.chase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import be.cmbsoft.livecontrol.fx.EffectConfiguratorContainer;
import be.cmbsoft.livecontrol.fx.Parameter;

public class Chaser
{
    private final List<Chase>        chases = new ArrayList<>();
    private final Parameter chaseSpeed;
    private final Parameter firstChaseRow;
    private final Random    random;
    private final ChaseReceiver      receiver;
    private final Chase              firstChase;

    public Chaser(EffectConfiguratorContainer configurator, ChaseReceiver receiver)
    {
        chaseSpeed = new Parameter("Chase speed");
        configurator.newParameter("Chase speed", chaseSpeed);
        firstChaseRow = new Parameter("First chase row");
        configurator.newParameter("First chase row", firstChaseRow);
        firstChase = new Chase().addReceiver(receiver)
            .addStep()
            .toggle(0, 4)
            .addStep()
            .toggle(0, 5)
            .addStep()
            .toggle(0, 6)
            .addStep()
            .toggle(0, 5)
            .get();
        chases.add(firstChase);
        random        = new Random();
        this.receiver = receiver;
    }

    public void toggle(int chaseIndex)
    {
        if (chaseIndex < chases.size())
        {
            chases.get(chaseIndex).toggle();
        } else
        {
            for (int i = chases.size(); i < chaseIndex; i++)
            {
                chases.add(newRandomChase());
            }
            chases.get(chases.size() - 1).toggle();
        }
    }

    public void update()
    {
        Float speed    = Optional.ofNullable(chaseSpeed.getValue()).orElse(1f);
        float interval = 1000.0f / speed;
        for (Chase chase: chases)
        {
            if (chase.isActive())
            {
                if (chase == firstChase)
                {
                    int newX = (int) firstChaseRow.getValue();
                    Optional.ofNullable(chase.getCurrentStep()).ifPresent(step -> step.setXForAllCoordinates(newX));

                }
                long lastTime = chase.getLastTime();
                if (System.currentTimeMillis() - lastTime > interval)
                {
                    chase.next();
                }
            }
        }
    }

    private Chase newRandomChase()
    {
        return new Chase().addReceiver(receiver)
            .addStep()
            .toggle(random(5, 7), random(5, 7))
            .toggle(random(7), random(7))
            .addStep()
            .toggle(random(5, 7), random(5, 7))
            .toggle(random(7), random(7))
            .addStep()
            .toggle(random(5, 7), random(5, 7))
            .toggle(random(7), random(7))
            .addStep()
            .toggle(random(5, 7), random(5, 7))
            .toggle(random(7), random(7))
            .get();
    }

    private int random(int min, int max)
    {
        return min + random.nextInt(max - min);
    }

    private int random(int max)
    {
        return random.nextInt(max);
    }
}
