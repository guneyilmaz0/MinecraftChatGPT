package net.swade.chatgpt;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import com.google.common.cache.*;
import net.swade.chatgpt.command.ChatCommand;
import net.swade.chatgpt.handler.PlayerHandlers;
import net.swade.chatgpt.util.Messages;

import java.util.concurrent.TimeUnit;

public class Main extends PluginBase {

    public static Cache<Player, StringBuilder> CACHE;
    public static Cache<Player, Type> USER_TYPE;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        OpenAI.init(getConfig().getString("API_KEY"));

        CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Player, StringBuilder>) notification -> {
                    if (notification.getKey() == null) {
                        return;
                    }
                    USER_TYPE.invalidate(notification.getKey());
                    if (notification.getCause() == RemovalCause.EXPIRED) {
                        notification.getKey().sendMessage(Messages.format(getConfig().getString("command.toggle.disabled")));
                    }
                }).build();
        USER_TYPE = CacheBuilder.newBuilder().build();

        getServer().getPluginManager().registerEvents(new PlayerHandlers(this), this);

        getLogger().info("Plugin enabled!");

        getServer().getCommandMap().register("chatgpt", new ChatCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}