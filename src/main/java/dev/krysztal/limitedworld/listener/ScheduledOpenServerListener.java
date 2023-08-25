package dev.krysztal.limitedworld.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.krysztal.limitedworld.ScheduledServer;
import dev.krysztal.limitedworld.foundation.api.ScheduledOpenServerEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ScheduledOpenServerListener {

    @Subscribe(order = PostOrder.FIRST)
    public void onServerScheduled(ScheduledOpenServerEvent event) {
        if (event.getCurrentServer() == null) return;

        Optional<RegisteredServer> currentServer = ScheduledServer
                .getInstance()
                .server
                .getServer(event.getCurrentServer().getServerName());

        for (String server : ScheduledServer.getInstance().server.getConfiguration().getAttemptConnectionOrder()) {
            Optional<RegisteredServer> attemptServer = ScheduledServer.getInstance().server.getServer(server);
            if (attemptServer.isEmpty()) continue;

            currentServer.ifPresent(registeredServer -> {
                registeredServer
                        .getPlayersConnected()
                        .forEach(player -> {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(ScheduledServer.getInstance().config.getMessage().getServerClosedMessage()));
                            ScheduledServer.getInstance().server.getScheduler().buildTask(ScheduledServer.getInstance(), () -> {
                                if (player.isActive())
                                    player.createConnectionRequest(attemptServer.get()).fireAndForget();
                            }).delay(5, TimeUnit.SECONDS).schedule();
                        });
            });
            return;
        }
        ScheduledServer.getInstance().logger.error("Cannot connect to any attempt server, will disconnect all players in server %s".formatted(event.getCurrentServer().getServerName()));
        currentServer.ifPresent(registeredServer -> registeredServer.getPlayersConnected().forEach(player -> player.disconnect(MiniMessage.miniMessage().deserialize(ScheduledServer.getInstance().config.getMessage().getServerClosedMessage()))));
    }
}
