package dev.simplix.cirrus.velocity.menus;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.configuration.MenuConfiguration;
import dev.simplix.cirrus.common.menus.SimpleMenu;
import dev.simplix.cirrus.common.model.CallResult;
import net.md_5.bungee.api.ChatColor;

import java.util.Locale;
import java.util.Optional;

public class FieldMenu extends SimpleMenu {

    private final ProxyServer proxyServer;

    public FieldMenu(PlayerWrapper player, MenuConfiguration configuration, ProxyServer proxyServer) {
        super(player, configuration, Locale.ENGLISH);
        this.proxyServer = proxyServer;


        registerActionHandler("monster_field", click -> {
            Player target = player().handle();
            Optional<RegisteredServer> server = proxyServer.getServer("Field");
            if (server.isPresent()) {
                server.get().ping().thenAccept(ping -> target.createConnectionRequest(server.get()).connect());
                player().sendMessage(ChatColor.GRAY + "몬스터 필드로 이동했습니다.");
            }
            player().closeInventory();
            return CallResult.DENY_GRABBING;
        });

        registerActionHandler("wild_field", click -> {
            Player target = player().handle();
            Optional<RegisteredServer> server = proxyServer.getServer("Wild");
            if (server.isPresent()) {
                server.get().ping().thenAccept(ping -> target.createConnectionRequest(server.get()).connect());
                player().sendMessage(ChatColor.GRAY + "야생 필드로 이동했습니다.");
            }
            player().closeInventory();
            return CallResult.DENY_GRABBING;
        });
    }
}