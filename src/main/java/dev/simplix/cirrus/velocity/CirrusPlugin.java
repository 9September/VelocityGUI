package dev.simplix.cirrus.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.simplix.cirrus.common.Cirrus;
import dev.simplix.cirrus.common.business.InventoryMenuItemWrapper;
import dev.simplix.cirrus.common.business.MenuItemWrapper;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.converter.Converters;
import dev.simplix.cirrus.common.effect.MenuAnimator;
import dev.simplix.cirrus.common.item.CirrusItem;
import dev.simplix.cirrus.common.menu.MenuBuilder;
import dev.simplix.cirrus.velocity.commands.TestCommand;
import dev.simplix.cirrus.velocity.converters.ItemModelConverter;
import dev.simplix.cirrus.velocity.converters.ItemStackConverter;
import dev.simplix.cirrus.velocity.converters.PlayerConverter;
import dev.simplix.cirrus.velocity.converters.PlayerUniqueIdConverter;
import dev.simplix.cirrus.velocity.listener.QuitListener;
import dev.simplix.cirrus.velocity.protocolize.ProtocolizeMenuBuilder;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "cirrusplugin",
        name = "CirrusPlugin",
        version = "1.0",
        authors = "Xefreh"
)

public class CirrusPlugin {

    private final ProxyServer proxyServer;
    private final Map<UUID, String> pendingWarps;
    private final MinecraftChannelIdentifier channelIdentifier;

    private static CirrusPlugin instance;

    @Inject
    public CirrusPlugin(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;
        this.pendingWarps = new ConcurrentHashMap<>();
        this.channelIdentifier = MinecraftChannelIdentifier.create("velocitygui","warpchannel");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (proxyServer != null) {
            return;
        }

        Cirrus.registerService(MenuBuilder.class, new ProtocolizeMenuBuilder());


        // Players
        Converters.register(Player.class, PlayerWrapper.class, new PlayerConverter());
        Converters.register(UUID.class, PlayerWrapper.class, new PlayerUniqueIdConverter(proxyServer));

        // Items
        Converters.register(ItemStack.class, MenuItemWrapper.class, new ItemStackConverter());
        Converters.register(CirrusItem.class, InventoryMenuItemWrapper.class, new ItemModelConverter());

        proxyServer.getScheduler().buildTask(this, () -> {
            if (proxyServer.getPlayerCount() > 0 && !MenuAnimator.isEmpty()) {
                MenuAnimator.updateAll();
            }
        }).repeat(50 * 2, TimeUnit.MILLISECONDS).schedule();

        proxyServer.getChannelRegistrar().register(channelIdentifier);

        proxyServer.getCommandManager().register("메뉴",new TestCommand(proxyServer));
        proxyServer.getEventManager().register(this, new QuitListener());
        proxyServer.getEventManager().register(this, ServerConnectedEvent.class, this::onServerConnected);

    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (pendingWarps.containsKey(playerId)) {
            String warpName = pendingWarps.remove(playerId);
            if (warpName != null && !warpName.isEmpty()) {
                sendWarpCommand(event.getPlayer(), warpName);
            }
        }
    }

    public void scheduleWarp(Player player, String warpName) {
        if (warpName != null && !warpName.isEmpty()) {
            pendingWarps.put(player.getUniqueId(), warpName);
        }
    }

    public static CirrusPlugin getInstance() {
        return instance;
    }

    public boolean sendWarpCommand(Player player, String warpName) {
        Optional<ServerConnection> serverConnectionOpt = player.getCurrentServer();
        if (serverConnectionOpt.isPresent()) {
            ServerConnection serverConnection = serverConnectionOpt.get();

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(player.getUsername()); // 플레이어 이름
            out.writeUTF(warpName); // 워프 명령

            return serverConnection.sendPluginMessage(channelIdentifier, out.toByteArray());
        }
        return false;
    }
}
