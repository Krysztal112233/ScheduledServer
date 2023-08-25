package dev.krysztal.limitedworld.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import dev.krysztal.limitedworld.ScheduledServer;
import dev.krysztal.limitedworld.foundation.SSConfig;
import dev.krysztal.limitedworld.foundation.api.ScheduledOpenServerEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class ServerJoinListener {

    @Subscribe
    public void onPlayerTryJoin(ServerPreConnectEvent event) {
        event.getResult().getServer().ifPresent(registeredServer -> {
            String targetServerName = registeredServer.getServerInfo().getName();
            SSConfig.ScheduledServerInfo serverInfo = ScheduledOpenServerEvent.getCurrentOpenedScheduledServer();

            List<String> scheduledServer = ScheduledServer.getInstance().config.getScheduledServerInfo().stream().map(SSConfig.ScheduledServerInfo::getServerName).toList();
            if (serverInfo == null) return;

            if (scheduledServer.contains(targetServerName) && !serverInfo.getServerName().equals(targetServerName)) {
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(ScheduledServer.getInstance().config.getMessage().getServerNotOpenMessage()));
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }

        });
    }
}
