package be.generativelasers.output;

import be.generativelasers.output.etherdream.Etherdream;
import be.generativelasers.output.etherdream.EtherdreamDiscoverer;
import ilda.IldaPoint;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EtherdreamOutput extends LaserOutput
{

    private final HashMap<String, Etherdream> devices = new HashMap<>();

    public EtherdreamOutput()
    {
        EtherdreamDiscoverer discoverer = new EtherdreamDiscoverer(devices);
        Thread discovererThread = new Thread(discoverer);
        discovererThread.setName("Etherdream discoverer");
        discovererThread.start();
    }

    @Override
    public void project(List<IldaPoint> points) throws IOException
    {
        Collection<Etherdream> etherdreams = devices.values();
        if (!etherdreams.isEmpty())
        {
            Etherdream etherdream = etherdreams.stream().findFirst().orElse(null);
            if (etherdream != null)
            {
                etherdream.prepareStream();
                etherdream.writeData(points);
                etherdream.beginPlayback(getPps());
            }
        }
    }
}
