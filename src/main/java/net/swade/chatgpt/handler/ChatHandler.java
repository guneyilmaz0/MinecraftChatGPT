package net.swade.chatgpt.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import lombok.RequiredArgsConstructor;
import net.swade.chatgpt.Main;
import net.swade.chatgpt.OpenAI;
import net.swade.chatgpt.Type;
import net.swade.chatgpt.util.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChatHandler implements Listener {
    private final Main plugin;

    @SuppressWarnings("unused")
    @EventHandler
    public void onChat(PlayerChatEvent e){
        Player player = e.getPlayer();
        boolean hasFull = Main.USER_TYPE.asMap().values().stream().anyMatch(type -> type == Type.FULL);
        if (!Main.CACHE.asMap().containsKey(player) && !hasFull) {
            return;
        }

        Collection<Player> recipients = new ArrayList<>();
        switch (Main.USER_TYPE.asMap().getOrDefault(player, hasFull ? Type.FULL : Type.SINGLE)){
            case SINGLE:
                recipients = Collections.singletonList(player);
                break;
            case FULL:
            case BROADCAST:
                List<Player> list = new ArrayList<>();
                for (CommandSender recipient : e.getRecipients()) {
                    if (recipient instanceof Player){
                        list.add((Player) recipient);
                    }
                }
                recipients = list;
        }

        List<String> list = plugin.getConfig().getStringList("format");

        if (!plugin.getConfig().getBoolean("use-default-chat", false)) {
            e.setCancelled(true);

            sendMessage(format(list.get(0), e.getMessage(), player.getName()), recipients);
        }

        StringBuilder builder = Main.CACHE.getIfPresent(player);
        if (builder == null) builder = new StringBuilder();

        Collection<Player> finalRecipients = recipients;
        OpenAI.getResponse(plugin.getConfig().getSection("chatgpt"), builder, e.getMessage()).whenComplete((response, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                player.sendMessage(Messages.format(plugin.getConfig().getString("command.error")));
                return;
            }
            sendMessage(format(list.get(1), response, player.getName()), finalRecipients);
        });
    }

    private String format(String str, String message, String player) {
        return Messages.format(str).replace("%message%", message).replace("%player%", player);
    }

    private void sendMessage(String message, Collection<Player> players) {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (players.contains(player) && player.hasPermission("minecraftgpt.receive")){
                player.sendMessage(message);
            }
        }

        for (Player player : players)
            player.sendMessage(message);

        if (plugin.getConfig().getBoolean("send-messages-to-console", true))
            plugin.getServer().getConsoleSender().sendMessage(message);
    }
}
