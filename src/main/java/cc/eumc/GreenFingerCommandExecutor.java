package cc.eumc;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class GreenFingerCommandExecutor implements CommandExecutor {
    Plugin plugin = GreenFingers.instance;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("GreenFingers.get")) {
                if (args.length == 0) {
                    sender.sendMessage("§b§l[EusGreenFingers] /gf get <Plant Type>");
                    sender.sendMessage("§b§l[EusGreenFingers] /gf list");
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        sender.sendMessage("§b§l[EusGreenFingers] Reloaded");
                    }
                    else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("list")) {
                        for (Material flower : GreenFingers.getFlowerList()) {
                            String cmdStr = "/sn get " + flower.name();
                            sendCopyableMessage((Player)sender, "[EusGreenFingers] §b" + cmdStr, cmdStr);
                        }
                    }
                }
                else if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("get")) {
                        Integer amount = args.length==3? Integer.valueOf(args[2]) : 1;
                        String targetFlower = args[1];
                        ItemStack itemStack = null;
                        for (String flowerName : plugin.getConfig().getConfigurationSection("Settings.Garden.FlowerLocalization").getKeys(false)) {
                            if(flowerName.equalsIgnoreCase(targetFlower)) {
                                itemStack = GreenFingers.getFlowerGas(flowerName, amount); // Translate flowerName into a legal one
                                break;
                            }
                        }
                        if (itemStack == null) {
                            sender.sendMessage("§b§l[EusGreenFingers] §cNo such plant supported: " + targetFlower);
                            sender.sendMessage("§b§l[EusGreenFingers] §bType §b§l/gf list §bto access flower list.");
                        }
                        else {
                            ((Player)sender).getInventory().addItem(itemStack);
                            sender.sendMessage("§b§l[EusGreenFingers] " + "" + amount + " " + args[1] + " has been added to your inventory.");
                        }
                    }
                    else {
                        sender.sendMessage("§b§l[EusGreenFingers] /gf get <Plant Type>");
                        sender.sendMessage("§b§l[EusGreenFingers] /gf list");
                    }
                }
            }
        }
        else if (sender instanceof ConsoleCommandSender && args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage("§b§l[EusGreenFingers] Reloaded");
            }
        } else {
            sender.sendMessage("§b§l[EusGreenFingers] Invalid Operation");
        }
        return true;
    }

    private void sendMessage(Player player, TextComponent component) {
        player.spigot().sendMessage(component);
    }

    private void sendCopyableMessage(Player player, String message, String value) {
        BaseComponent[] clickToCopy =  new ComponentBuilder("Click to use").create();
        TextComponent workerMessage = new TextComponent(message);
        workerMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value));
        workerMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickToCopy));
        sendMessage(player, workerMessage);
    }
}
