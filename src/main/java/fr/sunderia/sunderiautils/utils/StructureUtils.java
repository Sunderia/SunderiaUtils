package fr.sunderia.sunderiautils.utils;

import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;

public class StructureUtils {

    private StructureUtils() {}

    public static Vector getCenter(Structure structure) {
        return getCenter(structure, false);
    }

    public static Vector getCenter(Structure structure, boolean centerY) {
        Vector vec = structure.getSize().clone();
        vec.multiply(.5);
        return centerY ? vec : vec.setY(0);
    }
}