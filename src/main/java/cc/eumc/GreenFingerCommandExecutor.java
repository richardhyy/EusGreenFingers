package cc.eumc;

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
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        sender.sendMessage("§b§l[EusGreenFingers] Reloaded");
                    }
                }
                else if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("get")) {
                        Integer amount = args.length==3? Integer.valueOf(args[2]) : 1;
                        ItemStack itemStack = GreenFingers.getFlowerGas(args[1], amount);
                        if (itemStack == null) {
                            sender.sendMessage("§b§l[EusGreenFingers] §cNo such plant supported: " + args[1]);
                        }
                        else {
                            ((Player)sender).getInventory().addItem(itemStack);
                            sender.sendMessage("§b§l[EusGreenFingers] " + "" + amount + " " + args[1] + " has been added to your inventory.");
                        }
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
}
