package dev.aquapaka.pickyouup.cooldown;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HashedCooldownContainer implements CooldownContainer {

    private final Map<UUID, Instant> cooldownEnd = new ConcurrentHashMap<>();

    /**
     * Starts a cooldown for the given uuid
     *
     * @param uuid     the uuid
     * @param duration the duration this cooldown will last
     */
    @Override
    public void startCooldown(final UUID uuid, final Duration duration) {
        this.cooldownEnd.put(uuid, Instant.now().plus(duration));
    }

    /**
     * Returns the duration the cooldown for the uuid will last
     *
     * @param uuid the uuid to check for
     *
     * @return the duration wrapped in an {@link Optional}
     */
    @Override
    public Optional<Duration> getCooldownLeft(final UUID uuid) {
        final Instant now = Instant.now();
        return Optional.ofNullable(this.cooldownEnd.get(uuid))
            .filter(i -> i.isAfter(now))
            .map(i -> Duration.between(now, i));
    }

    /**
     * Removes the cooldown stored for this uuid
     *
     * @param uuid the uuid
     */
    @Override
    public void remove(final UUID uuid) {
        this.cooldownEnd.remove(uuid);
    }

}
