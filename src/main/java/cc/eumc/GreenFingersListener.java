package cc.eumc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Dictionary;
import java.util.UUID;

public class GreenFingersListener implements Listener {
    static Plugin instance = GreenFingers.instance;
    //Dictionary<Entity, ItemStack> projectileDictionary;

    @EventHandler
    public void onPotionSplash (PotionSplashEvent e) {
        final Material plant = GreenFingers.getTypeOfFlowerGas(e.getPotion().getItem());   // planet would be the target planet type if the potion is a GreenFingers one
        final Location hitLocation = e.getEntity().getLocation();
        if (plant != null && hitLocation != null) {                                           // | is it a GreenFingers item?
            new BukkitRunnable() {
                @Override
                public void run() {
                    GreenFingers.sendInfo("Planted " +
                            "" + genPlant(hitLocation, plant) +
                            " " + plant.toString());
                }
            }.runTaskLater(instance, 1);
        }
    }

    /*@EventHandler
    public void onProjectileHit (ProjectileHitEvent e) {
        if (e.getEntityType() == EntityType.SPLASH_POTION) {
            final Material plant = GreenFingers.getTypeOfFlowerGas(projectileDictionary.get(e.getEntity()));   // planet would be the target planet type if the potion is a GreenFingers one
            if (plant != null && e.getHitBlock() != null) {                                           // | is it a GreenFingers item?
                final Location hitLocation = e.getHitBlock().getLocation();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        GreenFingers.sendInfo("Planted " +
                                "" + genPlant(hitLocation, plant) +
                                " " + plant.toString());
                    }
                }.runTaskLater(instance, 1);
                projectileDictionary.remove(e.getEntity().getEntityId());
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch (ProjectileLaunchEvent e) {
        ProjectileSource projectileSource = e.getEntity().getShooter();
        if (projectileSource instanceof Player) {
            Player player = (Player)projectileSource;
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (itemInMainHand != null) {
                if (itemInMainHand.getType() == Material.SPLASH_POTION) {
                    if (GreenFingers.getTypeOfFlowerGas(itemInMainHand) != null) {
                        projectileDictionary.put(e.getEntity(), itemInMainHand);
                    }
                }
            }
        }
        else if (projectileSource instanceof Dispenser) {

        }
    }
*/
    public static Integer genPlant(Location centerBlock, Material plant) {
        if (centerBlock == null || plant == null) {
            GreenFingers.sendWarn((centerBlock==null?"CenterBlock":"") + (plant==null?"Plant":"" + " = null"));
            return 0;
        }

        Integer radius = instance.getConfig().getInt("Settings.Garden.Gas.Radius");
        Integer flowerCount = 0;

        Integer bx = centerBlock.getBlockX();
        Integer by = centerBlock.getBlockY();
        Integer bz = centerBlock.getBlockZ();

        for(Integer x = bx - radius; x <= bx + radius; x++) {
            for(Integer y = by - radius; y <= by + radius; y++) {
                for(Integer z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if(distance < radius * radius) {

                        Location targetLocation = new Location(centerBlock.getWorld(), x, y, z);
                        if (targetLocation.getBlock().getType() == Material.GRASS_BLOCK) {
                            targetLocation.setY(targetLocation.getY() + 1);
                            Block targetBlock = targetLocation.getBlock();
                            if (targetBlock.getType() == Material.AIR) {
                                targetBlock.setType(plant);
                                flowerCount++;
                            }
                        }

                    }

                }
            }
        }

        return flowerCount;
    }

}
