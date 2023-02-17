package dev.aquapaka.pickyouup.configuration.properties;

import org.bukkit.entity.EntityType;

import java.time.Duration;
import java.util.Set;

/**
 * The {@link PropertyConfiguration} defines a configuration that contains the properties of this plugin
 */
public interface PropertyConfiguration {

    /**
     * Returns the cooldown a player has between picking up entities
     *
     * @return the duration
     */
    Duration playerPickupCooldown();

    /**
     * Returns the multiplier applied to the player velocity when throwing an entity
     *
     * @return the double value
     */
    double throwVelocityMultiplier();

    /**
     * Provides a set containing all entity types that are banned from being picked up by pickyouup.
     * An entity type that is listed in the set cannot be picked up or thrown off the player by pickyouup.
     *
     * @return the set instance. The set is mutable but is not backed by this configuration, hence modifications to the
     *     set will not modify this configuration instance.
     */
    Set<EntityType> bannedEntityTypes();

}
