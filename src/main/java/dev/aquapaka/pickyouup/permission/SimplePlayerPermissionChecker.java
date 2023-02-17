package dev.aquapaka.pickyouup.permission;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class SimplePlayerPermissionChecker implements PlayerPermissionChecker {

    /**
     * Returns if the permissible instance has the permission to use the pickyouup toggle command
     *
     * @param permissible the permissible instance
     *
     * @return the result
     */
    @Override
    public boolean usePickYouUpToggleCommand(final Permissible permissible) {
        return permissible.hasPermission("pickyouup.toggle");
    }

    /**
     * Returns if the permissible can use the reload command
     *
     * @param permissible the permissible to check against
     *
     * @return the result
     */
    @Override
    public boolean usePickYouUpReloadCommand(final Permissible permissible) {
        return permissible.hasPermission("pickyouup.reload");
    }

    /**
     * Returns if the player has the permission to pickup the entity
     *
     * @param permissible the permissible to check against
     * @param entity      the entity to pickup
     *
     * @return the result
     */
    @Override
    public boolean pickupEntity(final Permissible permissible, final Entity entity) {
        return permissible.hasPermission("pickyouup.pickup.entity");
    }

    /**
     * Returns if the player has the permission to pickup the entity
     *
     * @param permissible the permissible to check against
     * @param entity      the entity to pickup
     *
     * @return the result
     */
    @Override
    public boolean pickupPlayer(final Permissible permissible, final Player entity) {
        return permissible.hasPermission("pickyouup.pickup.player");
    }

    /**
     * Returns if the permissible instance can bypass a toggled decision
     *
     * @param permissible the permissible instance
     *
     * @return the result
     */
    @Override
    public boolean bypassToggle(final Permissible permissible) {
        return permissible.hasPermission("pickyouup.pickup.bypass");
    }

}
