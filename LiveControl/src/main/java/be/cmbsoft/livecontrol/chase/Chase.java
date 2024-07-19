package be.cmbsoft.livecontrol.chase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chase
{
    public static class StepBuilder
    {
        private final Chase chase;
        private final Step  step;

        private StepBuilder(Chase chase)
        {
            this.chase = chase;
            this.step  = new Step();
        }

        public static StepBuilder newInstance(Chase chase)
        {
            return new StepBuilder(chase);
        }

        public StepBuilder toggle(int x, int y)
        {
            step.addCoordinate(x, y);
            return this;
        }

        public StepBuilder addStep()
        {
            chase.addStep(step);
            return new StepBuilder(chase);
        }

        public Chase get()
        {
            chase.addStep(step);
            return chase;
        }
    }

    private final List<Step>    steps         = new ArrayList<>();
    private       long          lastTime;
    private       int           index         = 0;
    private       Step          prev;
    private       ChaseReceiver chaseReceiver = null;
    private       boolean       active        = false;

    public Chase()
    {
        lastTime = System.currentTimeMillis();
    }

    public void toggle()
    {
        active = !active;
        if (prev != null) {
            prev.getCoordinates().forEach(coordinate -> deactivate(coordinate.x(), coordinate.y()));
        }
        if (active) {
            next();
        }
    }

    public Chase addReceiver(ChaseReceiver receiver)
    {
        this.chaseReceiver = receiver;
        return this;
    }

    public boolean isActive()
    {
        return active;
    }

    public void next()
    {
        index++;
        if (index >= steps.size()) {
            index = 0;
        }
        if (prev != null) {
            prev.getCoordinates().forEach(coordinate -> deactivate(coordinate.x(), coordinate.y()));
        }
        Step next = getStep(index);
        if (next != null) {
            next.getCoordinates().forEach(coordinate -> activate(coordinate.x(), coordinate.y()));
        }
        prev     = next;
        lastTime = System.currentTimeMillis();
    }

    public long getLastTime()
    {
        return lastTime;
    }

    public void newStep(Step step)
    {
        steps.add(step);
    }

    public StepBuilder addStep()
    {
        return StepBuilder.newInstance(this);
    }

    public Step getCurrentStep()
    {
        return prev;
    }

    private Step getStep(int index)
    {
        return index >= steps.size() ? null : steps.get(index);
    }

    private void addStep(Step step)
    {
        steps.add(step);
    }

    private void deactivate(int x, int y)
    {
        Optional.ofNullable(chaseReceiver).ifPresent(r -> r.deactivate(x, y));
    }

    private void activate(int x, int y)
    {
        Optional.ofNullable(chaseReceiver).ifPresent(r -> r.activate(x, y));
    }
}
