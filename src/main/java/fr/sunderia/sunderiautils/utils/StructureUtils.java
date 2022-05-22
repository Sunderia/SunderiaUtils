package fr.sunderia.sunderiautils.utils;

import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;

public class StructureUtils {

    private StructureUtils() {}

    public static Vector getCenter(Structure structure) {
        return getCenter(structure, false);
    }

    public static Vector getCenter(Structure structure, boolean centerY) {
        return centerY ? structure.getSize().clone().multiply(.5) : structure.getSize().clone().multiply(.5).setY(0);
    }
}
