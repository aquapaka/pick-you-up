package dev.aquapaka.pickyouup.permission;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * The player permission factory defines the creation logic for the permission checks used by PickYouUp
 */
public interface PlayerPermissionChecker {

    /**
     * Returns if the permissible instance has the permission to use the pickyouup toggle command
     *
     * @param permissible the permissible instance
     *
     * @return the result
     */
    boolean usePickYouUpToggleCommand(Permissible permissible);

    /**
     * Returns if the permissible can use the reload command
     *
     * @param permissible the permissible to check against
     *
     * @return the result
     */
    boolean usePickYouUpReloadCommand(Permissible permissible);

    /**
     * Returns if the player has the permission to pickup the entity
     *
     * @param permissible the permissible to check against
     * @param entity      the entity to pickup
     *
     * @return the result
     */
    boolean pickupEntity(Permissible permissible, Entity entity);

    /**
     * Returns if the player has the permission to pickup the entity
     *
     * @param permissible the permissible to check against
     * @param entity      the entity to pickup
     *
     * @return the result
     */
    boolean pickupPlayer(Permissible permissible, Player entity);

    /**
     * Returns if the permissible instance can bypass a toggled decision
     *
     * @param permissible the permissible instance
     *
     * @return the result
     */
    boolean bypassToggle(Permissible permissible);

}
