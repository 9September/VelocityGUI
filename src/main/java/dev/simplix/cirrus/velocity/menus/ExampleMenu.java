package dev.simplix.cirrus.velocity.menus;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.configuration.MenuConfiguration;
import dev.simplix.cirrus.common.menus.SimpleMenu;
import dev.simplix.cirrus.common.model.CallResult;
import dev.simplix.cirrus.velocity.converters.PlayerConverter;
import net.md_5.bungee.api.ChatColor;

import java.util.Locale;
import java.util.Optional;


public class ExampleMenu extends SimpleMenu {

    private final ProxyServer proxyServer;

    public ExampleMenu(PlayerWrapper player, MenuConfiguration configuration, ProxyServer proxyServer) {
        super(player, configuration, Locale.ENGLISH);
        this.proxyServer = proxyServer;


        registerActionHandler("spawn", click -> {
            Player target = player().handle();
            Optional<RegisteredServer> server = proxyServer.getServer("Spawn");
            if (server.isPresent()) {
                server.get().ping().thenAccept(ping->target.createConnectionRequest(server.get()).connect());
                player().sendMessage(ChatColor.GRAY+"스폰으로 이동했습니다.");
            }
            player().closeInventory();
            return CallResult.DENY_GRABBING;
        });


    }

}
