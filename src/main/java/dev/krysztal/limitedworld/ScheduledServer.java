package dev.krysztal.limitedworld;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.krysztal.limitedworld.foundation.SSConfig;
import dev.krysztal.limitedworld.foundation.api.ScheduledOpenServerEvent;
import dev.krysztal.limitedworld.listener.ScheduledOpenServerListener;
import dev.krysztal.limitedworld.listener.ServerJoinListener;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "scheduled_server",
        name = "ScheduledServer",
        version = "0.0.1",
        authors = {"Krysztal112233 <suibing12233@outlook.com>"}
)
public class ScheduledServer {

    public final Logger logger;
    public final ProxyServer server;
    public final Path dataDirectory;
    public final Metrics.Factory metricsFactory;
    public SSConfig config;

    private static ScheduledServer instance = null;

    @Inject
    public ScheduledServer(
            Logger logger,
            ProxyServer server,
            @DataDirectory
            Path dataDirectory,
            Metrics.Factory metricsFactory
    ) {
        instance = this;
        this.logger = logger;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;

        try {
            if (!dataDirectory.toFile().exists()) dataDirectory.toFile().mkdirs();
            File file = new File(dataDirectory.toAbsolutePath().toFile().toPath().toString(), "config.toml");
            if (!file.exists()) {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                new TomlWriter().write(new SSConfig(), writer);
                writer.close();
            }
            this.config = new Toml().read(new FileInputStream(file)).to(SSConfig.class);
        } catch (IOException e) {
            System.out.println(e);
            this.config = new SSConfig();
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.metricsFactory.make(this, 19616);

        this.server.getEventManager().register(this, new ServerJoinListener());
        this.server.getEventManager().register(this, new ScheduledOpenServerListener());

        ScheduledOpenServerEvent.startScheduler(this.server);
    }


    public static ScheduledServer getInstance() {
        return instance;
    }
}
