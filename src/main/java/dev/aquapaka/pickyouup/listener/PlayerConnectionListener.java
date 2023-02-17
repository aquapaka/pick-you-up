package dev.aquapaka.pickyouup.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.aquapaka.pickyouup.cooldown.CooldownContainer;

public class PlayerConnectionListener implements Listener {

    private final CooldownContainer cooldownContainer;

    public PlayerConnectionListener(final CooldownContainer cooldownContainer) {
        this.cooldownContainer = cooldownContainer;
    }

    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event) {
        this.cooldownContainer.remove(event.getPlayer().getUniqueId());
    }

}
