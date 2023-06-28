package net.swade.chatgpt.handler;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import net.swade.chatgpt.Main;

public class PlayerHandlers implements Listener {

    @SuppressWarnings("unused")
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.CACHE.invalidate(e.getPlayer());
    }
}
