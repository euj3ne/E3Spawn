package ru.euj3ne.e3spawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class E3Spawn extends JavaPlugin implements Listener {

    private Location spawnLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSpawnLocation();

        getLogger().info("Plugin has been enabled!");
        getLogger().info("Plugin developed by: @euj3ne");
        getLogger().info("Website: " + getPluginMeta().getWebsite());

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadSpawnLocation() {
        String worldName = getConfig().getString("Spawn.world");
        if (worldName == null || worldName.isEmpty()) {
            spawnLocation = null;
            getLogger().warning("Spawn world name is missing in config.");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            spawnLocation = null;
            getLogger().warning("World '" + worldName + "' not found. Players will not be teleported.");
            return;
        }

        double x = getConfig().getDouble("Spawn.x");
        double y = getConfig().getDouble("Spawn.y");
        double z = getConfig().getDouble("Spawn.z");
        float yaw = (float) getConfig().getDouble("Spawn.yaw");
        float pitch = (float) getConfig().getDouble("Spawn.pitch");

        spawnLocation = new Location(world, x, y, z, yaw, pitch);
        world.setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("setspawn")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return true;
        }

        Location location = player.getLocation();
        getConfig().set("Spawn.x", location.getX());
        getConfig().set("Spawn.y", location.getY());
        getConfig().set("Spawn.z", location.getZ());
        getConfig().set("Spawn.yaw", location.getYaw());
        getConfig().set("Spawn.pitch", location.getPitch());
        getConfig().set("Spawn.world", location.getWorld().getName());
        saveConfig();

        spawnLocation = location.clone();
        spawnLocation.getWorld().setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());

        player.sendMessage(ChatColor.GREEN + "Spawn point has been set.");
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (spawnLocation == null) {
            return;
        }

        event.getPlayer().teleport(spawnLocation);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been disabled!");
    }
}
