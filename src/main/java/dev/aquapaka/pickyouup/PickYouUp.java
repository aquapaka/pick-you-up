package dev.aquapaka.pickyouup;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import dev.aquapaka.pickyouup.commands.PickYouUpReloadCommand;
import dev.aquapaka.pickyouup.commands.PickYouUpToggleCommand;
import dev.aquapaka.pickyouup.configuration.ConfigurationFactory;
import dev.aquapaka.pickyouup.configuration.language.ImmutableLanguageConfigurationFactory;
import dev.aquapaka.pickyouup.configuration.language.LanguageConfiguration;
import dev.aquapaka.pickyouup.configuration.properties.ImmutablePropertyConfigurationFactory;
import dev.aquapaka.pickyouup.configuration.properties.PropertyConfiguration;
import dev.aquapaka.pickyouup.cooldown.CooldownContainer;
import dev.aquapaka.pickyouup.cooldown.HashedCooldownContainer;
import dev.aquapaka.pickyouup.listener.PlayerConnectionListener;
import dev.aquapaka.pickyouup.listener.PlayerPickupListener;
import dev.aquapaka.pickyouup.listener.PlayerThrowListener;
import dev.aquapaka.pickyouup.permission.PlayerPermissionChecker;
import dev.aquapaka.pickyouup.permission.SimplePlayerPermissionChecker;
import dev.aquapaka.pickyouup.player.PersistentPlayerToggleRegister;
import dev.aquapaka.pickyouup.player.PlayerToggleRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PickYouUp extends JavaPlugin implements Reloadable {

    private Path languageConfigPath;
    private Path propertiesConfigPath;

    private ConfigurationFactory<LanguageConfiguration> languageConfigurationFactory;
    private ConfigurationFactory<PropertyConfiguration> propertyConfigurationFactory;

    private CooldownContainer cooldownContainer;
    private PlayerToggleRegistry playerToggleRegistry;
    private PlayerPermissionChecker playerPermissionChecker;

    private FixedScheduler fixedScheduler;

    @Override
    public void onEnable() {
        this.languageConfigPath = getDataFolder().toPath().resolve("language.yml");
        this.propertiesConfigPath = getDataFolder().toPath().resolve("properties.yml");

        this.languageConfigurationFactory = new ImmutableLanguageConfigurationFactory();
        this.propertyConfigurationFactory = new ImmutablePropertyConfigurationFactory();

        this.cooldownContainer = new HashedCooldownContainer();
        this.playerPermissionChecker = new SimplePlayerPermissionChecker();
        this.playerToggleRegistry = new PersistentPlayerToggleRegister(Bukkit::getPlayer, new NamespacedKey(this,
            "ToggleValue"));

        this.fixedScheduler = (r, l) -> getServer().getScheduler().runTaskLater(this, r, l);

        this.reload();
    }

    @Override
    public void reload() {
        exportConfiguration();

        final LanguageConfiguration languageConfiguration = readConfig(languageConfigurationFactory, "language.yml");
        final PropertyConfiguration propertyConfiguration = readConfig(propertyConfigurationFactory, "properties.yml");

        getLogger().info(String.format("player-pickup-cooldown %d seconds",
            propertyConfiguration.playerPickupCooldown().getSeconds()));

        HandlerList.unregisterAll(this);
        registerListeners(
            this.cooldownContainer,
            languageConfiguration,
            this.playerToggleRegistry,
            this.playerPermissionChecker,
            propertyConfiguration,
            fixedScheduler
        );

        registerCommands(this.playerToggleRegistry, languageConfiguration, this.playerPermissionChecker);
    }

    /**
     * Registers the listeners this plugin uses
     *
     * @param cooldownContainer     the cooldown container to pass
     * @param languageConfiguration the languageConfiguration to use
     * @param toggleRegistry        the toggle registry to use
     * @param permissionChecker     the permission checker to use
     * @param propertyConfiguration the propertyConfiguration to use
     */
    private void registerListeners(final CooldownContainer cooldownContainer,
                                   final LanguageConfiguration languageConfiguration,
                                   final PlayerToggleRegistry toggleRegistry,
                                   final PlayerPermissionChecker permissionChecker,
                                   final PropertyConfiguration propertyConfiguration,
                                   final FixedScheduler scheduler) {

        this.getServer().getPluginManager().registerEvents(new PlayerPickupListener(
            cooldownContainer,
            languageConfiguration,
            toggleRegistry,
            permissionChecker,
            propertyConfiguration,
            Player::displayName
        ), this);
        this.getServer().getPluginManager().registerEvents(new PlayerThrowListener(propertyConfiguration, scheduler), this);
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(cooldownContainer), this);
    }

    /**
     * Registers the commands this plugin uses
     *
     * @param playerToggleRegistry  the toggle registry to use
     * @param languageConfiguration the languageConfiguration to use
     * @param permissionChecker     the the permission checker to use
     */
    private void registerCommands(final PlayerToggleRegistry playerToggleRegistry,
                                  final LanguageConfiguration languageConfiguration,
                                  final PlayerPermissionChecker permissionChecker) {

        Objects.requireNonNull(getCommand("pickyouup")).setExecutor(new PickYouUpToggleCommand(playerToggleRegistry
            , languageConfiguration
            , permissionChecker));
        Objects.requireNonNull(getCommand("pickyouup-reload")).setExecutor(new PickYouUpReloadCommand(permissionChecker
            , languageConfiguration
            , this));
    }

    /**
     * Exports all the default configurations
     */
    private void exportConfiguration() {
        if (!Files.exists(languageConfigPath)) this.saveResource("language.yml", false);
        if (!Files.exists(propertiesConfigPath)) this.saveResource("properties.yml", false);
    }

    /**
     * Reads a config from a relative path
     *
     * @param factory  the factory to create the data instance from
     * @param filePath the file path of the config
     * @param <T>      the type of the data instance
     *
     * @return the data instance
     *
     * @throws RuntimeException if the file could not be read
     */
    private <T> T readConfig(final ConfigurationFactory<T> factory, final String filePath) {
        try (final BufferedReader reader = Files.newBufferedReader(getDataFolder().toPath().resolve(filePath))) {
            return factory.create(YamlConfiguration.loadConfiguration(reader));
        } catch (final IOException e) {
            throw new RuntimeException(String.format("Could not read configuration file %s in plugin data-folder",
                filePath), e);
        }
    }

}
