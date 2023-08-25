package dev.krysztal.limitedworld.foundation.api;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.krysztal.limitedworld.ScheduledServer;
import dev.krysztal.limitedworld.foundation.SSConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
public class ScheduledOpenServerEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    @NotNull
    @Setter
    @Getter
    @Builder.Default
    private GenericResult result = GenericResult.allowed();

    @NotNull
    @Setter
    @Getter
    private SSConfig.ScheduledServerInfo nextServer;

    @Getter
    @Builder.Default
    @Nullable
    private SSConfig.ScheduledServerInfo currentServer = currentOpenedScheduledServer;

    @Getter
    @Nullable
    private static SSConfig.ScheduledServerInfo currentOpenedScheduledServer;

    public static void startScheduler(ProxyServer server) {
        currentOpenedScheduledServer = ScheduledServer.getInstance().config.getScheduledServerInfo().stream().findFirst().orElse(null);

        if (currentOpenedScheduledServer == null) {
            ScheduledServer.getInstance().logger.warn("Cannot find any server defined in config, the scheduled server will dont work.");
            return;
        }

        new Thread(() -> {
            while (true) {
                if (currentOpenedScheduledServer == null) continue;

                try {
                    // 休眠配置文件中的时间
                    currentOpenedScheduledServer.getTimeUnit().sleep(currentOpenedScheduledServer.getDuration());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                List<SSConfig.ScheduledServerInfo> scheduledServerInfo = ScheduledServer.getInstance().config.getScheduledServerInfo();
                SSConfig.ScheduledServerInfo next = scheduledServerInfo.get((scheduledServerInfo.indexOf(currentOpenedScheduledServer) + 1) % scheduledServerInfo.size());

                // 发动切换服务器
                server.getEventManager().fire(
                        ScheduledOpenServerEvent
                                .builder()
                                .currentServer(currentOpenedScheduledServer)
                                .nextServer(next)
                                .build()
                );
                currentOpenedScheduledServer = next;
            }
        }).start();
    }
}
