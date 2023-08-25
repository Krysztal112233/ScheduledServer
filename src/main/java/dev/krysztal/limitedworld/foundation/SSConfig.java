package dev.krysztal.limitedworld.foundation;

import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class SSConfig {
    List<ScheduledServerInfo> scheduledServerInfo = List.of(
            new ScheduledServerInfo("resource_world", 1, TimeUnit.HOURS, true),
            new ScheduledServerInfo("resource_nether", 1, TimeUnit.HOURS, true),
            new ScheduledServerInfo("resource_end", 1, TimeUnit.HOURS, true)
    );
    Message message = new Message();
    List<Player> playerWhiteList = List.of();

    @Data
    public static final class Message {
        String serverNotOpenMessage = "<red><bold>This is not the time for this server to be open.";
        String serverClosedMessage = "<red><bold>Server are not in the schedule to be turned on right now.";
    }

    @Data
    @AllArgsConstructor
    public static final class ScheduledServerInfo {
        String serverName;
        int duration;
        TimeUnit timeUnit;
        boolean enable = true;
    }

}
