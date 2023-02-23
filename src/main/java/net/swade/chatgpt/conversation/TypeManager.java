package net.swade.chatgpt.conversation;

import cn.nukkit.Player;
import net.swade.chatgpt.Main;
import net.swade.chatgpt.Type;
import net.swade.chatgpt.util.Messages;

public class TypeManager {

    public static void startConversation(Main plugin, Player player, Type type) {
        if (Main.CACHE.asMap().containsKey(player)) {
            Main.CACHE.invalidate(player);
            player.sendMessage(Messages.format(plugin.getConfig().getString("command.toggle.disabled")));
            return;
        }

        Main.USER_TYPE.put(player, type);
        Main.CACHE.put(player, new StringBuilder());
        player.sendMessage(Messages.format(plugin.getConfig().getString("command.toggle.enabled")));
    }
}
