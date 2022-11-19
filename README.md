# Sunderia Utils
Utility classes

---

## Example

```java
public class ExamplePlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        SunderiaUtils.of(this);
        //Register commands
        SunderiaUtils.registerCommands("com.example.commands");
    }
}
```

```java
@CommandInfo(name = "example", permission = "example.command", usage = "/example <player>", description = "Example command")
public class ExampleCommand extends PluginCommand {
    
    public ExampleCommand() {
        super("example");
    }
    
    @Override
    public void execute(Player sender, String[] args) {
        player.sendMessage("Hello " + getArg(args, 0).orElse("World"));
    }

    /**
     * The method should have 2 parameters
     * The first one is the sender, it can be either {@link org.bukkit.entity.Player} or {@link org.bukkit.command.CommandSender} (it depends on the value of {requiresPlayer} in the {@link CommandInfo#requiresPlayer()})
     * The second one is the arguments, it needs to be an array of String.
     **/
    @SubCommand(name = "test")
    public void test(Player player, String[] args) {
        player.sendMessage("Test sub command");
    }
}
```

For more examples, see the [Sunderia Utils Example](https://github.com/Sunderia/SunderiaUtils/tree/main/SunderiaUtilsTest/src/main/java/fr/minemobs/sunderiautilstest).

---

## Download


```groovy
repositories {
    maven {
        name 'SunderiaRepo'
        url 'https://maven.galaxyfight.fr/snapshots'
    }
}

dependencies {
    //...
    implementation 'fr.sunderia:SunderiaUtils:1.5-SNAPSHOT'
}

```

### [JavaDoc](https://jitpack.io/com/github/Sunderia/SunderiaUtils/1.0/javadoc/)
