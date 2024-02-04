package dev.simplix.cirrus.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.simplix.cirrus.velocity.commands.TestCommand;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Plugin(id = "cirrusplugin", name = "CirrusPlugin", version = "1.0", authors = {"Xefreh"})
public class CirrusExamplePlugin {
    private final ProxyServer proxyServer;

    private final Map<UUID, String> pendingWarps;

    private final MinecraftChannelIdentifier channelIdentifier;

    private static CirrusExamplePlugin instance;

    @Inject
    public CirrusExamplePlugin(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;
        this.pendingWarps = new ConcurrentHashMap<>();
        this.channelIdentifier = MinecraftChannelIdentifier.create("velocitygui", "warpchannel");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CirrusVelocity.init(this.proxyServer, this);
        this.proxyServer.getCommandManager().register("menu", new TestCommand(this.proxyServer));
        this.proxyServer.getEventManager().register(this, ServerConnectedEvent.class, this::onServerConnected);
        this.proxyServer.getChannelRegistrar().register(this.channelIdentifier);
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        handleWarpRequest(event.getPlayer(), playerId);
    }

    // 워프 요청을 처리하는 메소드
    private void handleWarpRequest(Player player, UUID playerId) {
        if (pendingWarps.containsKey(playerId)) {
            String warpName = pendingWarps.remove(playerId);
            sendWarpCommand(player, warpName);
        }
    }

    public void scheduleWarp(UUID playerId, String warpName) {
        if (warpName != null && !warpName.isEmpty()) {
            pendingWarps.put(playerId, warpName);
            // 플레이어가 이미 서버에 연결되어 있는 경우, 즉시 워프 처리를 시도합니다.
            Player player = proxyServer.getPlayer(playerId).orElse(null);
            if (player != null) {
                sendWarpCommand(player, warpName);
            }
        }
    }

    public static CirrusExamplePlugin getInstance() {
        return instance;
    }

    // 실제 워프를 수행하는 메소드
    public void sendWarpCommand(Player player, String warpName) {
        trySendWarpCommand(player, warpName, 5);
    }

    // 워프 요청 시도 + 횟수 조정 메소드
    private void trySendWarpCommand(Player player, String warpName, int attempt) {
        int maxRetries = 3;
        long retryDelay = 1000L;
        this.proxyServer.getScheduler().buildTask(this, () -> {
            Optional<ServerConnection> serverConnectionOpt = player.getCurrentServer();
            if (!serverConnectionOpt.isPresent()) {
                if (attempt < maxRetries) {
                    System.out.println("Server connection not present, retrying... Attempt: " + (attempt + 1));
                    trySendWarpCommand(player, warpName, attempt + 1);
                } else {
                    System.out.println("Failed to send warp command after " + maxRetries + " attempts.");
                }
            } else {
                ServerConnection serverConnection = serverConnectionOpt.get();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(player.getUsername());
                out.writeUTF(warpName);
                serverConnection.sendPluginMessage((ChannelIdentifier)this.channelIdentifier, out.toByteArray());
                System.out.println("Attempted to send messaging.");
            }
        }).delay(retryDelay, TimeUnit.MILLISECONDS).schedule();
    }
}