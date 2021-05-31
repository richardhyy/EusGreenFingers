package cc.eumc;

import cc.eumc.Polygon.StarPolygon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class GreenFingersListener implements Listener {
    static Plugin instance = GreenFingers.instance;

    @EventHandler
    public void onPotionSplash (PotionSplashEvent e) {
        // TODO Check if the player can build here
        final Location hitLocation = e.getEntity().getLocation();
        final Material plant = GreenFingers.getTypeOfFlowerGas(e.getPotion().getItem());   // planet would be the target planet type if the potion is a GreenFingers one
//        final Block blockBeneath = hitLocation.getWorld().getBlockAt(hitLocation.getBlockX(), hitLocation.getBlockY() - 1, hitLocation.getBlockZ());

        if (plant != null && hitLocation != null) {                                           // | is it a GreenFingers item?
            new BukkitRunnable() {
                @Override
                public void run() {
                    GreenFingers.sendInfo("Planted " +
                            "" + genPlant(hitLocation, plant) +
                            (plant.toString().equals("AIR") ?"MIX":plant.toString()) );
                }
            }.runTaskLater(instance, 1);
        }
    }

    public static int genPlant(Location centerBlock, Material plant) {
        if (centerBlock == null || plant == null) {
            GreenFingers.sendWarn((centerBlock==null?"CenterBlock":"") + (plant==null?"Plant":"" + " = null"));
            return 0;
        }

        int radius = instance.getConfig().getInt("Settings.Garden.Gas.Radius");
        int flowerCount = 0;

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        List<Material> flowerList = (plant == Material.AIR)? GreenFingers.getFlowerList() : null;
        if (plant == Material.AIR && flowerList == null) {
            return 0;
        }

        if (instance.getConfig().getString("Settings.Garden.Gas.Shape.Type").equalsIgnoreCase( "star")) {
            StarPolygon star = new StarPolygon(0, 0, radius, (int)(radius*0.618), instance.getConfig().getInt("Settings.Garden.Gas.Shape.VertexCount", 5));
            for (int y = (int)(centerBlock.getY() - (radius/2)); y <= (int)(centerBlock.getY() + (radius/2)); y++) {
                int i = 0; // index of xpoints
                for (Integer x : star.xpoints) {
                    Location targetLocation = new Location(centerBlock.getWorld(), bx + x, y, bz + star.ypoints[i]);
                    i++;
                    flowerCount += setPlantBlock(targetLocation, plant, flowerList)? 1 : 0;
                }
            }
        }
        else {
            for (int x = bx - radius; x <= bx + radius; x++) {
                for (int y = by - radius; y <= by + radius; y++) {
                    for (int z = bz - radius; z <= bz + radius; z++) {

                        double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));

                        if (distance < radius * radius) {

                            Location targetLocation = new Location(centerBlock.getWorld(), x, y, z);
                            flowerCount += setPlantBlock(targetLocation, plant, flowerList)? 1 : 0;

                        }

                    }
                }
            }
        }

        return flowerCount;
    }

    private static boolean setPlantBlock(Location targetLocation, Material plant, List<Material> flowerList) {
        if (targetLocation.getBlock().getType() == Material.GRASS_BLOCK) {
            targetLocation.setY(targetLocation.getY() + 1);
            Block targetBlock = targetLocation.getBlock();
            if (targetBlock.getType() == Material.AIR) {
                if (plant == Material.AIR) {    // is it a MIXED potion?
                    targetBlock.setType(flowerList.get(dice(0, flowerList.size() - 1)));
                } else {
                    targetBlock.setType(plant);
                }
                return true;
            }
        }
        return false;
    }

    private static int dice(int min, int max) {
        return (new Random().nextInt(max) % (max - min + 1) + min);
    }
}
