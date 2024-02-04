package dev.simplix.cirrus.velocity.menus;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.cirrus.common.Cirrus;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.configuration.MenuConfiguration;
import dev.simplix.cirrus.common.menus.SimpleMenu;
import dev.simplix.cirrus.common.model.CallResult;

import dev.simplix.cirrus.velocity.CirrusExamplePlugin;
import net.md_5.bungee.api.ChatColor;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class StoreMenu extends SimpleMenu {

    private final ProxyServer proxyServer;
    private final CirrusExamplePlugin cirrusExamplePlugin;

    public StoreMenu(PlayerWrapper player, MenuConfiguration configuration, ProxyServer proxyServer, CirrusExamplePlugin plugin) {
        super(player, configuration, Locale.ENGLISH);
        this.proxyServer = proxyServer;
        this.cirrusExamplePlugin = plugin;

        //시장가
        registerActionHandler("market_store", click -> {
            Player target = player().handle();
            UUID playerUUID = player().uniqueId();
            player().closeInventory();
            player().sendMessage(ChatColor.GRAY + "해당 워프로 이동중...");

            Optional<ServerConnection> currentServer = target.getCurrentServer();
            Optional<RegisteredServer> destinationServer = proxyServer.getServer("Spawn");

            if (destinationServer.isPresent()) {
                if (currentServer.isPresent() && currentServer.get().equals(destinationServer.get())) {
                    //이미 같은 서버에 있으므로 바로 워프 명령 예약
                    cirrusExamplePlugin.scheduleWarp(playerUUID,"market");
                } else {
                    //다른 서버에 있으므로 서버 이동 후 워프 명령 예약
                    destinationServer.get().ping().thenAccept(ping -> {
                        target.createConnectionRequest(destinationServer.get()).connect();
                    });
                    cirrusExamplePlugin.scheduleWarp(playerUUID,"market");
                }
            }
            return CallResult.DENY_GRABBING;
        });




        //유저상점
        registerActionHandler("user_store", click -> {
            player().closeInventory();
            new UserStoreMenu(player, Cirrus.configurationFactory().loadFile("plugins/VelocityGUI/menu_user_store.json"), proxyServer).open();
            return CallResult.DENY_GRABBING;
        });




        //경매소
        registerActionHandler("auction_store", click -> {
            Player target = player().handle();
            UUID playerUUID = player().uniqueId();
            player().closeInventory();
            player().sendMessage(ChatColor.GRAY + "해당 워프로 이동중...");

            Optional<ServerConnection> currentServer = target.getCurrentServer();
            Optional<RegisteredServer> destinationServer = proxyServer.getServer("Spawn");

            if (destinationServer.isPresent()) {
                if (currentServer.isPresent() && currentServer.get().equals(destinationServer.get())) {
                    cirrusExamplePlugin.scheduleWarp(playerUUID,"auction");
                } else {
                    destinationServer.get().ping().thenAccept(ping -> {
                        target.createConnectionRequest(destinationServer.get()).connect();
                    });
                    cirrusExamplePlugin.scheduleWarp(playerUUID,"auction");
                }
            }
            return CallResult.DENY_GRABBING;
        });
    }
}
