package fr.minemobs.sunderiautilstest;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        SunderiaUtils.of(this);
        try {
            SunderiaUtils.registerCommands(this.getClass().getPackageName() + ".commands");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
