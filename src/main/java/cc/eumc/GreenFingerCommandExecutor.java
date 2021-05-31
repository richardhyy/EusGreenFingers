package cc.eumc;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GreenFingerCommandExecutor implements CommandExecutor, TabExecutor {
    GreenFingers plugin;
    private final String[] commands = {"list", "get"};
    private final String permissionNode = "GreenFingers.get";
    private String[] flowerTypes;

    public GreenFingerCommandExecutor(GreenFingers plugin) {
        this.plugin = plugin;
        this.flowerTypes = plugin.getFlowerList().stream()
                .map(Enum::name).toArray(String[]::new);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("GreenFingers.get")) {
                if (args.length == 0) {
                    sender.sendMessage("§b§l[GreenFingers] /gf get <Plant Type>");
                    sender.sendMessage("§b§l[GreenFingers] /gf list");
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        sender.sendMessage("§b§l[GreenFingers] Reloaded");
                    }
                    else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("list")) {
                        for (Material flower : plugin.getFlowerList()) {
                            String cmdStr = "/gf get " + flower.name();
                            sendCopyableMessage((Player)sender, "[GreenFingers] §b" + cmdStr, cmdStr);
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
                            sender.sendMessage("§b§l[GreenFingers] §cNo such plant supported: " + targetFlower);
                            sender.sendMessage("§b§l[GreenFingers] §bType §b§l/gf list §bto access flower list.");
                        }
                        else {
                            ((Player)sender).getInventory().addItem(itemStack);
                            sender.sendMessage("§b§l[GreenFingers] " + "" + amount + " " + args[1] + " has been added to your inventory.");
                        }
                    }
                    else {
                        sender.sendMessage("§b§l[GreenFingers] /gf get <Plant Type>");
                        sender.sendMessage("§b§l[GreenFingers] /gf list");
                    }
                }
            }
        }
        else if (sender instanceof ConsoleCommandSender && args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage("§b§l[GreenFingers] Reloaded");
            }
        } else {
            sender.sendMessage("§b§l[GreenFingers] Invalid Operation");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(permissionNode)) return new ArrayList<>();

        if (args.length > 2)
            return new ArrayList<>();
        else if (args.length == 2 && args[0].equalsIgnoreCase("get"))
            return Arrays.stream(flowerTypes).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        else if (args.length == 1)
            return Arrays.stream(commands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        else
            return Arrays.asList(commands);
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
