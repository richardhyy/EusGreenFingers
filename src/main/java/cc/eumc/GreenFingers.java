package cc.eumc;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreenFingers extends JavaPlugin {

    public static Plugin instance;

    public void onEnable() {
        instance = this;

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            saveDefaultConfig();
        }

        reloadConfig();
        
        createRecipes();

        Bukkit.getPluginManager().registerEvents(new GreenFingersListener(),this);
        Bukkit.getPluginCommand("greenfingers").setExecutor(new GreenFingerCommandExecutor());

        sendInfo("Enabled");
    }

    public void onDisable() {
        sendInfo("Disabled");
    }

    private void createRecipes() {
        Configuration config = this.getConfig();

        for (String flowerName : config.getConfigurationSection("Settings.Garden.FlowerLocalization").getKeys(false)) {

            Material flower = flowerName.equalsIgnoreCase("MIXED")? Material.BONE_MEAL : Material.getMaterial(flowerName.toUpperCase());

            if (flower != null) {
                // Generate FlowerGas
                NamespacedKey key = new NamespacedKey(this, flowerName + "_gas");

                ItemStack gas = getFlowerGas(flowerName, 1);

                // Generate Recipe
                ShapedRecipe recipe = new ShapedRecipe(key, gas);
                recipe.shape(config.getString("Settings.Garden.Gas.Recipe.Line1"),
                            config.getString("Settings.Garden.Gas.Recipe.Line2"),
                            config.getString("Settings.Garden.Gas.Recipe.Line3"));
                for (String ingredient : config.getConfigurationSection("Settings.Garden.Gas.Recipe.Material").getKeys(false)) {
                    String ingredientName = config.getString("Settings.Garden.Gas.Recipe.Material." + ingredient).toUpperCase().replace("{FLOWER}", (flowerName.equalsIgnoreCase("MIXED")? Material.BONE_MEAL.name() : flowerName).toUpperCase());
                    Material ingredientMaterial = Material.getMaterial(ingredientName);
                    if (ingredientMaterial == null) {
                        sendSevere("Ingredient item " + config.getString(ingredientName + " does not exist!"));
                    }
                    else {
                        recipe.setIngredient( ingredient.charAt(0), ingredientMaterial);
                    }
                }
                Bukkit.addRecipe(recipe);

                sendInfo("√ Recipe: " + flowerName.toUpperCase());
            }
            else {
                sendWarn(flowerName.toUpperCase() + " does not exist!");
            }
        }
    }


    public static ItemStack getFlowerGas(String flowerName, Integer amount) {
        Configuration config = instance.getConfig();

        ItemStack gas = new ItemStack(Material.SPLASH_POTION);
        PotionMeta potionMeta = (PotionMeta) gas.getItemMeta();

        potionMeta.setDisplayName(config.getString("Settings.Garden.Gas.Name"));
        potionMeta.setLore(Arrays.asList(
                config.getString("Settings.Garden.Gas.Lore")
                        .replace("{FLOWERNAME}", config.getString("Settings.Garden.FlowerLocalization." + flowerName + ".Name")),
                "§7" + flowerName.toUpperCase()
        ));
        potionMeta.setColor(Color.fromBGR(
                 config.getInt("Settings.Garden.FlowerLocalization." + flowerName + ".Color.B", 103),
                config.getInt("Settings.Garden.FlowerLocalization." + flowerName + ".Color.G",255),
                config.getInt("Settings.Garden.FlowerLocalization." + flowerName + ".Color.R",175)) );

        gas.setItemMeta(potionMeta);

        gas.setAmount(amount > 0 ? amount : 1);

        return gas;
    }

    public static Material getTypeOfFlowerGas(ItemStack itemStack) {
        Configuration config = instance.getConfig();
        if (itemStack != null) {
            if (itemStack.getType() == Material.SPLASH_POTION) {
                PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
                if (itemMeta.getDisplayName().contains(config.getString("Settings.Garden.Gas.Name")) && itemMeta.hasLore()) {
                    List<String> lore = itemMeta.getLore();
                    if (lore.size() == 2) {
                        for (String flowerName : config.getConfigurationSection("Settings.Garden.FlowerLocalization").getKeys(false)) {
                            if (lore.indexOf("§7" + flowerName.toUpperCase()) != -1) {
                                return (flowerName.equalsIgnoreCase("MIXED")? Material.AIR : Material.getMaterial(flowerName.toUpperCase()));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<Material> getFlowerList() {
        List<Material> flowerList = new ArrayList<Material>();
        for (String flowerName : instance.getConfig().getConfigurationSection("Settings.Garden.FlowerLocalization").getKeys(false)) {
            if (!flowerName.equalsIgnoreCase("MIXED")) {
                Material _flower = Material.getMaterial(flowerName.toUpperCase());
                if (_flower != null) {
                    flowerList.add(_flower);
                }
            }
        }
        if (flowerList.size() == 0) {
            GreenFingers.sendSevere("Failed loading customized flower list.");
            return null;
        }
        return flowerList;
    }

    public static void sendSevere(String message) {
        Bukkit.getServer().getLogger().severe("[EucalyptusLeaves] [GreenFingers] " + message);
    }

    public static void sendWarn(String message) {
        Bukkit.getServer().getLogger().warning("[EucalyptusLeaves] [GreenFingers] " + message);
    }

    public static void sendInfo(String message) {
        Bukkit.getServer().getLogger().info("[EucalyptusLeaves] [GreenFingers] " + message);
    }
}
