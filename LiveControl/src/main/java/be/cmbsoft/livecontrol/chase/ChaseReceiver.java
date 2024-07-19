package be.cmbsoft.livecontrol.chase;

public interface ChaseReceiver
{
    void deactivate(int x, int y);

    void activate(int x, int y);
}
