package net.swade.chatgpt;

import cn.nukkit.Player;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.EventException;
import com.google.common.cache.*;
import net.swade.chatgpt.command.ChatCommand;
import net.swade.chatgpt.handler.ChatHandler;
import net.swade.chatgpt.handler.PlayerHandlers;
import net.swade.chatgpt.util.Messages;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class Main extends PluginBase {

    public static Cache<Player, StringBuilder> CACHE;
    public static Cache<Player, Type> USER_TYPE = CacheBuilder.newBuilder().build();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        OpenAI.init(getConfig().getString("API_KEY")).exceptionally(throwable -> {
            getLogger().error("Error while initializing OpenAI service! Is your API key valid?");
            throwable.printStackTrace();
            return null;
        });

        CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Player, StringBuilder>) notification -> {
                    if (notification.getKey() == null) return;
                    USER_TYPE.invalidate(notification.getKey());
                    if (notification.getCause() == RemovalCause.EXPIRED) {
                        notification.getKey().sendMessage(Messages.format(getConfig().getString("command.toggle.disabled")));
                    }
                }).build();

        String priority = getConfig().getString("chat-priority", "HIGH").toUpperCase();

        Class<PlayerChatEvent> eventClass = PlayerChatEvent.class;
        getServer().getPluginManager().registerEvent(eventClass, new ChatHandler(this), EventPriority.valueOf(priority), (listener, event) -> {
            try {
                listener.getClass().getMethod("onPlayerChat", eventClass).invoke(listener, event);
            } catch (InvocationTargetException ex) {
                throw new EventException(ex.getCause());
            } catch (Throwable t) {
                throw new EventException(t);
            }
        }, this);
        getServer().getPluginManager().registerEvents(new PlayerHandlers(), this);

        getLogger().info("Plugin enabled!");

        getServer().getCommandMap().register("chatgpt", new ChatCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}