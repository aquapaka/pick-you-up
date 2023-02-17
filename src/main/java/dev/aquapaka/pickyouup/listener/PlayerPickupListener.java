package dev.aquapaka.pickyouup.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import dev.aquapaka.pickyouup.configuration.language.LanguageConfiguration;
import dev.aquapaka.pickyouup.configuration.properties.PropertyConfiguration;
import dev.aquapaka.pickyouup.cooldown.CooldownContainer;
import dev.aquapaka.pickyouup.permission.PlayerPermissionChecker;
import dev.aquapaka.pickyouup.player.PlayerToggleRegistry;

import java.time.Duration;
import java.util.function.Function;

public record PlayerPickupListener(CooldownContainer cooldownContainer,
                                   LanguageConfiguration languageConfiguration,
                                   PlayerToggleRegistry toggleRegistry,
                                   PlayerPermissionChecker permissionChecker,
                                   PropertyConfiguration propertyConfiguration,
                                   Function<Player, Component> nameLookup) implements Listener {

    @EventHandler
    public void onPickup(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();

        if (!player.isSneaking()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        final Entity target = event.getRightClicked();
        if (!(target instanceof LivingEntity)) return;
        if (this.propertyConfiguration.bannedEntityTypes().contains(target.getType())) return;

        final Duration durationLeft = this.cooldownContainer.getCooldownLeft(player.getUniqueId()).orElse(null);
        if (durationLeft != null) {
            player.sendMessage(this.languageConfiguration.playerPickupCooldown(durationLeft));
            return;
        }

        if (!(target instanceof final Player targetPlayer)) {
            if (!this.permissionChecker.pickupEntity(player, target)) {
                player.sendMessage(this.languageConfiguration.permissionMissing());
                return;
            }
        } else {
            if (!this.permissionChecker.pickupPlayer(player, targetPlayer)) {
                player.sendMessage(this.languageConfiguration.permissionMissing());
                return;
            }

            if (!this.toggleRegistry.getCurrentToggle(target.getUniqueId())
                && !this.permissionChecker.bypassToggle(player)) {
                player.sendMessage(this.languageConfiguration.playerCannotBePickedUp(this.nameLookup.apply(targetPlayer)));
                return;
            }
        }

        if (player.getPassengers().contains(target)) return;

        player.addPassenger(target);
        this.cooldownContainer.startCooldown(player.getUniqueId(), propertyConfiguration.playerPickupCooldown());
    }

}
