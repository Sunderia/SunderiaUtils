package fr.sunderia.sunderiautils.utils;

import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;

public class StructureUtils {

    private StructureUtils() {}

    public static Vector getCenter(Structure structure) {
        return getCenter(structure, false);
    }

    public static Vector getCenter(Structure structure, boolean centerY) {
        return structure.getSize().multiply(new Vector(.5, centerY ? .5 : 0, .5));
    }
}
