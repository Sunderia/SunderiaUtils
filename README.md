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

For more examples, see the [Sunderia Utils](https://github.com/Sunderia/SunderiaUtils/tree/main/SunderiaUtilsTest/src/main/java/fr/minemobs/sunderiautilstest).

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