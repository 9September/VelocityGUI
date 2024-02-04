package dev.simplix.cirrus.velocity.menus;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
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


public class WarpMenu extends SimpleMenu {

    private final ProxyServer proxyServer;

    public WarpMenu(PlayerWrapper player, MenuConfiguration configuration, ProxyServer proxyServer) {
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

        registerActionHandler("core", click -> {

        });

        registerActionHandler("field", click -> {
            player().closeInventory();
            new FieldMenu(player, Cirrus.configurationFactory().loadFile("plugins/VelocityGUI/menu_field.json"), proxyServer).open();
            return CallResult.DENY_GRABBING;
        });

        registerActionHandler("store", click -> {
            player().closeInventory();
            new StoreMenu(player, Cirrus.configurationFactory().loadFile("plugins/VelocityGUI/menu_store.json"), proxyServer, CirrusExamplePlugin.getInstance()).open();
            return CallResult.DENY_GRABBING;
        });

        registerActionHandler("quest", click -> {

        });

        registerActionHandler("pass", click -> {

        });


    }

}
