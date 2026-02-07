package be.cmbsoft.livecontrol.gui;

/**
 * Multiple objects can get a pointer to the same Visibility object so they can easily be turned visible or invisible
 * as a group.
 * Usually a GUI element will have an ArrayList or something of Visibility objects. They will all have to be true for
 * the
 * object to be visible.
 * <p>
 * Created by Florian on 10/11/2017.
 */
@FunctionalInterface
public interface Visibility
{
    boolean isVisible(GUIContainer parent);

}
