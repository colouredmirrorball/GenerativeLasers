package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PVector;

public class HighlightEffect extends Effect
{
    public static class HighlightEffectInfo
    {
        private PVector position;
        private int     radius;
        private int     color;
        private int     duration;
        private String  alias;

        public PVector getPosition()
        {
            return position;
        }

        public void setPosition(PVector position)
        {
            this.position = position;
        }

        public int getRadius()
        {
            return radius;
        }

        public void setRadius(int radius)
        {
            this.radius = radius;
        }

        public int getColor()
        {
            return color;
        }

        public void setColor(int color)
        {
            this.color = color;
        }

        public int getDuration()
        {
            return duration;
        }

        public void setDuration(int duration)
        {
            this.duration = duration;
        }

        public String getAlias()
        {
            return alias;
        }

        public void setAlias(String alias)
        {
            this.alias = alias;
        }
    }

    private final Noot                noot;
    private       HighlightEffectInfo info;
    private       int                 time = 0;

    public HighlightEffect(HighlightEffectInfo info, Noot noot)
    {
        this.info = info;
        setType(Type.TOGGLE);
        setAlias("Highlight " + info.alias + (info.position == null ? ""
            : " at " + info.position.x + ", " + info.position.y));
        this.noot = noot;
    }

    @Override
    public void initialize(Lichtfestival parent)
    {
        time = 0;
        if (info == null) {
            info = new HighlightEffectInfo();
        }
        if (info.position == null) {
            info.position = parent.newRandomPosition();
        }
        if (info.color == 0) {
            info.color = parent.newRandomColour();
        }
        if (info.duration == 0) {
            info.duration = 120;
        }
        if (info.alias == null) {
            info.alias = "Generated";
        }
        if (info.radius == 0) {
            info.radius = (int) parent.random(30, 70);
        }
    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {
        renderer.stroke(info.color);
        renderer.ellipse(info.position.x, info.position.y, info.radius, info.radius);
        time++;
        if (!parent.isEffectSetupMode() && time > info.duration)
        {
            expire();
        }
    }

    public HighlightEffectInfo getInfo()
    {
        return info;
    }

    public Noot getNoot()
    {
        return noot;
    }
}
