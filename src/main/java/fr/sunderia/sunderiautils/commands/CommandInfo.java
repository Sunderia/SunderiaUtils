package fr.sunderia.sunderiautils.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    /**
     * @return The name of the command
     */
    String name();

    /**
     * @return The permission needed to execute the command
     */
    String permission() default "";

    /**
     * @return {@code true} if the command can only be executed by a player, {@code false} otherwise
     */
    boolean requiresPlayer();

    /**
     * @return The aliases of the command
     */
    String[] aliases() default {};

    /**
     * @return The usage of the command, for example: {@code /command <arg1> <arg2>}
     */
    String usage() default "";

    /**
     * @return The description of the command
     */
    String description() default "";

    /**
     * @return The message sent to the player if he doesn't have the permission to execute the command
     */
    String permissionMessage() default "§cYou don't have permission to use this command.";
}