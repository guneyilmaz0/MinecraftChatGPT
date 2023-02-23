package net.swade.chatgpt.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.swade.chatgpt.Main;
import net.swade.chatgpt.Type;
import net.swade.chatgpt.conversation.TypeManager;
import net.swade.chatgpt.util.Messages;

import java.util.Arrays;

public class ChatCommand extends Command {

    private final Main plugin;

    public ChatCommand(Main plugin) {
        super("chatgpt", "Chat GPT command");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length == 1 && sender.hasPermission("minecraftgpt.command.reload") && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aConfig reloaded!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        Type type = Type.SINGLE;
        if (args.length >= 1) {
            type = Type.getType(args[0]);
            if (type == null) {
                player.sendMessage(Messages.format(plugin.getConfig().getString("command.invalid-type"))
                        .replace("{types}", String.join(", ", Arrays.stream(Type.values()).map(Enum::name).toArray(String[]::new))));
                return true;
            }
        }

        if (!player.hasPermission("minecraftgpt.command." + type.name().toLowerCase())) {
            player.sendMessage(Messages.format(plugin.getConfig().getString("command.no-permission")));
            return true;
        }

        TypeManager.startConversation(plugin, player, type);
        return true;
    }
}
